# Motion Thrive — Android Studio project

**Package:** `com.motionthrive.app`  
**UI:** premium dark, Apple-inspired interface  
**Build type:** Android WebView wrapper around a local HTML/CSS/JavaScript renderer

## Included
- Android Studio / Gradle project configured for SDK 35 and Java 17
- Two clearly labelled generation modes: Manim-style scene-text subset and Canvas-style scene text
- 9:16 canvas preview and WebM export in compatible WebView/browser implementations
- Local project history
- Android Hindi speech-recognition intent bridge for live dictation
- GitHub Actions workflow to compile and upload a debug APK

## Build a debug APK
1. Install Android Studio and Android SDK Platform 35 / Build Tools 35, with JDK 17.
2. Open the `motion_thrive_android` folder in Android Studio and let Gradle sync.
3. Use **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. APK output after a successful build: `app/build/outputs/apk/debug/app-debug.apk`.

Alternative: push this project to a GitHub repository, then run **Actions > Build Motion Thrive APK** and download the `motion-thrive-debug-apk` artifact.

## Important feature status — please read
This is a working project scaffold, **not a compiled APK binary**.

- **Manim-style mode:** currently extracts scene text from common `Text(...)` calls and renders those as animated text scenes. It does **not** execute Python or the full Manim engine.
- **Canvas-style mode:** renders text scenes using the built-in canvas renderer. It does **not** execute arbitrary JavaScript supplied by the user.
- **Export:** WebM is the current output format. MP4 export requires adding an FFmpeg-based converter or a server-side render service.
- **History:** saved locally in the app WebView's browser storage.
- **Hinglish transcription:** live Hindi speech recognition is bridged to Android's installed speech service. Results may be Devanagari depending on the provider.
- **Transcribe an uploaded video:** the UI can select a media file, but automatic audio extraction and speech-to-text are **not connected yet**. This needs an on-device speech model or a configured transcription backend/API.
- **Build environment:** this workspace has Java but no Android SDK/Gradle installation, so an APK could not be compiled and verified here. Do not treat this ZIP as an APK.

## Suggested next implementation stage
To achieve full functionality, connect:
1. A Python/Manim rendering backend for real Manim code.
2. A Remotion/Canvas rendering pipeline for richer code-based scenes.
3. FFmpeg for MP4 encoding.
4. A Whisper-compatible transcription service or bundled on-device speech model for uploaded video transcription.
