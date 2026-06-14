package com.eventmanager.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ItemEventFeaturedBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventCarouselAdapter extends RecyclerView.Adapter<EventCarouselAdapter.CarouselViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private List<Event> events = new ArrayList<>();
    private final OnEventClickListener clickListener;

    public EventCarouselAdapter(OnEventClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventFeaturedBinding binding = ItemEventFeaturedBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CarouselViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateData(List<Event> newEvents) {
        this.events = new ArrayList<>(newEvents);
        notifyDataSetChanged();
    }

    class CarouselViewHolder extends RecyclerView.ViewHolder {
        private final ItemEventFeaturedBinding binding;

        CarouselViewHolder(ItemEventFeaturedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.tvFeaturedTitle.setText(event.getTitle());
            binding.tvFeaturedLocation.setText(event.getLocation());
            binding.tvFeaturedDate.setText(DateUtils.formatShortDate(event.getDate()));
            binding.tvCategory.setText(event.getCategory().toUpperCase(Locale.getDefault()));

            if (event.isFree()) {
                binding.tvFreeBadge.setVisibility(View.VISIBLE);
            } else {
                binding.tvFreeBadge.setVisibility(View.GONE);
            }

            Glide.with(binding.getRoot().getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.placeholder_event)
                    .error(R.drawable.placeholder_event)
                    .into(binding.ivFeaturedImage);

            binding.getRoot().setOnClickListener(v -> clickListener.onEventClick(event));
        }
    }
}