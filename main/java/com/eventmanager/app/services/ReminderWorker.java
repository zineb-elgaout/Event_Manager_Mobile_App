package com.eventmanager.app.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Worker exécuté par WorkManager au moment programmé.
 * Affiche la notification de rappel pour un événement donné.
 */
public class ReminderWorker extends Worker {

    public static final String KEY_EVENT_ID       = "event_id";
    public static final String KEY_EVENT_TITLE    = "event_title";
    public static final String KEY_EVENT_LOCATION = "event_location";
    public static final String KEY_HOURS_BEFORE   = "hours_before";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String eventId = getInputData().getString(KEY_EVENT_ID);
        String eventTitle = getInputData().getString(KEY_EVENT_TITLE);
        String eventLocation = getInputData().getString(KEY_EVENT_LOCATION);
        long hoursBefore = getInputData().getLong(KEY_HOURS_BEFORE, 1);
        int notificationId = getInputData().getInt(KEY_NOTIFICATION_ID, 0);

        if (eventId == null || eventTitle == null) {
            return Result.failure();
        }

        NotificationHelper helper = new NotificationHelper(getApplicationContext());
        helper.showEventReminder(eventId, eventTitle,
                eventLocation != null ? eventLocation : "", hoursBefore, notificationId);

        return Result.success();
    }
}