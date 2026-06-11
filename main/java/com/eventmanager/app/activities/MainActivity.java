package com.eventmanager.app.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) return;

        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // Smooth bottom nav visibility based on destination
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> updateBottomNavVisibility(destination)
        );
    }

    private void updateBottomNavVisibility(NavDestination destination) {
        /* int id = destination.getId();
        boolean shouldHide = (id == R.id.eventDetailFragment || id == R.id.bookingFragment);

        if (shouldHide) {
            binding.bottomNavigation.animate()
                    .translationY(binding.bottomNavigation.getHeight())
                    .setDuration(250)
                    .start();
        } else {
            binding.bottomNavigation.animate()
                    .translationY(0)
                    .setDuration(250)
                    .start();
        }*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}