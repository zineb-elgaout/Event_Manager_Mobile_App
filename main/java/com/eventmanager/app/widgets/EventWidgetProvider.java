package com.eventmanager.app.widgets;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.eventmanager.app.R;
import com.eventmanager.app.activities.MainActivity;
import com.eventmanager.app.database.BookingDAO;
import com.eventmanager.app.models.Booking;
import com.eventmanager.app.utils.DateUtils;
import com.eventmanager.app.utils.PreferenceManager;

import java.util.List;

/**
 * Widget écran d'accueil affichant le prochain événement réservé
 * avec un compte à rebours (J-X).
 */
public class EventWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId) {
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_event_countdown);

        Booking nextEvent = getNextUpcomingEvent(context);

        if (nextEvent != null) {
            views.setViewVisibility(R.id.widget_event_title, View.VISIBLE);
            views.setViewVisibility(R.id.widget_event_date, View.VISIBLE);
            views.setViewVisibility(R.id.widget_event_location, View.VISIBLE);
            views.setViewVisibility(R.id.widget_countdown_badge, View.VISIBLE);
            views.setViewVisibility(R.id.widget_empty_hint, View.GONE);

            views.setTextViewText(R.id.widget_event_title, nextEvent.getEventTitle());
            views.setTextViewText(R.id.widget_event_date, DateUtils.formatEventDate(nextEvent.getEventDate()));
            views.setTextViewText(R.id.widget_event_location, nextEvent.getEventLocation());
            views.setTextViewText(R.id.widget_countdown_badge, getCountdownBadge(nextEvent.getEventDate()));
        } else {
            views.setViewVisibility(R.id.widget_event_title, View.VISIBLE);
            views.setViewVisibility(R.id.widget_event_date, View.GONE);
            views.setViewVisibility(R.id.widget_event_location, View.GONE);
            views.setViewVisibility(R.id.widget_countdown_badge, View.GONE);
            views.setViewVisibility(R.id.widget_empty_hint, View.VISIBLE);

            views.setTextViewText(R.id.widget_event_title, "Aucun événement à venir");
        }

        // Click → ouvre l'app sur l'onglet Billets
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("open_tickets", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_event_title, pendingIntent);

        appWidgetManager.updateAppWidget(widgetId, views);
    }

    /**
     * Retourne la réservation à venir la plus proche dans le temps,
     * ou null si aucune.
     */
    private Booking getNextUpcomingEvent(Context context) {
        PreferenceManager prefManager = new PreferenceManager(context);
        if (!prefManager.isLoggedIn()) return null;

        BookingDAO dao = new BookingDAO(context);
        List<Booking> bookings = dao.getByUser(prefManager.getUserId());

        long now = System.currentTimeMillis();
        Booking closest = null;

        for (Booking b : bookings) {
            if (b.getEventDate() >= now) {
                if (closest == null || b.getEventDate() < closest.getEventDate()) {
                    closest = b;
                }
            }
        }
        return closest;
    }

    private String getCountdownBadge(long eventDate) {
        long now = System.currentTimeMillis();
        long diffMillis = eventDate - now;
        long days = diffMillis / (1000 * 60 * 60 * 24);

        if (days <= 0) {
            long hours = diffMillis / (1000 * 60 * 60);
            if (hours <= 0) return "Maintenant";
            return "H-" + hours;
        }
        return "J-" + days;
    }

    /**
     * Méthode statique appelée depuis l'app pour forcer la mise à jour
     * de toutes les instances du widget (après une nouvelle réservation,
     * un scan de billet, etc.)
     */
    public static void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, EventWidgetProvider.class);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);

        if (widgetIds.length > 0) {
            Intent intent = new Intent(context, EventWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
            context.sendBroadcast(intent);
        }
    }
}