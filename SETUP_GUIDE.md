# The Focus Live — Android APK Setup Guide
## Live App: Auto-updates with WordPress posts + YouTube videos

---

## How This Works

```
App opens → Shows live focus-site-maker.lovable.app
New post on WordPress → Automatically appears in app
New YouTube video → Automatically appears in app
No app update needed — ever.
```

---

## Prerequisites (Install These First)

1. **Node.js** — nodejs.org (LTS version)
2. **Android Studio** — developer.android.com/studio
3. **JDK 17** — comes with Android Studio

---

## Setup Steps

### Step 1 — Open Terminal in this folder

Windows: Right-click folder → "Open in Terminal"
Mac: Terminal → drag folder

### Step 2 — Install dependencies

```bash
npm install
```

### Step 3 — Add Android platform

```bash
npx cap add android
```

### Step 4 — Sync

```bash
npx cap sync android
```

### Step 5 — Open in Android Studio

```bash
npx cap open android
```

Android Studio will open automatically.

---

## In Android Studio

### Add App Icon
1. Right-click `app/src/main/res` → New → Image Asset
2. Icon Type: Launcher Icons
3. Upload your logo (use The Focus red logo)
4. Click Next → Finish

### Build APK
```
Build → Build Bundle(s)/APK(s) → Build APK(s)
```

APK location:
```
android/app/build/outputs/apk/debug/app-debug.apk
```

### Build Release APK (for Play Store)
```
Build → Generate Signed Bundle/APK
→ APK → Next
→ Create new keystore (save the password!)
→ Build
```

---

## Update capacitor.config.ts

The live URL is already set:
```typescript
server: {
  url: 'https://focus-site-maker.lovable.app',
}
```

If you deploy to a custom domain later, change this URL — rebuild APK once.

---

## Play Store Checklist

- [ ] App icon (512x512 PNG)
- [ ] Feature graphic (1024x500 PNG)
- [ ] Screenshots (min 2, phone size)
- [ ] App description
- [ ] Privacy policy URL
- [ ] Signed APK or AAB

---

## Faster Option — No Coding (5 minutes)

Go to **webintoapp.com**:
1. URL: `https://focus-site-maker.lovable.app`
2. App name: The Focus Live
3. Package: com.thefocuslive.app
4. Upload icon
5. Download APK

This creates the same WebView app instantly, no terminal needed.

---

## What Auto-Updates Without Rebuilding APK

✅ New WordPress posts
✅ Breaking news
✅ YouTube videos (long-form)
✅ YouTube Shorts
✅ Category pages
✅ Author pages
✅ Search results
✅ App UI/design changes on Lovable

❌ Does NOT auto-update:
- App icon
- App name
- Android permissions
- Splash screen
(These need a new APK)
