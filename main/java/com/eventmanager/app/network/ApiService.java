package com.eventmanager.app.network;

import com.eventmanager.app.models.Event;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("events")
    Call<List<Event>> getEvents(
            @Query("category") String category,
            @Query("search") String search
    );

    @GET("events/featured")
    Call<List<Event>> getFeaturedEvents();

    @GET("events/{id}")
    Call<Event> getEventById(@Path("id") String eventId);
}