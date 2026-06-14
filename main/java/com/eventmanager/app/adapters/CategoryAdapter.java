package com.eventmanager.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eventmanager.app.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    private final List<String> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView view = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.tvCategory.setText(category);

        boolean isSelected = position == selectedPosition;
        holder.tvCategory.setBackgroundResource(
                isSelected ? R.drawable.bg_category_chip_selected : R.drawable.bg_category_chip_unselected
        );
        holder.tvCategory.setTextColor(
                holder.itemView.getContext().getColor(isSelected ? R.color.white : R.color.text_secondary)
        );

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onCategoryClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;

        CategoryViewHolder(@NonNull android.view.View itemView) {
            super(itemView);
            tvCategory = (TextView) itemView;
        }
    }
}