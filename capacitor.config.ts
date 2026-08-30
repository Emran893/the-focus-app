import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.thefocuslive.app',
  appName: 'The Focus Live',
  webDir: 'dist',
  server: {
    url: 'https://focus-site-maker.lovable.app',
    cleartext: false,
    androidScheme: 'https',
    // Allow navigation within the app
    allowNavigation: ['*.lovable.app', '*.thefocuslive.com'],
  },
  android: {
    allowMixedContent: false,
    backgroundColor: '#1a0000',
    captureInput: true,
    webContentsDebuggingEnabled: false,
    overrideUserAgent: 'The Focus Live App/1.0 Android',
    // Prevent white flash on load
    initialFocus: false,
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 0,
      launchAutoHide: true,
      backgroundColor: '#1a0000',
      androidSplashResourceName: 'splash',
      showSpinner: false,
      splashFullScreen: true,
      splashImmersive: true,
    },
    StatusBar: {
      style: 'Dark',
      backgroundColor: '#1a0000',
      overlaysWebView: false,
    },
  },
};

export default config;
