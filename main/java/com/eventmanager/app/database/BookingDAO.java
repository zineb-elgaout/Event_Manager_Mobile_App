package com.eventmanager.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eventmanager.app.models.Booking;

import java.util.ArrayList;
import java.util.List;

import static com.eventmanager.app.database.DatabaseHelper.*;

public class BookingDAO {

    private final DatabaseHelper dbHelper;

    public BookingDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(Booking booking) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, booking.getId());
        values.put(COL_EVENT_ID, booking.getEventId());
        values.put(COL_USER_ID, booking.getUserId());
        values.put(COL_QUANTITY, booking.getQuantity());
        values.put(COL_TOTAL_PRICE, booking.getTotalPrice());
        values.put(COL_STATUS, booking.getStatus());
        values.put(COL_PURCHASE_DATE, booking.getPurchaseDate());
        values.put(COL_EVENT_TITLE, booking.getEventTitle());
        values.put(COL_EVENT_DATE, booking.getEventDate());
        values.put(COL_EVENT_LOCATION, booking.getEventLocation());
        values.put(COL_EVENT_IMAGE, booking.getEventImage());

        return db.insert(TABLE_BOOKINGS, null, values);
    }

    public List<Booking> getByUser(String userId) {
        List<Booking> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_BOOKINGS,
                null,
                COL_USER_ID + "=?",
                new String[]{userId},
                null, null,
                COL_PURCHASE_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            result.add(cursorToBooking(cursor));
        }
        cursor.close();
        return result;
    }

    public int getTotalEventsAttended(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(true, TABLE_BOOKINGS,
                new String[]{COL_EVENT_ID}, COL_USER_ID + "=?",
                new String[]{userId}, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private Booking cursorToBooking(Cursor cursor) {
        Booking b = new Booking();
        b.setId(cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)));
        b.setEventId(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_ID)));
        b.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        b.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
        b.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_TOTAL_PRICE)));
        b.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
        b.setPurchaseDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_PURCHASE_DATE)));
        b.setEventTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE)));
        b.setEventDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)));
        b.setEventLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_LOCATION)));
        b.setEventImage(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_IMAGE)));
        return b;
    }
}