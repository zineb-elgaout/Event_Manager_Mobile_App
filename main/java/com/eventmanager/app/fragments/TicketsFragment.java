package com.eventmanager.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eventmanager.app.R;
import com.eventmanager.app.activities.QRScanActivity;
import com.eventmanager.app.adapters.TicketAdapter;
import com.eventmanager.app.database.BookingDAO;
import com.eventmanager.app.database.TicketDAO;
import com.eventmanager.app.databinding.FragmentTicketsBinding;
import com.eventmanager.app.models.Booking;
import com.eventmanager.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment {

    private FragmentTicketsBinding binding;
    private BookingDAO bookingDAO;
    private TicketDAO ticketDAO;
    private TicketAdapter adapter;
    private PreferenceManager preferenceManager;

    private List<Booking> allBookings = new ArrayList<>();
    private int currentTab = 0; // 0 = upcoming, 1 = past

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTicketsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookingDAO = new BookingDAO(requireContext());
        ticketDAO = new TicketDAO(requireContext());
        preferenceManager = new PreferenceManager(requireContext());

        setupRecyclerView();
        setupTabs();
        setupScanButton();
        loadBookings();
    }

    private void setupRecyclerView() {
        adapter = new TicketAdapter((booking, tickets) -> {
            TicketDetailBottomSheet sheet = TicketDetailBottomSheet.newInstance(booking, tickets);
            sheet.show(getParentFragmentManager(), "ticket_detail");
        }, ticketDAO);

        binding.rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTickets.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabUpcoming.setOnClickListener(v -> selectTab(0));
        binding.tabPast.setOnClickListener(v -> selectTab(1));
    }

    private void selectTab(int tab) {
        currentTab = tab;

        if (tab == 0) {
            binding.tabUpcoming.setBackgroundResource(R.drawable.bg_category_chip_selected);
            binding.tabUpcoming.setTextColor(getResources().getColor(R.color.white, null));
            binding.tabPast.setBackground(null);
            binding.tabPast.setTextColor(getResources().getColor(R.color.text_secondary, null));
        } else {
            binding.tabPast.setBackgroundResource(R.drawable.bg_category_chip_selected);
            binding.tabPast.setTextColor(getResources().getColor(R.color.white, null));
            binding.tabUpcoming.setBackground(null);
            binding.tabUpcoming.setTextColor(getResources().getColor(R.color.text_secondary, null));
        }

        filterAndDisplay();
    }

    private void setupScanButton() {
        binding.btnScanQR.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), QRScanActivity.class));
        });
    }

    private void loadBookings() {
        String userId = preferenceManager.getUserId();
        allBookings = bookingDAO.getByUser(userId);
        filterAndDisplay();
    }

    private void filterAndDisplay() {
        long now = System.currentTimeMillis();
        List<Booking> filtered = new ArrayList<>();

        for (Booking b : allBookings) {
            boolean isUpcoming = b.getEventDate() >= now;
            if (currentTab == 0 && isUpcoming) {
                filtered.add(b);
            } else if (currentTab == 1 && !isUpcoming) {
                filtered.add(b);
            }
        }

        adapter.updateData(filtered);

        if (filtered.isEmpty()) {
            binding.emptyState.setVisibility(View.VISIBLE);
            binding.rvTickets.setVisibility(View.GONE);
            binding.tvEmptyTitle.setText(
                    currentTab == 0 ? "Aucun billet à venir" : "Aucun événement passé"
            );
        } else {
            binding.emptyState.setVisibility(View.GONE);
            binding.rvTickets.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            loadBookings();
            com.eventmanager.app.widgets.EventWidgetProvider.updateAllWidgets(requireContext());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}