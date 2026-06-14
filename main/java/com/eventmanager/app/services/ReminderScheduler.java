package com.eventmanager.app.services;

import android.content.Context;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.Constants;

import java.util.concurrent.TimeUnit;

/**
 * Planifie les rappels de notification pour un événement réservé :
 *  - 24h avant le début
 *  - 1h avant le début
 *
 * Utilise WorkManager pour une exécution fiable, même si l'app est fermée
 * ou si l'appareil redémarre (les tâches WorkManager persistent).
 */
public class ReminderScheduler {

    private final Context context;

    public ReminderScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Planifie les deux rappels (J-1 et H-1) pour un événement.
     * Si les délais sont déjà passés, le rappel correspondant n'est pas planifié.
     */
    public void scheduleEventReminders(Event event) {
        long now = System.currentTimeMillis();
        long eventDate = event.getDate();

        // TEST TEMPORAIRE — à retirer après test
        long delayTest = 10_000; // 10 secondes
        scheduleReminder(event, delayTest, 1, ("test_" + event.getId()).hashCode());

        // Rappel J-1 (24h avant)
        long delay24h = eventDate - now - Constants.REMINDER_1_DAY_BEFORE;
        if (delay24h > 0) {
            scheduleReminder(event, delay24h, 24, ("d1_" + event.getId()).hashCode());
        }

        // Rappel H-1 (1h avant)
        long delay1h = eventDate - now - Constants.REMINDER_1_HOUR_BEFORE;
        if (delay1h > 0) {
            scheduleReminder(event, delay1h, 1, ("h1_" + event.getId()).hashCode());
        }
    }

    private void scheduleReminder(Event event, long delayMillis, long hoursBefore, int notificationId) {
        Data inputData = new Data.Builder()
                .putString(ReminderWorker.KEY_EVENT_ID, event.getId())
                .putString(ReminderWorker.KEY_EVENT_TITLE, event.getTitle())
                .putString(ReminderWorker.KEY_EVENT_LOCATION, event.getLocation())
                .putLong(ReminderWorker.KEY_HOURS_BEFORE, hoursBefore)
                .putInt(ReminderWorker.KEY_NOTIFICATION_ID, notificationId)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReminderWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(Constants.WORK_TAG_REMINDER + event.getId())
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                Constants.WORK_TAG_REMINDER + event.getId() + "_" + hoursBefore,
                ExistingWorkPolicy.REPLACE,
                workRequest
        );
    }

    /**
     * Annule tous les rappels programmés pour un événement
     * (utile si une réservation est annulée).
     */
    public void cancelEventReminders(String eventId) {
        WorkManager.getInstance(context).cancelAllWorkByTag(Constants.WORK_TAG_REMINDER + eventId);
    }
}