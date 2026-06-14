package com.eventmanager.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eventmanager.app.R;
import com.eventmanager.app.activities.LoginActivity;
import com.eventmanager.app.adapters.CategoryAdapter;
import com.eventmanager.app.database.BookingDAO;
import com.eventmanager.app.database.TicketDAO;
import com.eventmanager.app.databinding.FragmentProfileBinding;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.utils.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private PreferenceManager preferenceManager;
    private BookingDAO bookingDAO;
    private TicketDAO ticketDAO;

    private Set<String> selectedCategories = new HashSet<>();

    private static final List<String> ALL_CATEGORIES = Arrays.asList(
            Constants.CAT_CONCERT,
            Constants.CAT_CONFERENCE,
            Constants.CAT_EXPO,
            Constants.CAT_SPORT,
            Constants.CAT_FESTIVAL,
            Constants.CAT_THEATER
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());
        bookingDAO = new BookingDAO(requireContext());
        ticketDAO = new TicketDAO(requireContext());

        bindUserInfo();
        loadStats();
        setupCategoryPreferences();
        setupMenuListeners();
    }

    // ─── User Info ────────────────────────────────────────────────────────────

    private void bindUserInfo() {
        String name = preferenceManager.getUserName();
        String email = preferenceManager.getUserEmail();

        binding.tvUserName.setText(name.isEmpty() ? "Utilisateur" : name);
        binding.tvUserEmail.setText(email);

        // Initiales dans l'avatar si pas de photo
        String initials = "";
        if (!name.isEmpty()) {
            String[] parts = name.split(" ");
            initials = parts[0].substring(0, 1).toUpperCase();
            if (parts.length > 1) initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
        }
        // L'avatar utilise placeholder_avatar (oval rouge/violet)
        // Une vraie photo peut être ajoutée via Gallery intent
    }

    // ─── Stats ────────────────────────────────────────────────────────────────

    private void loadStats() {
        String userId = preferenceManager.getUserId();

        // Nombre total de réservations uniques
        int totalEvents = bookingDAO.getTotalEventsAttended(userId);

        // Nombre total de tickets
        int totalTickets = ticketDAO.getByUser(userId).size();

        // Nombre de favoris (stocké en mémoire dans ViewModel — on met 0 ici
        // car les favoris ne sont pas encore persistés en base)
        int favorites = preferenceManager.getCategoryPreferences().size();

        binding.tvStatEvents.setText(String.valueOf(totalEvents));
        binding.tvStatTickets.setText(String.valueOf(totalTickets));
        binding.tvStatFavorites.setText(String.valueOf(ALL_CATEGORIES.size()));
        binding.tvFavoritesCount.setText(String.valueOf(totalTickets));

        // Animation count-up sur les stats
        animateCounter(binding.tvStatEvents, totalEvents);
        animateCounter(binding.tvStatTickets, totalTickets);
    }

    private void animateCounter(android.widget.TextView textView, int target) {
        if (target == 0) return;
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(0, target);
        animator.setDuration(800);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(anim ->
                textView.setText(String.valueOf((int) anim.getAnimatedValue()))
        );
        animator.start();
    }

    // ─── Category Preferences ─────────────────────────────────────────────────

    private void setupCategoryPreferences() {
        selectedCategories = new HashSet<>(preferenceManager.getCategoryPreferences());

        CategoryPrefAdapter adapter = new CategoryPrefAdapter(ALL_CATEGORIES, selectedCategories, category -> {
            if (selectedCategories.contains(category)) {
                selectedCategories.remove(category);
            } else {
                selectedCategories.add(category);
            }
            preferenceManager.saveCategoryPreferences(selectedCategories);
        });

        binding.rvCategoryPrefs.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategoryPrefs.setAdapter(adapter);
    }

    // ─── Menu Listeners ───────────────────────────────────────────────────────

    private void setupMenuListeners() {
        binding.btnSettings.setOnClickListener(v ->
                Toast.makeText(getContext(), "Paramètres à venir", Toast.LENGTH_SHORT).show()
        );

        binding.menuBookings.setOnClickListener(v -> {
            requireActivity().findViewById(R.id.bottomNavigation)
                    .performClick();
            // Navigate to Tickets tab
            ((com.google.android.material.bottomnavigation.BottomNavigationView)
                    requireActivity().findViewById(R.id.bottomNavigation))
                    .setSelectedItemId(R.id.ticketsFragment);
        });

        binding.menuFavorites.setOnClickListener(v ->
                Toast.makeText(getContext(), "Favoris — persistance en base à venir", Toast.LENGTH_SHORT).show()
        );

        binding.menuNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
            startActivity(intent);
        });

        binding.menuAbout.setOnClickListener(v ->
                showAboutDialog()
        );

        binding.btnEditAvatar.setOnClickListener(v ->
                Toast.makeText(getContext(), "Changer la photo de profil (Galerie)", Toast.LENGTH_SHORT).show()
        );

        binding.btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Déconnexion")
                .setMessage("Êtes-vous sûr de vouloir vous déconnecter ?")
                .setPositiveButton("Déconnexion", (dialog, which) -> logout())
                .setNegativeButton("Annuler", null)
                .show();
    }

    private void logout() {
        preferenceManager.logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        requireActivity().finish();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("EventManager v1.0")
                .setMessage("Application de découverte et réservation d'événements.\n\n" +
                        "Développée avec Java + Android SDK\n" +
                        "Architecture MVVM · SQLite · Retrofit · Google Maps\n\n" +
                        "© 2026 EventManager")
                .setPositiveButton("Fermer", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ─── Inner Adapter : Category Preferences ────────────────────────────────

    static class CategoryPrefAdapter
            extends androidx.recyclerview.widget.RecyclerView.Adapter<CategoryPrefAdapter.PrefViewHolder> {

        interface OnToggleListener {
            void onToggle(String category);
        }

        private final List<String> categories;
        private final Set<String> selected;
        private final OnToggleListener listener;

        CategoryPrefAdapter(List<String> categories, Set<String> selected, OnToggleListener listener) {
            this.categories = categories;
            this.selected = selected;
            this.listener = listener;
        }

        @NonNull
        @Override
        public PrefViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            android.widget.TextView tv = (android.widget.TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            return new PrefViewHolder(tv);
        }

        @Override
        public void onBindViewHolder(@NonNull PrefViewHolder holder, int position) {
            String cat = categories.get(position);
            boolean isSelected = selected.contains(cat);

            holder.tv.setText(cat);
            holder.tv.setBackgroundResource(
                    isSelected ? R.drawable.bg_category_chip_selected : R.drawable.bg_category_chip_unselected
            );
            holder.tv.setTextColor(
                    holder.tv.getContext().getColor(isSelected ? R.color.white : R.color.text_secondary)
            );

            holder.tv.setOnClickListener(v -> {
                listener.onToggle(cat);
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() { return categories.size(); }

        static class PrefViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            android.widget.TextView tv;
            PrefViewHolder(android.widget.TextView v) {
                super(v);
                tv = v;
            }
        }
    }
}