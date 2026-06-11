package com.eventmanager.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityRegisterBinding;
import com.eventmanager.app.models.User;
import com.eventmanager.app.repository.UserRepository;
import com.eventmanager.app.utils.PreferenceManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private UserRepository userRepository;
    private PreferenceManager preferenceManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository(this);
        preferenceManager = new PreferenceManager(this);

        setupListeners();
        setupInputWatchers();
    }

    private void setupListeners() {

        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        binding.tvGoToLogin.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.etPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
        } else {
            binding.etPassword.setInputType(
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.ivTogglePassword.setImageResource(R.drawable.ic_visibility);
        }
        binding.etPassword.setSelection(binding.etPassword.getText().length());
        isPasswordVisible = !isPasswordVisible;
    }

    private void setupInputWatchers() {
        TextWatcher hideErrorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hideError();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        binding.etName.addTextChangedListener(hideErrorWatcher);
        binding.etEmail.addTextChangedListener(hideErrorWatcher);
        binding.etPassword.addTextChangedListener(hideErrorWatcher);
    }

    private void attemptRegister() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_empty_fields));
            return;
        }

        showLoading(true);

        binding.getRoot().postDelayed(() -> {
            userRepository.register(name, email, password, new UserRepository.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    showLoading(false);
                    onRegisterSuccess(user);
                }

                @Override
                public void onError(String message) {
                    showLoading(false);
                    showError(message);
                }
            });
        }, 600);
    }

    private void onRegisterSuccess(User user) {
        preferenceManager.saveUser(user.getId(), user.getName(), user.getEmail());
        preferenceManager.setLoggedIn(true);

        Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void showLoading(boolean loading) {
        binding.btnRegister.setEnabled(!loading);
        if (loading) {
            binding.btnRegister.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setText(getString(R.string.btn_register));
        }
    }

    private void showError(String message) {
        binding.tvError.setText(message);
        binding.tvError.setVisibility(View.VISIBLE);
    }

    private void hideError() {
        binding.tvError.setVisibility(View.GONE);
    }
}