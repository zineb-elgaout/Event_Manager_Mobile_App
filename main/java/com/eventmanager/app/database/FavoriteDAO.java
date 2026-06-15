package com.eventmanager.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eventmanager.app.models.Event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoriteDAO {

    private final DatabaseHelper dbHelper;

    public FavoriteDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void addFavorite(Event event, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("event_id",     event.getId());
        values.put("user_id",      userId);
        values.put("title",        event.getTitle());
        values.put("date",         event.getDate());
        values.put("location",     event.getLocation());
        values.put("image_url",    event.getImageUrl());
        values.put("price",        event.getPrice());
        values.put("category",     event.getCategory());
        values.put("description",  event.getDescription());
        values.put("organizer",    event.getOrganizer());
        values.put("latitude",     event.getLatitude());
        values.put("longitude",    event.getLongitude());
        values.put("available_seats", event.getAvailableSeats());

        long result = db.insertWithOnConflict(
                DatabaseHelper.TABLE_FAVORITES, null, values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
        android.util.Log.d("FAV_DAO", "insert result=" + result
                + " | event=" + event.getId()
                + " | user=" + userId);
    }

    public void removeFavorite(String eventId, String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DatabaseHelper.TABLE_FAVORITES,
                "event_id=? AND user_id=?",
                new String[]{eventId, userId});
        android.util.Log.d("FAV_DAO", "remove rows=" + rows);
    }

    public boolean isFavorite(String eventId, String userId) {
        if (eventId == null || userId == null || userId.isEmpty()) return false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FAVORITES,
                new String[]{"event_id"},
                "event_id=? AND user_id=?",
                new String[]{eventId, userId},
                null, null, null
        );
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public List<Event> getFavorites(String userId) {
        List<Event> result = new ArrayList<>();
        if (userId == null || userId.isEmpty()) return result;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FAVORITES, null,
                "user_id=?", new String[]{userId},
                null, null, "date ASC"
        );

        android.util.Log.d("FAV_DAO", "getFavorites count=" + cursor.getCount()
                + " userId=" + userId);

        while (cursor.moveToNext()) {
            Event event = new Event();
            event.setId(safeGetString(cursor, "event_id"));
            event.setTitle(safeGetString(cursor, "title"));
            event.setDate(safeGetLong(cursor, "date"));
            event.setLocation(safeGetString(cursor, "location"));
            event.setImageUrl(safeGetString(cursor, "image_url"));
            event.setPrice(safeGetDouble(cursor, "price"));
            event.setCategory(safeGetString(cursor, "category"));
            event.setDescription(safeGetString(cursor, "description"));
            event.setOrganizer(safeGetString(cursor, "organizer"));
            event.setLatitude(safeGetDouble(cursor, "latitude"));
            event.setLongitude(safeGetDouble(cursor, "longitude"));
            event.setAvailableSeats(safeGetInt(cursor, "available_seats"));
            event.setFavorite(true);
            result.add(event);
        }
        cursor.close();
        return result;
    }

    public Set<String> getFavoriteIds(String userId) {
        Set<String> ids = new HashSet<>();
        if (userId == null || userId.isEmpty()) return ids;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FAVORITES,
                new String[]{"event_id"},
                "user_id=?", new String[]{userId},
                null, null, null
        );
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(0));
        }
        cursor.close();
        return ids;
    }

    public int countFavorites(String userId) {
        if (userId == null || userId.isEmpty()) return 0;
        return getFavoriteIds(userId).size();
    }

    // ─── Safe cursor getters ──────────────────────────────────────────────────

    private String safeGetString(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return idx >= 0 ? cursor.getString(idx) : null;
    }

    private long safeGetLong(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return idx >= 0 ? cursor.getLong(idx) : 0L;
    }

    private double safeGetDouble(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return idx >= 0 ? cursor.getDouble(idx) : 0.0;
    }

    private int safeGetInt(Cursor cursor, String column) {
        int idx = cursor.getColumnIndex(column);
        return idx >= 0 ? cursor.getInt(idx) : 0;
    }
}