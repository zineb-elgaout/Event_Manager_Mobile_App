package com.eventmanager.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.eventmanager.app.R;
import com.eventmanager.app.databinding.ActivityLoginBinding;
import com.eventmanager.app.models.User;
import com.eventmanager.app.repository.UserRepository;
import com.eventmanager.app.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserRepository userRepository;
    private PreferenceManager preferenceManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository(this);
        preferenceManager = new PreferenceManager(this);

        setupListeners();
        setupInputWatchers();
    }

    private void setupListeners() {

        // Toggle password visibility
        binding.ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        // Login button
        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        // Go to register
        binding.tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Forgot password (placeholder)
        binding.tvForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Fonctionnalité à venir", Toast.LENGTH_SHORT).show()
        );

        // Google login (placeholder)
        binding.btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "Connexion Google à venir", Toast.LENGTH_SHORT).show()
        );
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

        binding.etEmail.addTextChangedListener(hideErrorWatcher);
        binding.etPassword.addTextChangedListener(hideErrorWatcher);
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.error_empty_fields));
            return;
        }

        showLoading(true);

        // Simulate small network delay for realism
        binding.getRoot().postDelayed(() -> {
            userRepository.login(email, password, new UserRepository.AuthCallback() {
                @Override
                public void onSuccess(User user) {
                    showLoading(false);
                    onLoginSuccess(user);
                }

                @Override
                public void onError(String message) {
                    showLoading(false);
                    showError(message);
                }
            });
        }, 600);
    }

    private void onLoginSuccess(User user) {
        preferenceManager.saveUser(user.getId(), user.getName(), user.getEmail());
        preferenceManager.setLoggedIn(true);

        Toast.makeText(this, "Bienvenue " + user.getName() + " !", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void showLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        if (loading) {
            binding.btnLogin.setText("");
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setText(getString(R.string.btn_login));
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