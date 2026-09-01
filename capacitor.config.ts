import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.thefocuslive.app',
  appName: 'The Focus Live',
  webDir: 'dist',
  server: {
    url: 'https://focus-site-maker.lovable.app',
    cleartext: false,
    androidScheme: 'https',
    allowNavigation: ['*.lovable.app', '*.thefocuslive.com'],
  },
  android: {
    allowMixedContent: false,
    backgroundColor: '#000000',
    captureInput: true,
    webContentsDebuggingEnabled: false,
    overrideUserAgent: 'The Focus Live App/1.0 Android',
    initialFocus: false,
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 0,
      launchAutoHide: true,
      backgroundColor: '#000000',
      showSpinner: false,
      splashFullScreen: true,
      splashImmersive: true,
    },
    StatusBar: {
      style: 'Dark',
      backgroundColor: '#000000',
      overlaysWebView: false,
    },
  },
};

export default config;
