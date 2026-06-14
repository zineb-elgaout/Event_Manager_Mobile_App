package com.eventmanager.app.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.SupportMapFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.eventmanager.app.R;
import com.eventmanager.app.activities.EventDetailActivity;
import com.eventmanager.app.adapters.CategoryAdapter;
import com.eventmanager.app.databinding.FragmentMapBinding;
import com.eventmanager.app.models.Event;
import com.eventmanager.app.utils.Constants;
import com.eventmanager.app.viewmodel.EventViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private EventViewModel viewModel;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;

    private final Map<Marker, Event> markerEventMap = new HashMap<>();
    private List<Event> allEvents = new ArrayList<>();
    private Event selectedEvent;

    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.googleMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        setupCategoryFilter();
        setupButtons();
        observeEvents();
        checkPendingFocusEvent();
    }

    private void checkPendingFocusEvent() {
        Event focusEvent = com.eventmanager.app.activities.MainActivity.pendingFocusEvent;
        if (focusEvent != null) {
            com.eventmanager.app.activities.MainActivity.pendingFocusEvent = null;

            // Attendre que la map et les markers soient prêts
            binding.getRoot().postDelayed(() -> {
                for (Map.Entry<Marker, Event> entry : markerEventMap.entrySet()) {
                    if (entry.getValue().getId().equals(focusEvent.getId())) {
                        selectEvent(entry.getValue(), entry.getKey());
                        break;
                    }
                }
            }, 600);
        }
    }
    // ─── Map Setup ────────────────────────────────────────────────────────────

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark));
        } catch (Exception ignored) {}

        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        googleMap.setOnMarkerClickListener(marker -> {
            Event event = markerEventMap.get(marker);
            if (event != null) {
                selectEvent(event, marker);
            }
            return true; // Empêche le comportement par défaut (centrer + infowindow)
        });

        googleMap.setOnMapClickListener(latLng -> deselectEvent());

        checkLocationPermission();

        // Si les events sont déjà chargés (observer déclenché avant la map prête)
        if (!allEvents.isEmpty()) {
            addMarkersToMap(allEvents);
        }
    }

    // ─── Category Filter ──────────────────────────────────────────────────────

    private void setupCategoryFilter() {
        List<String> categories = Arrays.asList(
                Constants.CAT_ALL,
                Constants.CAT_CONCERT,
                Constants.CAT_CONFERENCE,
                Constants.CAT_EXPO,
                Constants.CAT_SPORT,
                Constants.CAT_FESTIVAL,
                Constants.CAT_THEATER
        );

        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            String filter = category.equals(Constants.CAT_ALL) ? null : category;
            viewModel.filterByCategory(filter);
            deselectEvent();
        });

        binding.rvMapCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvMapCategories.setAdapter(adapter);
    }

    // ─── Buttons ──────────────────────────────────────────────────────────────

    private void setupButtons() {
        binding.btnMyLocation.setOnClickListener(v -> centerOnUserLocation());

        binding.btnShowAll.setOnClickListener(v -> {
            deselectEvent();
            fitAllMarkers();
        });

        binding.btnViewEvent.setOnClickListener(v -> {
            if (selectedEvent != null) {
                Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                intent.putExtra("event", selectedEvent);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // ─── Observe Events ───────────────────────────────────────────────────────

    private void observeEvents() {
        viewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
            allEvents = events;
            binding.tvResultsCount.setText("📍 " + events.size() + " événement" + (events.size() != 1 ? "s" : ""));

            if (googleMap != null) {
                addMarkersToMap(events);
            }
        });
    }

    // ─── Markers ──────────────────────────────────────────────────────────────

    private void addMarkersToMap(List<Event> events) {
        googleMap.clear();
        markerEventMap.clear();

        BitmapDescriptor markerIcon = getMarkerIcon(R.drawable.ic_map_marker);

        for (Event event : events) {
            LatLng position = new LatLng(event.getLatitude(), event.getLongitude());

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(event.getTitle())
                    .icon(markerIcon)
                    .anchor(0.5f, 1f)
            );

            if (marker != null) {
                markerEventMap.put(marker, event);
            }
        }

        if (!events.isEmpty()) {
            fitAllMarkers();
        }
    }

    private BitmapDescriptor getMarkerIcon(int drawableRes) {
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableRes, null);
        if (drawable == null) return BitmapDescriptorFactory.defaultMarker();

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        drawable.setBounds(0, 0, width, height);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void fitAllMarkers() {
        if (markerEventMap.isEmpty() || googleMap == null) return;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Event event : markerEventMap.values()) {
            builder.include(new LatLng(event.getLatitude(), event.getLongitude()));
        }

        try {
            LatLngBounds bounds = builder.build();
            int padding = dpToPx(80);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
        } catch (Exception e) {
            // Cas d'un seul point : bounds invalide
            if (!markerEventMap.isEmpty()) {
                Event first = markerEventMap.values().iterator().next();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(first.getLatitude(), first.getLongitude()), 10f
                ));
            }
        }
    }

    // ─── Selection ────────────────────────────────────────────────────────────

    private void selectEvent(Event event, Marker marker) {
        selectedEvent = event;

        binding.hintContainer.setVisibility(View.GONE);
        binding.selectedEventCard.setVisibility(View.VISIBLE);

        binding.tvSelectedCategory.setText(event.getCategory().toUpperCase(Locale.getDefault()));
        binding.tvSelectedTitle.setText(event.getTitle());
        binding.tvSelectedLocation.setText(event.getLocation());

        if (event.isFree()) {
            binding.tvSelectedPrice.setText(getString(R.string.free));
        } else {
            binding.tvSelectedPrice.setText(String.format(Locale.getDefault(), "%.0f MAD", event.getPrice()));
        }

        // Distance
        if (userLocation != null) {
            float[] result = new float[1];
            Location.distanceBetween(
                    userLocation.getLatitude(), userLocation.getLongitude(),
                    event.getLatitude(), event.getLongitude(),
                    result
            );
            float km = result[0] / 1000f;
            binding.tvSelectedDistance.setText(String.format(Locale.getDefault(), "%.1f km", km));
            binding.tvSelectedDistance.setVisibility(View.VISIBLE);
        } else {
            binding.tvSelectedDistance.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(event.getImageUrl())
                .placeholder(R.drawable.placeholder_event)
                .into(binding.ivSelectedEventImage);

        // Center map slightly above the marker so the bottom sheet doesn't cover it
        LatLng position = marker.getPosition();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, Math.max(googleMap.getCameraPosition().zoom, 11f)));
    }

    private void deselectEvent() {
        selectedEvent = null;
        binding.selectedEventCard.setVisibility(View.GONE);
        binding.hintContainer.setVisibility(View.VISIBLE);
    }

    // ─── Location Permission ─────────────────────────────────────────────────

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        } else {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void enableUserLocation() {
        if (googleMap == null) return;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); // on a notre propre bouton

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = location;
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    private void centerOnUserLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
            return;
        }

        if (userLocation != null) {
            LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f));
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    userLocation = location;
                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f));
                } else {
                    android.widget.Toast.makeText(getContext(), "Position non disponible. Activez le GPS.", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}