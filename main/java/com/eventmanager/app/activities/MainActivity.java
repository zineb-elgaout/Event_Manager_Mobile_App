package com.eventmanager.app.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityMainBinding;
import com.eventmanager.app.models.Event;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    public static Event pendingFocusEvent; // simple bridge entre activities/fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
        handleIntentExtras();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) return;

        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> updateBottomNavVisibility(destination)
        );
    }

    private void handleIntentExtras() {
        boolean navigateToMap = getIntent().getBooleanExtra("navigate_to_map", false);
        if (navigateToMap) {
            Event focusEvent = (Event) getIntent().getSerializableExtra("focus_event");
            pendingFocusEvent = focusEvent;
            binding.bottomNavigation.setSelectedItemId(R.id.mapFragment);
            return;
        }

        boolean openTickets = getIntent().getBooleanExtra("open_tickets", false);
        if (openTickets) {
            binding.bottomNavigation.setSelectedItemId(R.id.ticketsFragment);
        }
    }

    private void updateBottomNavVisibility(NavDestination destination) {
        // TODO étape suivante si nécessaire
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }


}