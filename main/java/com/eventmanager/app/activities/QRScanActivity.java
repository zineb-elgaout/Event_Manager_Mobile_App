package com.eventmanager.app.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.eventmanager.app.R;
import com.eventmanager.app.database.TicketDAO;
import com.eventmanager.app.databinding.ActivityQrScanBinding;
import com.eventmanager.app.models.Ticket;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.DateUtils;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class QRScanActivity extends AppCompatActivity {

    private ActivityQrScanBinding binding;
    private DecoratedBarcodeView barcodeView;
    private TicketDAO ticketDAO;
    private boolean isFlashOn = false;
    private boolean isProcessing = false;

    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ticketDAO = new TicketDAO(this);

        setupListeners();
        startScanLineAnimation();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initScanner();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    private void initScanner() {
        barcodeView = new DecoratedBarcodeView(this);
        barcodeView.getViewFinder().setVisibility(View.GONE); // on utilise notre propre overlay
        binding.scannerContainer.addView(barcodeView);

        barcodeView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                if (isProcessing) return;
                isProcessing = true;
                handleScanResult(result.getText());
            }
        });

        barcodeView.resume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner();
            } else {
                android.widget.Toast.makeText(this, "Permission caméra requise pour scanner", android.widget.Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void setupListeners() {
        binding.btnClose.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btnFlash.setOnClickListener(v -> toggleFlash());

        binding.btnScanAgain.setOnClickListener(v -> {
            binding.resultCard.setVisibility(View.GONE);
            isProcessing = false;
            if (barcodeView != null) barcodeView.resume();
        });
    }

    private void toggleFlash() {
        if (barcodeView == null) return;
        isFlashOn = !isFlashOn;
        if (isFlashOn) {
            barcodeView.setTorchOn();
            binding.btnFlash.setImageResource(R.drawable.ic_flash_on);
        } else {
            barcodeView.setTorchOff();
            binding.btnFlash.setImageResource(R.drawable.ic_flash_off);
        }
    }

    // ─── Animation ────────────────────────────────────────────────────────────

    private void startScanLineAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.scanLine, "translationY", 0f, 256f);
        animator.setDuration(1800);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    // ─── Scan Result Handling ─────────────────────────────────────────────────

    private void handleScanResult(String content) {
        if (barcodeView != null) barcodeView.pause();

        Ticket ticket = ticketDAO.getById(content);

        runOnUiThread(() -> {
            binding.resultCard.setVisibility(View.VISIBLE);

            if (ticket == null) {
                showInvalidResult("QR Code non reconnu", "Ce billet n'existe pas dans le système.");
                return;
            }

            switch (ticket.getStatus()) {
                case Constants.STATUS_VALID:
                    ticketDAO.updateStatus(ticket.getId(), Constants.STATUS_USED);
                    showValidResult(ticket);
                    break;

                case Constants.STATUS_USED:
                    showInvalidResult("Billet déjà utilisé",
                            ticket.getEventTitle() + "\nCe billet a déjà été scanné.");
                    break;

                case Constants.STATUS_CANCELLED:
                    showInvalidResult("Billet annulé",
                            ticket.getEventTitle() + "\nCe billet a été annulé.");
                    break;

                default:
                    showInvalidResult("Statut inconnu", ticket.getEventTitle());
            }
        });
    }

    private void showValidResult(Ticket ticket) {
        binding.ivResultIcon.setImageResource(R.drawable.ic_check_circle);
        binding.tvResultTitle.setText("Billet valide ✓");
        binding.tvResultSubtitle.setText(
                ticket.getEventTitle() + "\n" +
                        DateUtils.formatEventDate(ticket.getEventDate()) + "\n" +
                        "Place n°" + ticket.getSeatNumber()
        );
    }

    private void showInvalidResult(String title, String subtitle) {
        binding.ivResultIcon.setImageResource(R.drawable.ic_close);
        binding.tvResultTitle.setText(title);
        binding.tvResultSubtitle.setText(subtitle);
    }

    // ─── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null && !isProcessing) barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (barcodeView != null) barcodeView.pause();
    }
}