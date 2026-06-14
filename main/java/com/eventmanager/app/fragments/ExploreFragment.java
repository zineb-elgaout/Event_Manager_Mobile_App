package com.eventmanager.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eventmanager.app.R;
import com.eventmanager.app.activities.EventDetailActivity;
import com.eventmanager.app.adapters.CategoryAdapter;
import com.eventmanager.app.adapters.EventAdapter;
import com.eventmanager.app.databinding.FragmentExploreBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.viewmodel.EventViewModel;

import java.util.Arrays;
import java.util.List;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;
    private EventViewModel viewModel;
    private EventAdapter eventAdapter;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        setupSearch();
        setupCategories();
        setupEventsList();
        observeViewModel();
    }

    // ─── Search ───────────────────────────────────────────────────────────────

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s.length() > 0;
                binding.btnClearSearch.setVisibility(hasText ? View.VISIBLE : View.GONE);

                if (s.length() >= 2 || s.length() == 0) {
                    viewModel.search(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.search(binding.etSearch.getText().toString());
                hideKeyboard();
                return true;
            }
            return false;
        });

        binding.btnClearSearch.setOnClickListener(v -> {
            binding.etSearch.setText("");
            viewModel.search("");
            hideKeyboard();
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
        }
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
            binding.etSearch.setText("");
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
    }

    // ─── Observers ────────────────────────────────────────────────────────────

    private void observeViewModel() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            eventAdapter.updateData(events);

            binding.shimmerExplore.stopShimmer();
            binding.shimmerExplore.setVisibility(View.GONE);

            int count = events.size();
            binding.tvResultsCount.setText(count + " événement" + (count > 1 ? "s" : "") + " trouvé" + (count > 1 ? "s" : ""));

            if (events.isEmpty()) {
                binding.rvEvents.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.rvEvents.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.shimmerExplore.setVisibility(View.VISIBLE);
                binding.shimmerExplore.startShimmer();
                binding.rvEvents.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.GONE);
            }
        });
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    private void navigateToDetail(Event event) {
        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}