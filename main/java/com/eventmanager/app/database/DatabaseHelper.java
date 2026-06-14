package com.eventmanager.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eventmanager.app.utils.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

    // ─── Tables ───────────────────────────────────────────────────────────────
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String TABLE_TICKETS  = "tickets";

    // ─── Common columns ──────────────────────────────────────────────────────
    public static final String COL_ID             = "id";
    public static final String COL_EVENT_ID       = "event_id";
    public static final String COL_USER_ID        = "user_id";
    public static final String COL_STATUS         = "status";
    public static final String COL_PURCHASE_DATE  = "purchase_date";
    public static final String COL_EVENT_TITLE    = "event_title";
    public static final String COL_EVENT_DATE     = "event_date";
    public static final String COL_EVENT_LOCATION = "event_location";
    public static final String COL_EVENT_IMAGE    = "event_image";

    // ─── Bookings specific ───────────────────────────────────────────────────
    public static final String COL_QUANTITY    = "quantity";
    public static final String COL_TOTAL_PRICE = "total_price";

    // ─── Tickets specific ────────────────────────────────────────────────────
    public static final String COL_BOOKING_ID  = "booking_id";
    public static final String COL_QR_CODE     = "qr_code";
    public static final String COL_SEAT_NUMBER = "seat_number";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_EVENT_ID + " TEXT NOT NULL, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_QUANTITY + " INTEGER NOT NULL, " +
                COL_TOTAL_PRICE + " REAL NOT NULL, " +
                COL_STATUS + " TEXT NOT NULL, " +
                COL_PURCHASE_DATE + " INTEGER NOT NULL, " +
                COL_EVENT_TITLE + " TEXT, " +
                COL_EVENT_DATE + " INTEGER, " +
                COL_EVENT_LOCATION + " TEXT, " +
                COL_EVENT_IMAGE + " TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_TICKETS + " (" +
                COL_ID + " TEXT PRIMARY KEY, " +
                COL_BOOKING_ID + " TEXT NOT NULL, " +
                COL_EVENT_ID + " TEXT NOT NULL, " +
                COL_USER_ID + " TEXT NOT NULL, " +
                COL_QR_CODE + " TEXT NOT NULL, " +
                COL_SEAT_NUMBER + " INTEGER, " +
                COL_STATUS + " TEXT NOT NULL, " +
                COL_PURCHASE_DATE + " INTEGER NOT NULL, " +
                COL_EVENT_TITLE + " TEXT, " +
                COL_EVENT_DATE + " INTEGER, " +
                COL_EVENT_LOCATION + " TEXT, " +
                COL_EVENT_IMAGE + " TEXT" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        onCreate(db);
    }
}