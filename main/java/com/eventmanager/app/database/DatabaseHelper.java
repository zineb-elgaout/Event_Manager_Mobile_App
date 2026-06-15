package com.eventmanager.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.eventmanager.app.utils.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_BOOKINGS  = "bookings";
    public static final String TABLE_TICKETS   = "tickets";
    public static final String TABLE_FAVORITES = "favorites";

    // Bookings / Tickets columns
    public static final String COL_ID             = "id";
    public static final String COL_EVENT_ID       = "event_id";
    public static final String COL_USER_ID        = "user_id";
    public static final String COL_STATUS         = "status";
    public static final String COL_PURCHASE_DATE  = "purchase_date";
    public static final String COL_EVENT_TITLE    = "event_title";
    public static final String COL_EVENT_DATE     = "event_date";
    public static final String COL_EVENT_LOCATION = "event_location";
    public static final String COL_EVENT_IMAGE    = "event_image";
    public static final String COL_QUANTITY       = "quantity";
    public static final String COL_TOTAL_PRICE    = "total_price";
    public static final String COL_BOOKING_ID     = "booking_id";
    public static final String COL_QR_CODE        = "qr_code";
    public static final String COL_SEAT_NUMBER    = "seat_number";

    // Favorites columns
    public static final String COL_FAV_EVENT_ID      = "event_id";
    public static final String COL_FAV_USER_ID       = "user_id";
    public static final String COL_FAV_TITLE         = "title";
    public static final String COL_FAV_DATE          = "date";
    public static final String COL_FAV_LOCATION      = "location";
    public static final String COL_FAV_IMAGE_URL     = "image_url";
    public static final String COL_FAV_PRICE         = "price";
    public static final String COL_FAV_CATEGORY      = "category";
    public static final String COL_FAV_DESCRIPTION   = "description";
    public static final String COL_FAV_ORGANIZER     = "organizer";
    public static final String COL_FAV_LATITUDE      = "latitude";
    public static final String COL_FAV_LONGITUDE     = "longitude";
    public static final String COL_FAV_SEATS         = "available_seats";

    // DB Version — incrémenté à 3 pour la migration favorites étendue
    private static final int DB_VERSION = 3;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, Constants.DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bookings
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + " (" +
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

        // Tickets
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TICKETS + " (" +
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

        // Favorites — avec tous les champs nécessaires pour EventDetail
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + " (" +
                "event_id TEXT NOT NULL, " +
                "user_id TEXT NOT NULL, " +
                "title TEXT, " +
                "date INTEGER, " +
                "location TEXT, " +
                "image_url TEXT, " +
                "price REAL, " +
                "category TEXT, " +
                "description TEXT, " +
                "organizer TEXT, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "available_seats INTEGER, " +
                "PRIMARY KEY (event_id, user_id)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITES + " (" +
                    "event_id TEXT NOT NULL, user_id TEXT NOT NULL, " +
                    "title TEXT, date INTEGER, location TEXT, image_url TEXT, " +
                    "price REAL, category TEXT, " +
                    "PRIMARY KEY (event_id, user_id)" +
                    ")");
        }
        if (oldVersion < 3) {
            // Ajoute les colonnes manquantes à la table favorites existante
            String[] newColumns = {
                    "description TEXT",
                    "organizer TEXT",
                    "latitude REAL",
                    "longitude REAL",
                    "available_seats INTEGER"
            };
            for (String col : newColumns) {
                try {
                    db.execSQL("ALTER TABLE " + TABLE_FAVORITES + " ADD COLUMN " + col);
                } catch (Exception e) {
                    // Colonne déjà existante — ignore
                    android.util.Log.w("DB", "Column already exists: " + col);
                }
            }
        }
    }
}