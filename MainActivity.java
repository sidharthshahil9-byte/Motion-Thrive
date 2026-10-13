package com.motionthrive.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int REQ_MIC = 101;
    private static final int REQ_SPEECH = 102;
    private WebView webView;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(android.graphics.Color.rgb(8,9,13));
        getWindow().setNavigationBarColor(android.graphics.Color.rgb(9,10,16));
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        webView.addJavascriptInterface(new NativeBridge(), "MotionNative");
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) request.grant(request.getResources());
                    else request.deny();
                });
            }
        });
        webView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            } catch (Exception e) { Toast.makeText(this, "Download link open nahi ho paya", Toast.LENGTH_LONG).show(); }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class NativeBridge {
        @JavascriptInterface public void startHindiSpeech() {
            runOnUiThread(() -> {
                if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_MIC);
                    return;
                }
                startSpeechIntent();
            });
        }
    }

    private void startSpeechIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hindi / Hinglish mein boliye");
        try { startActivityForResult(intent, REQ_SPEECH); }
        catch (Exception e) { Toast.makeText(this, "Speech recognition service available nahi hai", Toast.LENGTH_LONG).show(); }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_MIC && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) startSpeechIntent();
        else if (requestCode == REQ_MIC) Toast.makeText(this, "Microphone permission zaroori hai", Toast.LENGTH_LONG).show();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spoken = results.get(0).replace("\\", "\\\\").replace("'", "\\'").replace("\n", " ");
                webView.evaluateJavascript("window.motionNativeTranscript && window.motionNativeTranscript('" + spoken + "')", null);
            }
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }
}
