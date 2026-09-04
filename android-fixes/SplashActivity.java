package com.thefocuslive.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean launched = false;
    private VideoView videoView;
    private WebView hiddenWebView; // Preloads app in background

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Pure black everywhere
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

        // ── Start preloading dashboard IMMEDIATELY in hidden WebView ──
        preloadDashboard();

        // ── Play boot video ──
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

        // Safety timeout
        new Handler(Looper.getMainLooper()).postDelayed(this::launchApp, 8000);
    }

    private void preloadDashboard() {
        // Create hidden WebView and load the URL in background
        // When video ends, app is already loaded — zero black screen
        hiddenWebView = new WebView(this);
        hiddenWebView.setVisibility(View.INVISIBLE);
        hiddenWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        WebSettings settings = hiddenWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        hiddenWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Page loaded — ready for instant show
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });

        // Add to layout but invisible
        FrameLayout root = findViewById(R.id.splash_root);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        );
        root.addView(hiddenWebView, params);

        // Load URL — starts in background during video playback
        hiddenWebView.loadUrl("https://focus-site-maker.lovable.app");
    }

    private void smoothTransition() {
        if (launched) return;
        launched = true;

        FrameLayout root = findViewById(R.id.splash_root);

        // Make hidden WebView visible with fade — it's already loaded
        if (hiddenWebView != null) {
            hiddenWebView.setVisibility(View.VISIBLE);
            hiddenWebView.setAlpha(0f);
            hiddenWebView.animate()
                .alpha(1f)
                .setDuration(350)
                .setInterpolator(new DecelerateInterpolator(2f))
                .withEndAction(() -> {
                    // Now launch real MainActivity
                    // User sees loaded page, not black screen
                    hiddenWebView.destroy();
                    launchApp();
                })
                .start();

            // Hide video
            videoView.animate().alpha(0f).setDuration(300).start();
        } else {
            launchApp();
        }
    }

    private void launchApp() {
        if (videoView != null) videoView.stopPlayback();
        if (hiddenWebView != null) {
            hiddenWebView.stopLoading();
            hiddenWebView.destroy();
            hiddenWebView = null;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
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
        if (videoView != null && !videoView.isPlaying() && !launched) videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hiddenWebView != null) {
            hiddenWebView.destroy();
            hiddenWebView = null;
        }
    }
}
