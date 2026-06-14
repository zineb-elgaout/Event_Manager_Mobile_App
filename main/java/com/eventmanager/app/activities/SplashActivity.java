package com.eventmanager.app.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.eventmanager.app.R;


import androidx.appcompat.app.AppCompatActivity;

import com.eventmanager.app.databinding.ActivitySplashBinding;
import com.eventmanager.app.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        startSplashAnimation();
    }

    private void startSplashAnimation() {
        // Animate logo container: fade + scale in
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(binding.logoContainer, "alpha", 0f, 1f);
        fadeIn.setDuration(600);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(binding.logoContainer, "scaleX", 0.7f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(binding.logoContainer, "scaleY", 0.7f, 1f);
        scaleX.setDuration(700);
        scaleY.setDuration(700);
        scaleX.setInterpolator(new OvershootInterpolator(1.2f));
        scaleY.setInterpolator(new OvershootInterpolator(1.2f));

        AnimatorSet logoAnim = new AnimatorSet();
        logoAnim.playTogether(fadeIn, scaleX, scaleY);
        logoAnim.setStartDelay(200);

        // Animate loading dots
        ObjectAnimator dotsFadeIn = ObjectAnimator.ofFloat(binding.loadingDots, "alpha", 0f, 1f);
        dotsFadeIn.setDuration(400);
        dotsFadeIn.setStartDelay(900);

        AnimatorSet fullAnim = new AnimatorSet();
        fullAnim.playTogether(logoAnim, dotsFadeIn);

        fullAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animateDots();
            }
        });

        fullAnim.start();
    }

    private void animateDots() {
        View[] dots = {binding.dot1, binding.dot2, binding.dot3};
        int delay = 0;

        for (View dot : dots) {
            ObjectAnimator up = ObjectAnimator.ofFloat(dot, "translationY", 0f, -12f);
            up.setDuration(300);
            up.setStartDelay(delay);
            up.setRepeatCount(ObjectAnimator.INFINITE);
            up.setRepeatMode(ObjectAnimator.REVERSE);
            up.setInterpolator(new DecelerateInterpolator());
            up.start();
            delay += 150;
        }

        // Navigate after 2.8 seconds total
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateNext, 2800);
    }

    /*private void navigateNext() {
        Intent intent;

        if (!preferenceManager.isOnboardingDone()) {
            intent = new Intent(this, OnboardingActivity.class);
        } else if (!preferenceManager.isLoggedIn()) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }*/

    private void navigateNext() {
        startActivity(new Intent(this, OnboardingActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}