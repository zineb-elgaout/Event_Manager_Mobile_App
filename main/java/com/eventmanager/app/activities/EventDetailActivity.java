package com.eventmanager.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityEventDetailBinding;
import com.eventmanager.app.databinding.ItemTagBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.DateUtils;
import com.eventmanager.app.utils.ShareUtils;

import java.util.List;
import java.util.Locale;
import com.eventmanager.app.activities.MainActivity;

public class EventDetailActivity extends AppCompatActivity {

    private ActivityEventDetailBinding binding;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        event = (Event) getIntent().getSerializableExtra("event");

        if (event == null) {
            finish();
            return;
        }

        bindData();
        setupListeners();
    }

    private void bindData() {
        Glide.with(this)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder_event)
                .error(R.drawable.placeholder_event)
                .into(binding.ivHeroImage);

        binding.tvCategory.setText(event.getCategory().toUpperCase(Locale.getDefault()));
        binding.tvTitle.setText(event.getTitle());
        binding.tvOrganizer.setText("Organisé par " + event.getOrganizer());

        binding.tvDate.setText(DateUtils.formatShortDate(event.getDate()) + " "
                + new java.text.SimpleDateFormat("yyyy", Locale.FRENCH).format(new java.util.Date(event.getDate())));
        binding.tvTime.setText(DateUtils.formatTime(event.getDate()));

        binding.tvLocation.setText(event.getLocation());

        binding.tvSeats.setText(event.getAvailableSeats() + " " + getString(R.string.seats_available));

        binding.tvDescription.setText(event.getDescription());

        if (event.isFree()) {
            binding.tvPrice.setText(getString(R.string.free));
        } else {
            binding.tvPrice.setText(String.format(Locale.getDefault(), "%.0f MAD", event.getPrice()));
        }

        // Tags
        bindTags(event.getTags());
    }

    private void bindTags(List<String> tags) {
        binding.tagsContainer.removeAllViews();
        if (tags == null) return;

        for (String tag : tags) {
            ItemTagBinding tagBinding = ItemTagBinding.inflate(LayoutInflater.from(this), binding.tagsContainer, false);
            tagBinding.getRoot().setText("#" + tag);
            binding.tagsContainer.addView(tagBinding.getRoot());
        }
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        binding.btnShare.setOnClickListener(v -> ShareUtils.shareEventViaSms(this, event));

        binding.locationCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("navigate_to_map", true);
            intent.putExtra("focus_event", event);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        binding.btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}