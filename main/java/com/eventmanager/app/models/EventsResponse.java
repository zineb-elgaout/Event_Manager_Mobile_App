package com.eventmanager.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EventsResponse {

    @SerializedName("events")
    private List<Event> events;

    @SerializedName("featured_ids")
    private List<String> featuredIds;

    public List<Event> getEvents() { return events; }
    public void setEvents(List<Event> events) { this.events = events; }

    public List<String> getFeaturedIds() { return featuredIds; }
    public void setFeaturedIds(List<String> featuredIds) { this.featuredIds = featuredIds; }
}