package com.thefocuslive.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
        setContentView(R.layout.activity_splash);
        ImageView logo = findViewById(R.id.splash_logo);
        startAnimation(logo);
    }

    private void startAnimation(final ImageView logo) {

        // Phase 1: Flip in
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(logo, "alpha", 0f, 1f);
        fadeIn.setDuration(300);
        fadeIn.setStartDelay(200);

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(logo, "scaleX", 0f, 1f);
        flipIn.setDuration(550);
        flipIn.setStartDelay(200);
        flipIn.setInterpolator(new OvershootInterpolator(1.8f));

        ObjectAnimator scaleYIn = ObjectAnimator.ofFloat(logo, "scaleY", 0.85f, 1f);
        scaleYIn.setDuration(450);
        scaleYIn.setStartDelay(250);
        scaleYIn.setInterpolator(new OvershootInterpolator(1.3f));

        AnimatorSet phase1 = new AnimatorSet();
        phase1.playTogether(fadeIn, flipIn, scaleYIn);

        // Phase 2: Slow zoom fills screen — scale 30x covers full screen
        ObjectAnimator zoomX = ObjectAnimator.ofFloat(logo, "scaleX", 1f, 30f);
        zoomX.setDuration(900);
        zoomX.setInterpolator(new AccelerateInterpolator(1.8f));

        ObjectAnimator zoomY = ObjectAnimator.ofFloat(logo, "scaleY", 1f, 30f);
        zoomY.setDuration(900);
        zoomY.setInterpolator(new AccelerateInterpolator(1.8f));

        AnimatorSet phase2 = new AnimatorSet();
        phase2.playTogether(zoomX, zoomY);
        phase2.setStartDelay(900); // Hold 0.9s after flip

        AnimatorSet full = new AnimatorSet();
        full.playSequentially(phase1, phase2);
        full.start();

        // Launch app EXACTLY when zoom fills screen — no red screen wait
        logo.postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Zero transition — red fills both screens seamlessly
            overridePendingTransition(0, 0);
            finish();
        }, 1800); // flip(750) + hold(900) + zoom starts = seamless
    }
}
