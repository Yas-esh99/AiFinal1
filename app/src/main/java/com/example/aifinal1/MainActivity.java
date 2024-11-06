package com.example.aifinal1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.aifinal1.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity implements PublicInterface {
    private ActivityMainBinding binding;

    private static final int PERMISSION_REQUEST_CODE = 1;
    private BluetoothService Bs;
    private Stt stt;
    private CallGemini callgemini;
    private Tts tts;
    private ExecutorService executorService;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // set content view to binding's root
        setContentView(binding.getRoot());
        executorService = Executors.newSingleThreadExecutor();

        checkPermission();
        
        stt = new Stt(MainActivity.this);
        stt.setListener(this);
        
        tts = new Tts(MainActivity.this);
        tts.onInit(1);
        binding.btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        // TODO: Implement this method
                        startSetup();
                    }
                });
    }
    
    
    private void startSetup()
{
    
        executorService.submit(() -> {
                    // Perform transcription
                    stt.mao("audio.wav");

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        Log.d("SpeechResult","hi");
                        //binding.textView.setText(result);
                    });
                });
}
    private void setupListener() {}

    private void checkPermission() {
        String[] permissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.INTERNET
        };

        List<String> permissionsToRequest =
                new ArrayList<>(); // Create a list to hold permissions that need to be requested

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(
                        permission); // Add to the list if permission is not granted
            }
        }

        // If there are permissions to request, do so
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, permissionsToRequest.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }
    
    private void status(String s){
        binding.textView.setText(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
        if (stt != null) {
            stt.cleanup();
        }
        this.binding = null;
    }

    @Override
    public void onReceiveFile() {}

    @Override
    public void onReceiveResponse(String s) {
        Log.d(TAG,s);
        tts.speak(s);
        //tts = new Tts(MainActivity.this,this);
    }

    @Override
    public void onReceiveText(String t) {
        Log.d(TAG,t);
        callgemini = new CallGemini("hi",MainActivity.this);
        callgemini.setListener(this);
        callgemini.sendRequest();
    }

    @Override
    public void updateStatus() {}
}
