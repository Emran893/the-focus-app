package com.thefocuslive.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean launched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // True fullscreen — immersive like Netflix
        getWindow().setStatusBarColor(Color.parseColor("#1a0000"));
        getWindow().setNavigationBarColor(Color.parseColor("#1a0000"));
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        ImageView logoF    = findViewById(R.id.splash_logo_f);
        ImageView logoFull = findViewById(R.id.splash_logo_full);

        netflixAnimation(logoF, logoFull);
    }

    private void netflixAnimation(ImageView logoF, ImageView logoFull) {

        // ── Phase 1: F. lettermark fades in from center (Netflix style) ──
        // Start invisible, scale 0.85 → 1.0, alpha 0 → 1
        logoF.setAlpha(0f);
        logoF.setScaleX(0.85f);
        logoF.setScaleY(0.85f);
        logoFull.setAlpha(0f);
        logoFull.setScaleX(0.95f);
        logoFull.setScaleY(0.95f);

        // F. fade in — Netflix slow elegant reveal
        ObjectAnimator fAlpha = ObjectAnimator.ofFloat(logoF, "alpha", 0f, 1f);
        fAlpha.setDuration(800);
        fAlpha.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator fScaleX = ObjectAnimator.ofFloat(logoF, "scaleX", 0.85f, 1f);
        fScaleX.setDuration(800);
        fScaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator fScaleY = ObjectAnimator.ofFloat(logoF, "scaleY", 0.85f, 1f);
        fScaleY.setDuration(800);
        fScaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet phase1 = new AnimatorSet();
        phase1.playTogether(fAlpha, fScaleX, fScaleY);
        phase1.setStartDelay(300);

        // ── Phase 2: F. fades out, "The Focus" fades in ──
        ObjectAnimator fFadeOut = ObjectAnimator.ofFloat(logoF, "alpha", 1f, 0f);
        fFadeOut.setDuration(400);
        fFadeOut.setInterpolator(new LinearInterpolator());
        fFadeOut.setStartDelay(200);

        ObjectAnimator fullAlpha = ObjectAnimator.ofFloat(logoFull, "alpha", 0f, 1f);
        fullAlpha.setDuration(600);
        fullAlpha.setInterpolator(new AccelerateDecelerateInterpolator());
        fullAlpha.setStartDelay(300);

        ObjectAnimator fullScale = ObjectAnimator.ofFloat(logoFull, "scaleX", 0.95f, 1f);
        fullScale.setDuration(600);
        fullScale.setInterpolator(new AccelerateDecelerateInterpolator());
        fullScale.setStartDelay(300);

        ObjectAnimator fullScaleY = ObjectAnimator.ofFloat(logoFull, "scaleY", 0.95f, 1f);
        fullScaleY.setDuration(600);
        fullScaleY.setInterpolator(new AccelerateDecelerateInterpolator());
        fullScaleY.setStartDelay(300);

        AnimatorSet phase2 = new AnimatorSet();
        phase2.playTogether(fFadeOut, fullAlpha, fullScale, fullScaleY);
        phase2.setStartDelay(1300); // After F. held for ~1s

        // ── Full animation sequence ──
        AnimatorSet full = new AnimatorSet();
        full.playSequentially(phase1, phase2);
        full.start();

        // Launch app after The Focus logo held for 0.8s
        // Total: 300ms delay + 800ms phase1 + 200ms gap + 1300ms + 600ms phase2 + 800ms hold
        new Handler(Looper.getMainLooper()).postDelayed(this::launchApp, 3200);
    }

    private void launchApp() {
        if (launched) return;
        launched = true;

        // Fade out entire splash to black → app loads seamlessly
        View root = findViewById(R.id.splash_root);
        root.animate()
            .alpha(0f)
            .setDuration(300)
            .setInterpolator(new LinearInterpolator())
            .withEndAction(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            })
            .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Prevent stuck splash if activity resumes
        new Handler(Looper.getMainLooper()).postDelayed(this::launchApp, 5000);
    }
}
