package com.eventmanager.app.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eventmanager.app.database.BookingDAO;
import com.eventmanager.app.models.Booking;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.services.ReminderScheduler;
import com.eventmanager.app.utils.PreferenceManager;

import java.util.List;

/**
 * Re-planifie tous les rappels de notification après un redémarrage de l'appareil.
 * WorkManager persiste déjà ses tâches via Room/SQLite interne, mais cette
 * sécurité garantit la cohérence même en cas de nettoyage système.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) return;

        PreferenceManager prefManager = new PreferenceManager(context);
        if (!prefManager.isLoggedIn()) return;

        BookingDAO bookingDAO = new BookingDAO(context);
        ReminderScheduler scheduler = new ReminderScheduler(context);

        String userId = prefManager.getUserId();
        List<Booking> bookings = bookingDAO.getByUser(userId);

        long now = System.currentTimeMillis();

        for (Booking booking : bookings) {
            if (booking.getEventDate() > now) {
                // Reconstruit un Event minimal à partir des données snapshot du booking
                Event event = new Event();
                event.setId(booking.getEventId());
                event.setTitle(booking.getEventTitle());
                event.setDate(booking.getEventDate());
                event.setLocation(booking.getEventLocation());

                scheduler.scheduleEventReminders(event);
            }
        }

        // Met aussi à jour le widget
        com.eventmanager.app.widgets.EventWidgetProvider.updateAllWidgets(context);
    }
}