package com.thefocuslive.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Full screen — no status bar
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.splash_logo);
        FrameLayout root = (FrameLayout) logo.getParent();

        startAnimation(logo, root);
    }

    private void startAnimation(ImageView logo, FrameLayout root) {

        // ── Phase 1: Fade in + Flip (white bg, logo appears) ──
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        fadeIn.setDuration(250);
        fadeIn.setStartDelay(200);

        // Flip: scaleX 0 → 1 with overshoot (like card flip)
        ObjectAnimator flipX = ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f);
        flipX.setDuration(550);
        flipX.setStartDelay(200);
        flipX.setInterpolator(new OvershootInterpolator(2f));

        // Slight bounce on Y too
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(logo, "scaleY", 0.8f, 1f);
        scaleY1.setDuration(400);
        scaleY1.setStartDelay(300);
        scaleY1.setInterpolator(new OvershootInterpolator(1.5f));

        AnimatorSet phase1 = new AnimatorSet();
        phase1.playTogether(fadeIn, flipX, scaleY1);

        // ── Phase 2: Hold for a moment ──
        // (handled by delay in phase3)

        // ── Phase 3: Zoom toward user, white fades ──
        // Logo zooms to ~3x (near the screen edges but not full)
        ObjectAnimator zoomX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 3.5f);
        zoomX.setDuration(500);
        zoomX.setInterpolator(new AccelerateInterpolator(2f));

        ObjectAnimator zoomY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 3.5f);
        zoomY.setDuration(500);
        zoomY.setInterpolator(new AccelerateInterpolator(2f));

        // Fade logo out during zoom
        ObjectAnimator logoFade = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f);
        logoFade.setDuration(350);
        logoFade.setStartDelay(200);

        // Fade background to transparent/white
        ObjectAnimator bgFade = ObjectAnimator.ofFloat(root, "alpha", 1f, 0f);
        bgFade.setDuration(400);
        bgFade.setStartDelay(200);

        AnimatorSet phase3 = new AnimatorSet();
        phase3.playTogether(zoomX, zoomY, logoFade, bgFade);
        phase3.setStartDelay(900); // Wait after flip

        AnimatorSet full = new AnimatorSet();
        full.playSequentially(phase1, phase3);
        full.start();

        // Launch main app after animation completes
        logo.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Smooth crossfade into app
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 1700);
    }
}
