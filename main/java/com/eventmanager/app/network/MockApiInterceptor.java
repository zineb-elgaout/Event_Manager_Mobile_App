package com.eventmanager.app.network;

import android.content.Context;

import androidx.annotation.NonNull;

import com.eventmanager.app.R;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.models.EventsResponse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Intercepteur qui simule un backend en lisant res/raw/mock_events.json
 * Intercepte toutes les requêtes vers BASE_URL et renvoie des données locales.
 *
 * Endpoints simulés :
 *  - GET events?category=X&search=Y
 *  - GET events/featured
 *  - GET events/{id}
 */
public class MockApiInterceptor implements Interceptor {

    private final Context context;
    private final Gson gson = new Gson();
    private EventsResponse cachedResponse;

    public MockApiInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url();
        List<String> pathSegments = url.pathSegments();

        EventsResponse data = loadMockData();
        String json;

        // GET events/featured
        if (pathSegments.size() >= 2
                && pathSegments.get(pathSegments.size() - 2).equals("events")
                && pathSegments.get(pathSegments.size() - 1).equals("featured")) {

            List<Event> featured = new ArrayList<>();
            for (Event e : data.getEvents()) {
                if (data.getFeaturedIds().contains(e.getId())) {
                    featured.add(e);
                }
            }
            json = gson.toJson(featured);
        }
        // GET events/{id}
        else if (pathSegments.size() >= 2
                && pathSegments.get(pathSegments.size() - 2).equals("events")
                && !pathSegments.get(pathSegments.size() - 1).isEmpty()) {

            String eventId = pathSegments.get(pathSegments.size() - 1);
            Event found = null;
            for (Event e : data.getEvents()) {
                if (e.getId().equals(eventId)) {
                    found = e;
                    break;
                }
            }
            json = found != null ? gson.toJson(found) : "null";
        }
        // GET events?category=X&search=Y
        else {
            String category = url.queryParameter("category");
            String search = url.queryParameter("search");

            List<Event> filtered = new ArrayList<>();
            for (Event e : data.getEvents()) {
                boolean matchCategory = category == null || category.isEmpty()
                        || e.getCategory().equalsIgnoreCase(category);
                boolean matchSearch = search == null || search.isEmpty()
                        || e.getTitle().toLowerCase().contains(search.toLowerCase())
                        || e.getLocation().toLowerCase().contains(search.toLowerCase());

                if (matchCategory && matchSearch) {
                    filtered.add(e);
                }
            }
            json = gson.toJson(filtered);
        }

        // Simulate small network delay
        try {
            Thread.sleep(400);
        } catch (InterruptedException ignored) {}

        ResponseBody body = ResponseBody.create(
                json, MediaType.parse("application/json")
        );

        return new Response.Builder()
                .code(200)
                .message("OK (mocked)")
                .protocol(Protocol.HTTP_1_1)
                .request(request)
                .body(body)
                .addHeader("content-type", "application/json")
                .build();
    }

    private EventsResponse loadMockData() {
        if (cachedResponse != null) return cachedResponse;

        try (InputStream is = context.getResources().openRawResource(R.raw.mock_events)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            cachedResponse = gson.fromJson(sb.toString(), EventsResponse.class);
            return cachedResponse;
        } catch (Exception e) {
            EventsResponse empty = new EventsResponse();
            empty.setEvents(new ArrayList<>());
            empty.setFeaturedIds(new ArrayList<>());
            return empty;
        }
    }
}