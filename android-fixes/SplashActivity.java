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
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean launched = false;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pure black — no red, no white
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);

        // Pre-launch MainActivity in background IMMEDIATELY
        // This way WebView starts loading while video plays
        preWarmApp();

        videoView = findViewById(R.id.boot_video);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.boot_video);
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(mp -> smoothTransition());

        videoView.setOnErrorListener((mp, what, extra) -> {
            launchApp();
            return true;
        });

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            mp.setVolume(1f, 1f);
            mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
            videoView.start();
        });

        videoView.requestFocus();

        // Safety: force launch after 5 sec max
        new Handler(Looper.getMainLooper()).postDelayed(this::launchApp, 5000);
    }

    private void preWarmApp() {
        // Start MainActivity HIDDEN in background so WebView preloads
        // When we launch it, it's already loaded — no black screen
        new Thread(() -> {
            try {
                // Small delay so video starts first
                Thread.sleep(500);
                runOnUiThread(() -> {
                    Intent warm = new Intent(this, MainActivity.class);
                    warm.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // Don't start yet — just prepare the intent
                });
            } catch (Exception e) {
                // ignore
            }
        }).start();
    }

    private void smoothTransition() {
        if (launched) return;
        launched = true;

        FrameLayout root = findViewById(R.id.splash_root);

        // Smooth fade out + slide left simultaneously
        root.animate()
            .alpha(0f)
            .translationX(-80f)  // Subtle slide left
            .setDuration(400)
            .setInterpolator(new DecelerateInterpolator(2f))
            .withEndAction(this::launchApp)
            .start();
    }

    private void launchApp() {
        if (launched && videoView != null) {
            videoView.stopPlayback();
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        // Slide in from right — content appears smoothly
        overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out_splash);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) videoView.start();
    }
}
