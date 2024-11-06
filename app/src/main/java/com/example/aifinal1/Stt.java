package com.example.aifinal1;

import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.StorageService;

public class Stt {
    private Model model;
    private Recognizer recognizer;
    private Context context;
    private StringBuilder resultText;
    private static final String TAG = "Stt";
    public JSONObject re;
    
    public PublicInterface listener;

    public Stt(Context c) {
        this.context = c;
        
        initModel();
          // Initialize model when creating the instance
    }

    private void initModel() {
        StorageService.unpack(context, "vosk-model-small-en-in-0.4", "model",
            modelFromCallback -> {
                model = modelFromCallback;  // Initialize model from unpacked path
                Log.d(TAG, "Model unpacked successfully: " + model);
                try {
                    recognizer = new Recognizer(model, 16000.0f);  // Initialize recognizer
                    Log.d(TAG, "Recognizer initialized successfully");
                    
                } catch (IOException e) {
                    Log.e(TAG, "Recognizer initialization failed", e);
                    e.printStackTrace();
                }
            },
            exception -> { 
                Log.e(TAG, "Model unpacking failed", exception);
                exception.printStackTrace();
            }
        );
    }

    public void mai(InputStream i) {
        Log.d(TAG,"mai");
        
        if (recognizer == null) {
            Log.e(TAG, "Recognizer not initialized");
            
        }

        resultText = new StringBuilder();
        try {
            i.skip(44);  // Skip the WAV header
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = i.read(buffer)) != -1) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                 //   resultText.append(recognizer.getResult()).append("\n");
                }
            }
            try {
                
            	re = new JSONObject(recognizer.getFinalResult());
                resultText.append(re.getString("text"));
                
            } catch(JSONException err) {
            	Log.e(TAG, "Error during coversion of json object", err);
            }
          //  resultText.append();

        } catch (IOException e) {
            Log.e(TAG, "Error during transcription", e);
        } finally {
            try {
                i.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing input stream", e);
            }
        }
        if (listener != null) {
            Log.d(TAG,"re");
            listener.onReceiveText(resultText.toString());
        }
        
    }
    
   public void setListener(PublicInterface listener) {
    this.listener = listener;
}

    public void mao(String fileName) {
        try {
            InputStream wavInputStream = context.getAssets().open(fileName);
            
            mai(wavInputStream);
        } catch (IOException e) {
            Log.e(TAG, "Error opening .wav file from assets", e);
            
        }
    }

    public void cleanup() {
        if (recognizer != null) {
            recognizer.close();
            recognizer = null;
        }
        if (model != null) {
            model.close();
            model = null;
        }
    }
}