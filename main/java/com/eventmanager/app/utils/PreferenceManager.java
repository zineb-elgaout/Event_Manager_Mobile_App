package com.eventmanager.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferenceManager {

    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    // ─── Onboarding ──────────────────────────────────────────────────────────

    public void setOnboardingDone(boolean done) {
        prefs.edit().putBoolean(Constants.KEY_ONBOARDING, done).apply();
    }

    public boolean isOnboardingDone() {
        return prefs.getBoolean(Constants.KEY_ONBOARDING, false);
    }

    // ─── Auth ─────────────────────────────────────────────────────────────────

    public void setLoggedIn(boolean loggedIn) {
        prefs.edit().putBoolean(Constants.KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public void saveUser(String id, String name, String email) {
        prefs.edit()
                .putString(Constants.KEY_USER_ID, id)
                .putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_EMAIL, email)
                .apply();
    }

    public String getUserId()    { return prefs.getString(Constants.KEY_USER_ID, ""); }
    public String getUserName()  { return prefs.getString(Constants.KEY_USER_NAME, ""); }
    public String getUserEmail() { return prefs.getString(Constants.KEY_USER_EMAIL, ""); }

    public void saveAvatar(String avatarPath) {
        prefs.edit().putString(Constants.KEY_USER_AVATAR, avatarPath).apply();
    }

    public String getAvatar() {
        return prefs.getString(Constants.KEY_USER_AVATAR, "");
    }

    // ─── Category preferences ────────────────────────────────────────────────

    public void saveCategoryPreferences(Set<String> categories) {
        prefs.edit().putStringSet(Constants.KEY_CATEGORIES, categories).apply();
    }

    public Set<String> getCategoryPreferences() {
        return prefs.getStringSet(Constants.KEY_CATEGORIES, new HashSet<>());
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    public void logout() {
        prefs.edit()
                .remove(Constants.KEY_IS_LOGGED_IN)
                .remove(Constants.KEY_USER_ID)
                .remove(Constants.KEY_USER_NAME)
                .remove(Constants.KEY_USER_EMAIL)
                .remove(Constants.KEY_USER_AVATAR)
                .apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }
}