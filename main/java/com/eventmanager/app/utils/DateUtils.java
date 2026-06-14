package com.eventmanager.app.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final Locale LOCALE_FR = new Locale("fr", "FR");

    /**
     * Format complet : "15 Juin 2026 • 20h00"
     */
    public static String formatEventDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy '•' HH'h'mm", LOCALE_FR);
        return capitalize(sdf.format(new Date(timestamp)));
    }

    /**
     * Format court : "15 Juin"
     */
    public static String formatShortDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", LOCALE_FR);
        return capitalize(sdf.format(new Date(timestamp)));
    }

    /**
     * Format jour de la semaine : "Lundi"
     */
    public static String formatDayName(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", LOCALE_FR);
        return capitalize(sdf.format(new Date(timestamp)));
    }

    /**
     * Format heure : "20h00"
     */
    public static String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH'h'mm", LOCALE_FR);
        return sdf.format(new Date(timestamp));
    }

    /**
     * Numéro du jour : "15"
     */
    public static String formatDayNumber(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd", LOCALE_FR);
        return sdf.format(new Date(timestamp));
    }

    /**
     * Nom du mois court : "Juin"
     */
    public static String formatMonthShort(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", LOCALE_FR);
        return capitalize(sdf.format(new Date(timestamp)));
    }

    /**
     * Compte à rebours lisible : "Dans 3 jours", "Demain", "Aujourd'hui"
     */
    public static String getCountdownText(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = timestamp - now;

        if (diff < 0) return "Terminé";

        long days = diff / (1000 * 60 * 60 * 24);

        if (days == 0) return "Aujourd'hui";
        if (days == 1) return "Demain";
        if (days < 7) return "Dans " + days + " jours";
        if (days < 30) return "Dans " + (days / 7) + " semaine" + (days / 7 > 1 ? "s" : "");

        return "Dans " + (days / 30) + " mois";
    }

    /**
     * Greeting selon l'heure : "Bonjour" / "Bon après-midi" / "Bonsoir"
     */
    public static String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour < 12) return "Bonjour";
        if (hour < 18) return "Bon après-midi";
        return "Bonsoir";
    }

    private static String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase(LOCALE_FR) + input.substring(1);
    }
}