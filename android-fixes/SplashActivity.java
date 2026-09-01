package com.thefocuslive.app;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean launched = false;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // True fullscreen — no status bar, no nav bar
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        // Keep screen on during video
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_splash);

        videoView = findViewById(R.id.boot_video);

        // Load video from raw resources
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.boot_video);
        videoView.setVideoURI(videoUri);

        // When video completes — swipe left into app
        videoView.setOnCompletionListener(mp -> slideToApp());

        // If video fails to load — launch app immediately
        videoView.setOnErrorListener((mp, what, extra) -> {
            launchApp();
            return true;
        });

        // When ready — play immediately, no delay
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            mp.setVolume(1f, 1f);
            // Remove black bars — stretch to fill screen
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            videoView.start();
        });

        videoView.requestFocus();

        // Safety timeout — if video longer than 12s, force launch
        new Handler(Looper.getMainLooper()).postDelayed(this::launchApp, 12000);
    }

    private void slideToApp() {
        if (launched) return;
        launched = true;

        // Slide left animation — dashboard comes in from right
        videoView.animate()
            .translationX(-getResources().getDisplayMetrics().widthPixels)
            .setDuration(350)
            .setInterpolator(new AccelerateInterpolator(1.5f))
            .withEndAction(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
                finish();
            })
            .start();
    }

    private void launchApp() {
        if (launched) return;
        launched = true;
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }
}
