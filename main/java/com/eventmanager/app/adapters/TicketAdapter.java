package com.eventmanager.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eventmanager.app.R;
import com.eventmanager.app.database.TicketDAO;
import com.eventmanager.app.databinding.ItemTicketBinding;
import com.eventmanager.app.models.Booking;
import com.eventmanager.app.models.Ticket;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.BookingViewHolder> {

    public interface OnBookingClickListener {
        void onBookingClick(Booking booking, List<Ticket> tickets);
    }

    private List<Booking> bookings = new ArrayList<>();
    private final OnBookingClickListener listener;
    private final TicketDAO ticketDAO;

    public TicketAdapter(OnBookingClickListener listener, TicketDAO ticketDAO) {
        this.listener = listener;
        this.ticketDAO = ticketDAO;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTicketBinding binding = ItemTicketBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void updateData(List<Booking> newBookings) {
        this.bookings = new ArrayList<>(newBookings);
        notifyDataSetChanged();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemTicketBinding binding;

        BookingViewHolder(ItemTicketBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Booking booking) {
            binding.tvEventTitle.setText(booking.getEventTitle());
            binding.tvEventDate.setText(DateUtils.formatEventDate(booking.getEventDate()));
            binding.tvEventLocation.setText(booking.getEventLocation());

            binding.tvQuantityBadge.setText(
                    booking.getQuantity() + (booking.getQuantity() > 1 ? " BILLETS" : " BILLET")
            );

            List<Ticket> tickets = ticketDAO.getByBooking(booking.getId());

            // Détermine le statut global (si au moins un valide -> VALIDE, sinon utilisé/annulé)
            boolean hasValid = false;
            boolean allUsed = true;
            for (Ticket t : tickets) {
                if (t.getStatus().equals(Constants.STATUS_VALID)) hasValid = true;
                if (!t.getStatus().equals(Constants.STATUS_USED)) allUsed = false;
            }

            if (hasValid) {
                binding.tvStatus.setText("VALIDE");
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_valid);
            } else if (allUsed) {
                binding.tvStatus.setText("UTILISÉ");
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_used);
            } else {
                binding.tvStatus.setText("ANNULÉ");
                binding.tvStatus.setBackgroundResource(R.drawable.bg_status_cancelled);
            }

            Glide.with(binding.getRoot().getContext())
                    .load(booking.getEventImage())
                    .placeholder(R.drawable.placeholder_event)
                    .into(binding.ivEventImage);

            binding.getRoot().setOnClickListener(v -> listener.onBookingClick(booking, tickets));
        }
    }
}