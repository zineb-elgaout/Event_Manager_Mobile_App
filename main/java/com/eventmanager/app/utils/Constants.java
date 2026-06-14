package com.eventmanager.app.utils;

public final class Constants {

    private Constants() {}

    // SharedPreferences
    public static final String PREF_NAME        = "EventManagerPrefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_ONBOARDING   = "onboarding_done";
    public static final String KEY_USER_ID      = "user_id";
    public static final String KEY_USER_NAME    = "user_name";
    public static final String KEY_USER_EMAIL   = "user_email";
    public static final String KEY_USER_AVATAR  = "user_avatar";
    public static final String KEY_CATEGORIES   = "category_prefs";

    // Intent extras
    public static final String EXTRA_EVENT_ID   = "event_id";
    public static final String EXTRA_TICKET_ID  = "ticket_id";
    public static final String EXTRA_FROM_NOTIF = "from_notification";

    // Database
    public static final String DB_NAME    = "eventmanager.db";
    public static final int    DB_VERSION = 1;

    // API
    public static final String BASE_URL         = "https://api.eventmanager.mock/v1/";
    public static final int    TIMEOUT_CONNECT  = 30;
    public static final int    TIMEOUT_READ     = 30;

    // Notifications
    public static final String CHANNEL_ID   = "event_reminders";
    public static final String CHANNEL_NAME = "Event Reminders";
    public static final int    NOTIF_ID_BASE = 1000;

    // Request codes
    public static final int RC_CAMERA   = 100;
    public static final int RC_LOCATION = 101;
    public static final int RC_SMS      = 102;
    public static final int RC_QR_SCAN  = 103;

    // Pagination
    public static final int PAGE_SIZE = 10;

    // Categories
    public static final String CAT_ALL        = "Tous";
    public static final String CAT_CONCERT    = "Concert";
    public static final String CAT_CONFERENCE = "Conférence";
    public static final String CAT_EXPO       = "Exposition";
    public static final String CAT_SPORT      = "Sport";
    public static final String CAT_FESTIVAL   = "Festival";
    public static final String CAT_THEATER    = "Théâtre";

    // Ticket / Booking status
    public static final String STATUS_VALID     = "VALID";
    public static final String STATUS_USED      = "USED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    // Max tickets per booking
    public static final int MAX_TICKETS_PER_BOOKING = 8;

    // Tabs Tickets
    public static final int TICKET_TAB_UPCOMING = 0;
    public static final int TICKET_TAB_PAST     = 1;

    // QR Scan result
    public static final String EXTRA_SCAN_RESULT = "scan_result";

    // Notifications
    public static final String CHANNEL_ID_REMINDERS = "event_reminders";
    public static final String CHANNEL_NAME_REMINDERS = "Rappels d'événements";

    // Reminder timings (en millisecondes avant l'événement)
    public static final long REMINDER_1_DAY_BEFORE = 24 * 60 * 60 * 1000L;
    public static final long REMINDER_1_HOUR_BEFORE = 60 * 60 * 1000L;

    // Work tags
    public static final String WORK_TAG_REMINDER = "event_reminder_";

    // Widget
    public static final String ACTION_WIDGET_UPDATE = "com.eventmanager.app.WIDGET_UPDATE";
    public static final String PREF_WIDGET_EVENT_ID = "widget_event_id";
}