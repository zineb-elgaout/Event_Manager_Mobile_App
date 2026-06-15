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
import com.eventmanager.app.activities.EventDetailActivity;
import com.eventmanager.app.adapters.EventAdapter;
import com.eventmanager.app.databinding.FragmentFavoritesBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.viewmodel.EventViewModel;

public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private EventViewModel viewModel;
    private EventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);

        setupRecyclerView();
        setupListeners();
        observeViewModel();

        // Charge les favoris dès l'ouverture
        viewModel.loadFavorites();
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter(
                event -> navigateToDetail(event),
                event -> {
                    // Supprime des favoris directement depuis cette page
                    viewModel.toggleFavorite(event);
                }
        );
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFavorites.setAdapter(adapter);
    }

    private void navigateToDetail(Event event) {
        // L'event vient de SQLite favorites — il a tous les champs nécessaires
        Intent intent = new Intent(requireActivity(), EventDetailActivity.class);
        intent.putExtra("event", event);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    private void observeViewModel() {
        viewModel.getFavoriteEvents().observe(getViewLifecycleOwner(), events -> {
            adapter.updateData(events);
            binding.tvCount.setText(String.valueOf(events.size()));

            if (events.isEmpty()) {
                binding.rvFavorites.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.rvFavorites.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) viewModel.loadFavorites();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}