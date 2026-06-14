package com.eventmanager.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ItemEventCardBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private final OnEventClickListener clickListener;
    private final OnFavoriteClickListener favoriteListener;

    public EventAdapter(OnEventClickListener clickListener, OnFavoriteClickListener favoriteListener) {
        this.clickListener = clickListener;
        this.favoriteListener = favoriteListener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventCardBinding binding = ItemEventCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new EventViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateData(List<Event> newEvents) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EventDiffCallback(this.events, newEvents));
        this.events = new ArrayList<>(newEvents);
        diffResult.dispatchUpdatesTo(this);
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventCardBinding binding;

        EventViewHolder(ItemEventCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.tvEventTitle.setText(event.getTitle());
            binding.tvEventLocation.setText(event.getLocation());
            binding.tvEventDate.setText(DateUtils.formatEventDate(event.getDate()));
            binding.tvCategory.setText(event.getCategory().toUpperCase(Locale.getDefault()));

            if (event.isFree()) {
                binding.tvPrice.setText(R.string.free);
            } else {
                binding.tvPrice.setText(String.format(Locale.getDefault(), "%.0f MAD", event.getPrice()));
            }

            binding.ivFavorite.setImageResource(
                    event.isFavorite() ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline
            );

            Glide.with(binding.getRoot().getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(binding.ivEventImage);

            binding.getRoot().setOnClickListener(v -> clickListener.onEventClick(event));
            binding.ivFavorite.setOnClickListener(v -> {
                favoriteListener.onFavoriteClick(event);
                binding.ivFavorite.setImageResource(
                        event.isFavorite() ? R.drawable.ic_heart_outline : R.drawable.ic_heart_filled
                );
            });
        }
    }

    private static class EventDiffCallback extends DiffUtil.Callback {
        private final List<Event> oldList;
        private final List<Event> newList;

        EventDiffCallback(List<Event> oldList, List<Event> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override public int getOldListSize() { return oldList.size(); }
        @Override public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return oldList.get(oldPos).getId().equals(newList.get(newPos).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            Event o = oldList.get(oldPos);
            Event n = newList.get(newPos);
            return o.equals(n) && o.isFavorite() == n.isFavorite();
        }
    }
}