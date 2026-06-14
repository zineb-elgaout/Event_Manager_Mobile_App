package com.eventmanager.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eventmanager.app.R;
import com.eventmanager.app.adapters.CategoryAdapter;
import com.eventmanager.app.adapters.EventAdapter;
import com.eventmanager.app.adapters.EventCarouselAdapter;
import com.eventmanager.app.databinding.FragmentHomeBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.DateUtils;
import com.eventmanager.app.utils.PreferenceManager;
import com.eventmanager.app.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private EventViewModel viewModel;

    private EventAdapter eventAdapter;
    private EventCarouselAdapter carouselAdapter;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupGreeting();
        setupCarousel();
        setupCategories();
        setupEventsList();
        setupSwipeRefresh();
        observeViewModel();
    }

    // ─── Greeting ─────────────────────────────────────────────────────────────

    private void setupGreeting() {
        PreferenceManager prefs = new PreferenceManager(requireContext());
        String name = prefs.getUserName();
        String firstName = name.isEmpty() ? "" : name.split(" ")[0];

        String greeting = DateUtils.getGreeting();
        binding.tvGreeting.setText(
                firstName.isEmpty() ? greeting + " 👋" : greeting + ", " + firstName + " 👋"
        );
    }

    // ─── Featured Carousel ────────────────────────────────────────────────────

    private void setupCarousel() {
        carouselAdapter = new EventCarouselAdapter(this::navigateToDetail);
        binding.viewPagerFeatured.setAdapter(carouselAdapter);
        binding.viewPagerFeatured.setOffscreenPageLimit(2);

        // Add page transform for a nice "peek" effect
        binding.viewPagerFeatured.setPageTransformer((page, position) -> {
            float scale = 1 - (0.06f * Math.abs(position));
            page.setScaleY(Math.max(scale, 0.92f));
        });
    }

    // ─── Categories ───────────────────────────────────────────────────────────

    private void setupCategories() {
        List<String> categories = Arrays.asList(
                Constants.CAT_ALL,
                Constants.CAT_CONCERT,
                Constants.CAT_CONFERENCE,
                Constants.CAT_EXPO,
                Constants.CAT_SPORT,
                Constants.CAT_FESTIVAL,
                Constants.CAT_THEATER
        );

        categoryAdapter = new CategoryAdapter(categories, category -> {
            String filter = category.equals(Constants.CAT_ALL) ? null : category;
            viewModel.filterByCategory(filter);

            String sectionTitle = category.equals(Constants.CAT_ALL)
                    ? getString(R.string.section_upcoming)
                    : category;
            binding.tvSectionTitle.setText(sectionTitle);
        });

        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    // ─── Events List ──────────────────────────────────────────────────────────

    private void setupEventsList() {
        eventAdapter = new EventAdapter(
                this::navigateToDetail,
                event -> viewModel.toggleFavorite(event)
        );

        binding.rvEvents.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvEvents.setAdapter(eventAdapter);
        binding.rvEvents.setNestedScrollingEnabled(false);
    }

    // ─── Swipe Refresh ────────────────────────────────────────────────────────

    private void setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary);
        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private void observeViewModel() {

        // Featured events
        viewModel.getFeaturedEvents().observe(getViewLifecycleOwner(), events -> {
            carouselAdapter.updateData(events);
            binding.shimmerFeatured.stopShimmer();
            binding.shimmerFeatured.setVisibility(View.GONE);

            if (!events.isEmpty()) {
                binding.viewPagerFeatured.setVisibility(View.VISIBLE);
                binding.dotsIndicator.setVisibility(View.VISIBLE);
                binding.dotsIndicator.attachTo(binding.viewPagerFeatured);
            }
        });

        // Main events list
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.updateData(events);

            binding.shimmerEvents.stopShimmer();
            binding.shimmerEvents.setVisibility(View.GONE);
            binding.swipeRefresh.setRefreshing(false);

            if (events.isEmpty()) {
                binding.rvEvents.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.rvEvents.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
            }

            binding.tvEventCount.setText(events.size() + " événement" + (events.size() > 1 ? "s" : ""));
        });

        // Loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                if (binding.shimmerEvents.getVisibility() != View.GONE
                        || binding.rvEvents.getVisibility() == View.GONE) {
                    binding.shimmerEvents.setVisibility(View.VISIBLE);
                    binding.shimmerEvents.startShimmer();
                    binding.rvEvents.setVisibility(View.GONE);
                    binding.emptyState.setVisibility(View.GONE);
                }
            }
        });

        // Errors
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                binding.shimmerFeatured.stopShimmer();
                binding.shimmerFeatured.setVisibility(View.GONE);
                binding.shimmerEvents.stopShimmer();
                binding.shimmerEvents.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);

                android.widget.Toast.makeText(getContext(), error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    private void navigateToDetail(Event event) {
        Intent intent = new Intent(getActivity(), com.eventmanager.app.activities.EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.shimmerFeatured.stopShimmer();
        binding.shimmerEvents.stopShimmer();
        binding = null;
    }
}