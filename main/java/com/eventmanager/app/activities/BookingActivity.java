package com.eventmanager.app.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.eventmanager.app.R;
import com.eventmanager.app.database.BookingDAO;
import com.eventmanager.app.database.TicketDAO;
import com.eventmanager.app.databinding.ActivityBookingBinding;
import com.eventmanager.app.models.Booking;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.models.Ticket;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.DateUtils;
import com.eventmanager.app.utils.PreferenceManager;
import com.eventmanager.app.utils.QRCodeGenerator;
import com.eventmanager.app.utils.ShareUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import com.eventmanager.app.services.ReminderScheduler;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BookingActivity extends AppCompatActivity {

    private static final double SERVICE_FEE_PER_TICKET = 20;

    private ActivityBookingBinding binding;
    private Event event;
    private PreferenceManager preferenceManager;
    private BookingDAO bookingDAO;
    private TicketDAO ticketDAO;

    private int quantity = 1;
    private List<Ticket> createdTickets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestNotificationPermissionIfNeeded();
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        event = (Event) getIntent().getSerializableExtra("event");
        if (event == null) {
            finish();
            return;
        }

        preferenceManager = new PreferenceManager(this);
        bookingDAO = new BookingDAO(this);
        ticketDAO = new TicketDAO(this);

        bindEventSummary();
        updatePriceBreakdown();
        setupListeners();

    }

    private void requestNotificationPermissionIfNeeded() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 300);
            }
        }
    }
    private void bindEventSummary() {
        Glide.with(this)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder_event)
                .into(binding.ivEventImage);

        binding.tvEventTitle.setText(event.getTitle());
        binding.tvEventDate.setText(DateUtils.formatEventDate(event.getDate()));
        binding.tvEventLocation.setText(event.getLocation());
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updatePriceBreakdown();
            }
        });

        binding.btnIncrease.setOnClickListener(v -> {
            int maxAvailable = Math.min(Constants.MAX_TICKETS_PER_BOOKING, event.getAvailableSeats());
            if (quantity < maxAvailable) {
                quantity++;
                updatePriceBreakdown();
            } else {
                Toast.makeText(this, "Limite atteinte", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnConfirm.setOnClickListener(v -> confirmBooking());

        binding.btnDone.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        binding.btnShareTicket.setOnClickListener(v -> {
            if (!createdTickets.isEmpty()) {
                ShareUtils.shareTicketViaSms(this, createdTickets.get(0));
            }
        });
    }

    private void updatePriceBreakdown() {
        binding.tvQuantity.setText(String.valueOf(quantity));

        double unitPrice = event.getPrice();
        double serviceFee = event.isFree() ? 0 : SERVICE_FEE_PER_TICKET * quantity;
        double total = (unitPrice * quantity) + serviceFee;

        binding.tvUnitPrice.setText(formatPrice(unitPrice));
        binding.tvServiceFee.setText(formatPrice(serviceFee));
        binding.tvTotal.setText(formatPrice(total));
    }

    private String formatPrice(double price) {
        if (price == 0) return getString(R.string.free);
        return String.format(Locale.getDefault(), "%.0f MAD", price);
    }

    // ─── Confirmation ────────────────────────────────────────────────────────

    private void confirmBooking() {
        binding.btnConfirm.setEnabled(false);
        binding.btnConfirm.setText("Traitement...");

        // Simulate processing delay
        binding.getRoot().postDelayed(() -> {
            saveBookingToDatabase();
            showConfirmation();
        }, 800);
    }

    private void saveBookingToDatabase() {
        String userId = preferenceManager.getUserId();
        long now = System.currentTimeMillis();

        double unitPrice = event.getPrice();
        double serviceFee = event.isFree() ? 0 : SERVICE_FEE_PER_TICKET * quantity;
        double total = (unitPrice * quantity) + serviceFee;

        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);

        Booking booking = new Booking(
                bookingId,
                event.getId(),
                userId,
                quantity,
                total,
                Constants.STATUS_VALID,
                now,
                event.getTitle(),
                event.getDate(),
                event.getLocation(),
                event.getImageUrl()
        );
        bookingDAO.insert(booking);

        createdTickets.clear();
        for (int i = 1; i <= quantity; i++) {
            String ticketId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
            String qrContent = ticketId;

            Ticket ticket = new Ticket(
                    ticketId,
                    bookingId,
                    event.getId(),
                    userId,
                    qrContent,
                    i,
                    Constants.STATUS_VALID,
                    now,
                    event.getTitle(),
                    event.getDate(),
                    event.getLocation(),
                    event.getImageUrl()
            );
            ticketDAO.insert(ticket);
            createdTickets.add(ticket);
        }

        // Planification des rappels de notification (J-1 et H-1)
        new ReminderScheduler(this).scheduleEventReminders(event);

        // Mise à jour du widget avec ce nouvel événement (le plus proche sera affiché)
        com.eventmanager.app.widgets.EventWidgetProvider.updateAllWidgets(this);
    }

    private void showConfirmation() {
        binding.bookingForm.setVisibility(View.GONE);
        binding.ticketConfirmation.setVisibility(View.VISIBLE);

        binding.tvConfirmationSubtitle.setText(
                quantity + (quantity > 1 ? " billets pour " : " billet pour ") + event.getTitle()
        );

        binding.tvTicketEventTitle.setText(event.getTitle());
        binding.tvTicketEventDate.setText(DateUtils.formatEventDate(event.getDate()));
        binding.tvTicketEventLocation.setText(event.getLocation());

        // QR code du premier ticket (le plus représentatif de la réservation)
        Ticket firstTicket = createdTickets.get(0);
        binding.tvTicketCode.setText(firstTicket.getQrCode());

        android.graphics.Bitmap qrBitmap = QRCodeGenerator.generate(firstTicket.getQrCode(), 600);
        if (qrBitmap != null) {
            binding.ivQrCode.setImageBitmap(qrBitmap);
        }

        Toast.makeText(this, "Billet" + (quantity > 1 ? "s" : "") + " ajouté" + (quantity > 1 ? "s" : "") + " à votre historique", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (binding.ticketConfirmation.getVisibility() == View.VISIBLE) {
            setResult(RESULT_OK);
            finish();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}