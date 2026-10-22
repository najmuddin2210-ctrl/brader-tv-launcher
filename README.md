# Brader TV Launcher

A lightweight starter launcher for Android TV / Google TV, designed for remote-control navigation. It shows installed Leanback apps in a simple purple/dark interface, opens apps with OK, and offers app info on long-press.

## Build APK using GitHub Actions (recommended)

This repository includes `.github/workflows/build.yml`. Upload the files to a GitHub repository, then:

1. Open the repository's **Actions** tab.
2. Select **Build Brader TV Launcher**.
3. Press **Run workflow**.
4. When complete, open the run and download the `brader-tv-launcher-apk` artifact.
5. Extract the ZIP to get the APK.

GitHub's Ubuntu x86-64 build runner avoids the ARM64 AAPT2 issue encountered in Android/Termux.

## Install on ONN 4K over ADB

First install and test the APK without disabling the stock launcher:

```sh
adb connect TV_IP:5555
adb install -r app-release.apk
```

Launch **Brader TV** from the TV's app list and test the remote. Only after it works should you select it as the Home app in Android settings. Do not disable or uninstall the original launcher until the new one has been tested and a rollback path is confirmed.

## Build locally

Requires JDK 17, Android SDK Platform 34 and Build Tools 34.0.0. Use Gradle 8.7 or compatible 8.x:

```sh
gradle assembleRelease
```

APK output: `app/build/outputs/apk/release/app-release.apk`.

## Scope / limitations

This is a simple starter launcher, not a full clone of Glass TV. It currently provides app tiles and app-info access. It does not modify system apps, debloat the device, set itself as default Home automatically, or change any TV settings.
