package com.eventmanager.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eventmanager.app.models.Event;
import com.eventmanager.app.repository.EventRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository repository;

    private final MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private final MutableLiveData<List<Event>> featuredEvents = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private final Set<String> favoriteIds = new HashSet<>();
    private List<Event> allEvents = new ArrayList<>();
    private String currentCategory = null;

    public EventViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
        loadFeaturedEvents();
        loadEvents(null);
    }

    // ─── Getters LiveData ────────────────────────────────────────────────────

    public LiveData<List<Event>> getEvents() { return events; }
    public LiveData<List<Event>> getFeaturedEvents() { return featuredEvents; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    // ─── Loading ──────────────────────────────────────────────────────────────

    public void loadEvents(String category) {
        currentCategory = category;
        isLoading.setValue(true);

        repository.getEvents(category, null, new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> result) {
                applyFavorites(result);
                allEvents = result;
                events.postValue(result);
                isLoading.postValue(false);
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
                applyFavorites(result);
                featuredEvents.postValue(result);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }
        });
    }

    public void refresh() {
        loadFeaturedEvents();
        loadEvents(currentCategory);
    }

    // ─── Filtering ────────────────────────────────────────────────────────────

    public void filterByCategory(String category) {
        loadEvents(category);
    }

    public void search(String query) {
        isLoading.setValue(true);
        repository.getEvents(currentCategory, query, new EventRepository.EventListCallback() {
            @Override
            public void onSuccess(List<Event> result) {
                applyFavorites(result);
                events.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
                isLoading.postValue(false);
            }
        });
    }

    // ─── Favorites ────────────────────────────────────────────────────────────

    public void toggleFavorite(Event event) {
        if (favoriteIds.contains(event.getId())) {
            favoriteIds.remove(event.getId());
            event.setFavorite(false);
        } else {
            favoriteIds.add(event.getId());
            event.setFavorite(true);
        }

        // Re-emit lists to trigger UI refresh
        if (allEvents != null) {
            applyFavorites(allEvents);
            events.setValue(allEvents);
        }
        if (featuredEvents.getValue() != null) {
            applyFavorites(featuredEvents.getValue());
            featuredEvents.setValue(featuredEvents.getValue());
        }
    }

    private void applyFavorites(List<Event> list) {
        for (Event e : list) {
            e.setFavorite(favoriteIds.contains(e.getId()));
        }
    }
}