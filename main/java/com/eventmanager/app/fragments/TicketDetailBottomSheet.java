package com.eventmanager.app.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityTicketDetailBinding;
import com.eventmanager.app.databinding.ItemSingleTicketBinding;
import com.eventmanager.app.models.Booking;
import com.eventmanager.app.models.Ticket;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.DateUtils;
import com.eventmanager.app.utils.QRCodeGenerator;
import com.eventmanager.app.utils.ShareUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class TicketDetailBottomSheet extends BottomSheetDialogFragment {

    private ActivityTicketDetailBinding binding;
    private Booking booking;
    private List<Ticket> tickets;

    public static TicketDetailBottomSheet newInstance(Booking booking, List<Ticket> tickets) {
        TicketDetailBottomSheet sheet = new TicketDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("booking", booking);
        args.putSerializable("tickets", new java.util.ArrayList<>(tickets));
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityTicketDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            booking = (Booking) getArguments().getSerializable("booking");
            tickets = (List<Ticket>) getArguments().getSerializable("tickets");
        }

        if (booking == null || tickets == null) {
            dismiss();
            return;
        }

        bindHeader();
        setupViewPager();
        setupListeners();
    }

    private void bindHeader() {
        binding.tvEventTitle.setText(booking.getEventTitle());
        binding.tvEventDate.setText(DateUtils.formatEventDate(booking.getEventDate()));
        binding.tvEventLocation.setText(booking.getEventLocation());
    }

    private void setupViewPager() {
        TicketPageAdapter adapter = new TicketPageAdapter(tickets);
        binding.viewPagerTickets.setAdapter(adapter);

        if (tickets.size() > 1) {
            binding.dotsIndicator.setVisibility(View.VISIBLE);
            binding.dotsIndicator.attachTo(binding.viewPagerTickets);
        } else {
            binding.dotsIndicator.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        binding.btnClose.setOnClickListener(v -> dismiss());

        binding.btnShare.setOnClickListener(v -> {
            int currentPosition = binding.viewPagerTickets.getCurrentItem();
            if (currentPosition < tickets.size()) {
                ShareUtils.shareTicketViaSms(requireContext(), tickets.get(currentPosition));
            }
        });
    }

    // ─── Inner ViewPager Adapter ──────────────────────────────────────────────

    static class TicketPageAdapter extends RecyclerView.Adapter<TicketPageAdapter.TicketPageViewHolder> {

        private final List<Ticket> tickets;

        TicketPageAdapter(List<Ticket> tickets) {
            this.tickets = tickets;
        }

        @NonNull
        @Override
        public TicketPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSingleTicketBinding binding = ItemSingleTicketBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new TicketPageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TicketPageViewHolder holder, int position) {
            holder.bind(tickets.get(position));
        }

        @Override
        public int getItemCount() {
            return tickets.size();
        }

        static class TicketPageViewHolder extends RecyclerView.ViewHolder {
            private final ItemSingleTicketBinding binding;

            TicketPageViewHolder(ItemSingleTicketBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            void bind(Ticket ticket) {
                binding.tvSeatNumber.setText("PLACE N°" + ticket.getSeatNumber());
                binding.tvTicketCode.setText(ticket.getQrCode());

                Bitmap qr = QRCodeGenerator.generate(ticket.getQrCode(), 500);
                if (qr != null) binding.ivQrCode.setImageBitmap(qr);

                switch (ticket.getStatus()) {
                    case Constants.STATUS_VALID:
                        binding.tvTicketStatus.setText("VALIDE");
                        binding.tvTicketStatus.setBackgroundResource(R.drawable.bg_status_valid);
                        binding.ivQrCode.setAlpha(1f);
                        break;
                    case Constants.STATUS_USED:
                        binding.tvTicketStatus.setText("UTILISÉ");
                        binding.tvTicketStatus.setBackgroundResource(R.drawable.bg_status_used);
                        binding.ivQrCode.setAlpha(0.3f);
                        break;
                    default:
                        binding.tvTicketStatus.setText("ANNULÉ");
                        binding.tvTicketStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
                        binding.ivQrCode.setAlpha(0.3f);
                }
            }
        }
    }
}