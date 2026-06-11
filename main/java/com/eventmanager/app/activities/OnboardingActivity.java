package com.eventmanager.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityOnboardingBinding;
import com.eventmanager.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private PreferenceManager preferenceManager;
    private OnboardingAdapter adapter;
    private int currentPage = 0;

    // Onboarding data
    private static class OnboardingPage {
        int imageRes;
        String title;
        String description;

        OnboardingPage(int imageRes, String title, String description) {
            this.imageRes = imageRes;
            this.title = title;
            this.description = description;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        setupPages();
        setupDots();
        setupListeners();
    }

    private void setupPages() {
        List<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage(
                R.drawable.onboarding_1,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
        ));
        pages.add(new OnboardingPage(
                R.drawable.onboarding_2,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
        ));
        pages.add(new OnboardingPage(
                R.drawable.onboarding_3,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
        ));

        adapter = new OnboardingAdapter(pages);
        binding.viewPagerOnboarding.setAdapter(adapter);

        binding.viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateDots(position);
                updateButtonText(position);
            }
        });
    }

    private void setupDots() {
        updateDots(0);
    }

    private void updateDots(int selectedIndex) {
        binding.dotsContainer.removeAllViews();
        int pageCount = adapter.getItemCount();

        for (int i = 0; i < pageCount; i++) {
            View dot = new View(this);
            int size = getResources().getDimensionPixelSize(
                    i == selectedIndex ? R.dimen.dot_active_width : R.dimen.dot_inactive_size
            );
            ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                    i == selectedIndex ? dpToPx(24) : dpToPx(8),
                    dpToPx(8)
            );
            params.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(
                    i == selectedIndex
                            ? R.drawable.bg_onboarding_dot_active
                            : R.drawable.bg_onboarding_dot_inactive
            );
            binding.dotsContainer.addView(dot);
        }
    }

    private void updateButtonText(int position) {
        if (position == adapter.getItemCount() - 1) {
            binding.btnNext.setText(getString(R.string.btn_get_started));
            binding.btnSkip.setVisibility(View.GONE);
        } else {
            binding.btnNext.setText(getString(R.string.btn_next));
            binding.btnSkip.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        binding.btnNext.setOnClickListener(v -> {
            if (currentPage < adapter.getItemCount() - 1) {
                binding.viewPagerOnboarding.setCurrentItem(currentPage + 1, true);
            } else {
                finishOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void finishOnboarding() {
        preferenceManager.setOnboardingDone(true);
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    // ─── Inner Adapter ───────────────────────────────────────────────────────

    static class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.PageViewHolder> {

        private final List<OnboardingPage> pages;

        OnboardingAdapter(List<OnboardingPage> pages) {
            this.pages = pages;
        }

        @NonNull
        @Override
        public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_onboarding_page, parent, false);
            return new PageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
            OnboardingPage page = pages.get(position);
            holder.ivIllustration.setImageResource(page.imageRes);
            holder.tvTitle.setText(page.title);
            holder.tvDescription.setText(page.description);
        }

        @Override
        public int getItemCount() {
            return pages.size();
        }

        static class PageViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIllustration;
            TextView tvTitle, tvDescription;

            PageViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIllustration = itemView.findViewById(R.id.ivIllustration);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescription = itemView.findViewById(R.id.tvDescription);
            }
        }
    }
}