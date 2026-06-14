package com.eventmanager.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.eventmanager.app.R;
import com.eventmanager.app.activities.EventDetailActivity;
import com.eventmanager.app.activities.MainActivity;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.Constants;

/**
 * Centralise la création de notifications pour l'application.
 */
public class NotificationHelper {

    private final Context context;
    private final NotificationManager manager;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    Constants.CHANNEL_ID_REMINDERS,
                    Constants.CHANNEL_NAME_REMINDERS,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Rappels avant le début de vos événements réservés");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 250, 100, 250});
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Affiche un rappel d'événement.
     *
     * @param eventId       id de l'événement
     * @param eventTitle    titre de l'événement
     * @param eventLocation lieu
     * @param hoursBefore   nombre d'heures avant le début (pour le texte)
     * @param notificationId id unique de la notification (pour éviter les doublons)
     */
    public void showEventReminder(String eventId, String eventTitle, String eventLocation,
                                  long hoursBefore, int notificationId) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.EXTRA_EVENT_ID, eventId);
        intent.putExtra(Constants.EXTRA_FROM_NOTIF, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title;
        String body;

        if (hoursBefore >= 24) {
            title = "📅 Demain : " + eventTitle;
            body = "N'oubliez pas, votre événement commence demain à " + eventLocation;
        } else {
            title = "⏰ " + eventTitle + " commence dans 1h";
            body = "Préparez-vous ! 📍 " + eventLocation;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.CHANNEL_ID_REMINDERS)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(context.getResources().getColor(R.color.primary, null))
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(notificationId, builder.build());
    }
}