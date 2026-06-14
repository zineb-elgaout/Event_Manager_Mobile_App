package com.eventmanager.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eventmanager.app.models.Ticket;

import java.util.ArrayList;
import java.util.List;

import static com.eventmanager.app.database.DatabaseHelper.*;

public class TicketDAO {

    private final DatabaseHelper dbHelper;

    public TicketDAO(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(Ticket ticket) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ID, ticket.getId());
        values.put(COL_BOOKING_ID, ticket.getBookingId());
        values.put(COL_EVENT_ID, ticket.getEventId());
        values.put(COL_USER_ID, ticket.getUserId());
        values.put(COL_QR_CODE, ticket.getQrCode());
        values.put(COL_SEAT_NUMBER, ticket.getSeatNumber());
        values.put(COL_STATUS, ticket.getStatus());
        values.put(COL_PURCHASE_DATE, ticket.getPurchaseDate());
        values.put(COL_EVENT_TITLE, ticket.getEventTitle());
        values.put(COL_EVENT_DATE, ticket.getEventDate());
        values.put(COL_EVENT_LOCATION, ticket.getEventLocation());
        values.put(COL_EVENT_IMAGE, ticket.getEventImage());

        return db.insert(TABLE_TICKETS, null, values);
    }

    public List<Ticket> getByBooking(String bookingId) {
        return query(COL_BOOKING_ID + "=?", new String[]{bookingId});
    }

    public List<Ticket> getByUser(String userId) {
        return query(COL_USER_ID + "=?", new String[]{userId});
    }

    public Ticket getById(String ticketId) {
        List<Ticket> list = query(COL_ID + "=?", new String[]{ticketId});
        return list.isEmpty() ? null : list.get(0);
    }

    public void updateStatus(String ticketId, String status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        db.update(TABLE_TICKETS, values, COL_ID + "=?", new String[]{ticketId});
    }

    private List<Ticket> query(String selection, String[] args) {
        List<Ticket> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_TICKETS, null, selection, args,
                null, null, COL_PURCHASE_DATE + " DESC"
        );

        while (cursor.moveToNext()) {
            result.add(cursorToTicket(cursor));
        }
        cursor.close();
        return result;
    }

    private Ticket cursorToTicket(Cursor cursor) {
        Ticket t = new Ticket();
        t.setId(cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)));
        t.setBookingId(cursor.getString(cursor.getColumnIndexOrThrow(COL_BOOKING_ID)));
        t.setEventId(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_ID)));
        t.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_ID)));
        t.setQrCode(cursor.getString(cursor.getColumnIndexOrThrow(COL_QR_CODE)));
        t.setSeatNumber(cursor.getInt(cursor.getColumnIndexOrThrow(COL_SEAT_NUMBER)));
        t.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
        t.setPurchaseDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_PURCHASE_DATE)));
        t.setEventTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_TITLE)));
        t.setEventDate(cursor.getLong(cursor.getColumnIndexOrThrow(COL_EVENT_DATE)));
        t.setEventLocation(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_LOCATION)));
        t.setEventImage(cursor.getString(cursor.getColumnIndexOrThrow(COL_EVENT_IMAGE)));
        return t;
    }
}