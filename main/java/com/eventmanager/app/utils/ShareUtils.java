package com.eventmanager.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.eventmanager.app.models.Event;
import com.eventmanager.app.models.Ticket;

import java.util.Locale;

public class ShareUtils {

    /**
     * Partage les infos d'un événement via SMS (Intent implicite SENDTO).
     * Aucune permission requise — ouvre l'app SMS de l'utilisateur.
     */
    public static void shareEventViaSms(Context context, Event event) {
        String message = buildEventMessage(event);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"));
        intent.putExtra("sms_body", message);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // Fallback: partage générique (WhatsApp, Mail, etc.)
            shareGeneric(context, "Découvre cet événement", message);
        }
    }

    /**
     * Partage un billet (avec QR code id) via SMS.
     */
    public static void shareTicketViaSms(Context context, Ticket ticket) {
        String message = String.format(Locale.getDefault(),
                "🎫 Mon billet pour %s\n📅 %s\n📍 %s\n🔖 Code: %s\n\nTéléchargez EventManager pour plus d'infos !",
                ticket.getEventTitle(),
                DateUtils.formatEventDate(ticket.getEventDate()),
                ticket.getEventLocation(),
                ticket.getQrCode()
        );

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"));
        intent.putExtra("sms_body", message);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            shareGeneric(context, "Mon billet EventManager", message);
        }
    }

    private static String buildEventMessage(Event event) {
        return String.format(Locale.getDefault(),
                "🎉 %s\n📅 %s\n📍 %s\n💰 %s\n\nDécouvre cet événement sur EventManager !",
                event.getTitle(),
                DateUtils.formatEventDate(event.getDate()),
                event.getLocation(),
                event.isFree() ? "Gratuit" : String.format(Locale.getDefault(), "%.0f MAD", event.getPrice())
        );
    }

    private static void shareGeneric(Context context, String title, String message) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(shareIntent, title));
    }
}