package com.eventmanager.app.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.eventmanager.app.network.ApiClient;
import com.eventmanager.app.network.ApiService;
import com.eventmanager.app.models.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventRepository {

    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onError(String message);
    }

    public interface EventCallback {
        void onSuccess(Event event);
        void onError(String message);
    }

    private final ApiService apiService;

    public EventRepository(Context context) {
        apiService = ApiClient.getApiService(context);
    }

    public void getEvents(String category, String search, EventListCallback callback) {
        apiService.getEvents(category, search).enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erreur lors du chargement des événements.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Erreur réseau.");
            }
        });
    }

    public void getFeaturedEvents(EventListCallback callback) {
        apiService.getFeaturedEvents().enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NonNull Call<List<Event>> call, @NonNull Response<List<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Erreur lors du chargement des événements à la une.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Event>> call, @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Erreur réseau.");
            }
        });
    }

    public void getEventById(String id, EventCallback callback) {
        apiService.getEventById(id).enqueue(new Callback<Event>() {
            @Override
            public void onResponse(@NonNull Call<Event> call, @NonNull Response<Event> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Événement introuvable.");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Event> call, @NonNull Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Erreur réseau.");
            }
        });
    }
}