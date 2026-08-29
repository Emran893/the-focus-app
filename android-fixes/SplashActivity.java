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

        // Full immersive screen
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.splash_logo);
        startAnimation(logo);
    }

    private void startAnimation(ImageView logo) {

        // ── Phase 1: Logo appears with flip (scaleX 0 → 1) ──
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.setStartDelay(300);

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f);
        flipIn.setDuration(600);
        flipIn.setStartDelay(300);
        flipIn.setInterpolator(new OvershootInterpolator(1.8f));

        ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(logo, "scaleY", 0.85f, 1f);
        scaleYIn.setDuration(500);
        scaleYIn.setStartDelay(350);
        scaleYIn.setInterpolator(new OvershootInterpolator(1.5f));

        AnimatorSet phase1 = new AnimatorSet();
        phase1.playTogether(fadeIn, flipIn, scaleYIn);

        // ── Phase 2: Logo holds for 1 second (do nothing — just delay) ──

        // ── Phase 3: Slow zoom to full screen ──
        // Scale from 1x to 25x — logo covers full screen smoothly
        ObjectAnimator zoomX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 25f);
        zoomX.setDuration(1200);
        zoomX.setInterpolator(new AccelerateInterpolator(1.5f));

        ObjectAnimator zoomY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 25f);
        zoomY.setDuration(1200);
        zoomY.setInterpolator(new AccelerateInterpolator(1.5f));

        // Logo fades out near end so app transition is seamless
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(logo, "alpha", 1f, 0f);
        fadeOut.setDuration(400);
        fadeOut.setStartDelay(900);

        AnimatorSet phase3 = new AnimatorSet();
        phase3.playTogether(zoomX, zoomY, fadeOut);
        phase3.setStartDelay(1100); // Hold after flip

        AnimatorSet full = new AnimatorSet();
        full.playSequentially(phase1, phase3);
        full.start();

        // Launch main app — seamlessly after zoom fills screen
        logo.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // No transition animation — red screen fills both, seamless
            overridePendingTransition(0, 0);
            finish();
        }, 2600);
    }
}
