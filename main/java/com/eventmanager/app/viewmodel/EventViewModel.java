package com.eventmanager.app.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eventmanager.app.database.FavoriteDAO;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.repository.EventRepository;
import com.eventmanager.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository  repository;
    private final FavoriteDAO      favoriteDAO;
    private final PreferenceManager preferenceManager;
    private final ExecutorService  executor = Executors.newSingleThreadExecutor();
    private final Handler          mainHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<List<Event>> events         = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> featuredEvents = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> favoriteEvents = new MutableLiveData<>();
    private final MutableLiveData<Boolean>     isLoading      = new MutableLiveData<>(false);
    private final MutableLiveData<String>      errorMessage   = new MutableLiveData<>();

    private List<Event> allEvents = new ArrayList<>();
    private String currentCategory = null;

    public EventViewModel(@NonNull Application application) {
        super(application);
        repository        = new EventRepository(application);
        favoriteDAO       = new FavoriteDAO(application);
        preferenceManager = new PreferenceManager(application);

        loadFeaturedEvents();
        loadEvents(null);
    }

    private String getUserId() {
        return preferenceManager.getUserId();
    }

    // ─── Getters ─────────────────────────────────────────────────────────────
    public LiveData<List<Event>> getEvents()         { return events; }
    public LiveData<List<Event>> getFeaturedEvents() { return featuredEvents; }
    public LiveData<List<Event>> getFavoriteEvents() { return favoriteEvents; }
    public LiveData<Boolean>     getIsLoading()      { return isLoading; }
    public LiveData<String>      getErrorMessage()   { return errorMessage; }

    // ─── Load events ──────────────────────────────────────────────────────────
    public void loadEvents(String category) {
        currentCategory = category;
        isLoading.setValue(true);

        repository.getEvents(category, null, new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> result) {
                executor.execute(() -> {
                    applyFavorites(result);
                    allEvents = new ArrayList<>(result);
                    events.postValue(result);
                    isLoading.postValue(false);
                });
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    public void loadFeaturedEvents() {
        repository.getFeaturedEvents(new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> result) {
                executor.execute(() -> {
                    applyFavorites(result);
                    featuredEvents.postValue(result);
                });
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void loadFavorites() {
        executor.execute(() -> {
            String uid = getUserId();
            android.util.Log.d("FAV_VM", "loadFavorites uid=" + uid);
            if (uid == null || uid.isEmpty()) {
                favoriteEvents.postValue(new ArrayList<>());
                return;
            }
            List<Event> favs = favoriteDAO.getFavorites(uid);
            android.util.Log.d("FAV_VM", "loadFavorites count=" + favs.size());
            favoriteEvents.postValue(favs);
        });
    }

    public void refresh() {
        loadFeaturedEvents();
        loadEvents(currentCategory);
    }

    public void filterByCategory(String category) {
        loadEvents(category);
    }

    public void search(String query) {
        isLoading.setValue(true);
        repository.getEvents(currentCategory, query, new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> result) {
                executor.execute(() -> {
                    applyFavorites(result);
                    events.postValue(result);
                    isLoading.postValue(false);
                });
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    // ─── Toggle favori ────────────────────────────────────────────────────────
    public void toggleFavorite(Event event) {
        executor.execute(() -> {
            String uid = getUserId();
            android.util.Log.d("FAV_VM", "toggle: " + event.getId() + " uid=" + uid);

            if (uid == null || uid.isEmpty()) {
                android.util.Log.e("FAV_VM", "userId vide");
                return;
            }
            if (event.getId() == null || event.getId().isEmpty()) {
                android.util.Log.e("FAV_VM", "eventId vide");
                return;
            }

            boolean wasFav = favoriteDAO.isFavorite(event.getId(), uid);
            if (wasFav) {
                favoriteDAO.removeFavorite(event.getId(), uid);
                event.setFavorite(false);
                android.util.Log.d("FAV_VM", "Supprimé");
            } else {
                favoriteDAO.addFavorite(event, uid);
                event.setFavorite(true);
                android.util.Log.d("FAV_VM", "Ajouté");
            }

            // Recharge et réémet toutes les listes
            applyFavorites(allEvents);
            events.postValue(new ArrayList<>(allEvents));

            List<Event> featured = featuredEvents.getValue();
            if (featured != null) {
                applyFavorites(featured);
                featuredEvents.postValue(new ArrayList<>(featured));
            }

            List<Event> favs = favoriteDAO.getFavorites(uid);
            favoriteEvents.postValue(favs);
        });
    }

    private void applyFavorites(List<Event> list) {
        if (list == null) return;
        String uid = getUserId();
        if (uid == null || uid.isEmpty()) return;
        Set<String> favIds = favoriteDAO.getFavoriteIds(uid);
        for (Event e : list) {
            e.setFavorite(favIds.contains(e.getId()));
        }
    }

    public int getFavoriteCount() {
        String uid = getUserId();
        if (uid == null || uid.isEmpty()) return 0;
        return favoriteDAO.countFavorites(uid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}