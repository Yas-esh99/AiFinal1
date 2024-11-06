package com.example.aifinal1;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import java.util.Locale;

public class Tts implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;
    private boolean isInitialized = false;

    public Tts(Context context) {
        textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US); // Set your preferred language
            isInitialized = true;
            Log.d("Tts", "TextToSpeech initialized successfully");
        } else {
            Log.e("Tts", "Initialization failed");
        }
    }

    public void speak(String text) {
        if (isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            Log.e("Tts", "TextToSpeech not initialized");
        }
    }

    public void stop() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
    }
}