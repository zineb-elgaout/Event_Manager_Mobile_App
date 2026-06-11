package com.eventmanager.app.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import com.eventmanager.app.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Repository d'authentification.
 * Étape 2 : stockage local simple via SharedPreferences (simulateur de backend).
 * Étape 3+ : sera remplacé/complété par Retrofit + API réelle.
 */
public class UserRepository {

    private static final String PREF_USERS = "registered_users";
    private final SharedPreferences usersPrefs;

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public UserRepository(Context context) {
        usersPrefs = context.getSharedPreferences(PREF_USERS, Context.MODE_PRIVATE);
        seedDefaultAccount();
    }

    /** Compte de démo pré-enregistré pour tester rapidement le login */
    private void seedDefaultAccount() {
        if (!usersPrefs.contains("demo@eventmanager.com")) {
            usersPrefs.edit()
                    .putString("demo@eventmanager.com", "demo1234|Utilisateur Démo|" + UUID.randomUUID())
                    .apply();
        }
    }

    // ─── Validation ───────────────────────────────────────────────────────────

    public boolean isValidEmail(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2;
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    public void login(String email, String password, AuthCallback callback) {
        if (!isValidEmail(email)) {
            callback.onError("Adresse e-mail invalide.");
            return;
        }
        if (!isValidPassword(password)) {
            callback.onError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        String stored = usersPrefs.getString(email.toLowerCase().trim(), null);

        if (stored == null) {
            callback.onError("Aucun compte associé à cet e-mail.");
            return;
        }

        String[] parts = stored.split("\\|");
        String storedPassword = parts[0];
        String storedName = parts.length > 1 ? parts[1] : "";
        String storedId = parts.length > 2 ? parts[2] : UUID.randomUUID().toString();

        if (!storedPassword.equals(password)) {
            callback.onError("Mot de passe incorrect.");
            return;
        }

        User user = new User(storedId, storedName, email.toLowerCase().trim(), password);
        callback.onSuccess(user);
    }

    // ─── Register ─────────────────────────────────────────────────────────────

    public void register(String name, String email, String password, AuthCallback callback) {
        if (!isValidName(name)) {
            callback.onError("Le nom doit contenir au moins 2 caractères.");
            return;
        }
        if (!isValidEmail(email)) {
            callback.onError("Adresse e-mail invalide.");
            return;
        }
        if (!isValidPassword(password)) {
            callback.onError("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        String key = email.toLowerCase().trim();

        if (usersPrefs.contains(key)) {
            callback.onError("Un compte existe déjà avec cet e-mail.");
            return;
        }

        String userId = UUID.randomUUID().toString();
        String value = password + "|" + name.trim() + "|" + userId;

        usersPrefs.edit().putString(key, value).apply();

        User user = new User(userId, name.trim(), key, password);
        callback.onSuccess(user);
    }
}