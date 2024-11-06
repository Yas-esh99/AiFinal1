package com.example.aifinal1;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CallGemini {
    private String ques = "";
    private final String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyAIBDy9CSGuKiRzFmlgPp6rguhoCmBAAhQ"; // Replace with your actual API key
    private JSONObject jsonObj;
    public String ans = "";
    private final Context context;
    
    public PublicInterface pi;

    public CallGemini(String question, Context c) {
        this.ques = question;
        this.context = c;
        
    }

    public JSONObject createRequest() {
        jsonObj = new JSONObject();
        try {
            JSONArray contentsArray = new JSONArray();
            JSONObject partsObject = new JSONObject();
            partsObject.put("text", ques);
            JSONObject contentObject = new JSONObject();
            contentObject.put("parts", new JSONArray().put(partsObject));
            contentsArray.put(contentObject);
            jsonObj.put("contents", contentsArray);
        } catch (JSONException err) {
            throw new RuntimeException(err);
        }
        return jsonObj;
    }

    public void sendRequest() {
        JSONObject jsonObj = createRequest();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObj,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray candidateArray = response.getJSONArray("candidates");
                        JSONObject candidate = candidateArray.getJSONObject(0);
                        JSONObject content = candidate.getJSONObject("content");
                        JSONArray partArray = content.getJSONArray("parts");
                        JSONObject part = partArray.getJSONObject(0);
                        // Process response from Gemini API
                        ans = part.getString("text"); // Adjust as necessary to extract the desired info
                        pi.onReceiveResponse(ans);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errorMessage", error.toString());
                }
            }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json");
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
    public void setListener(PublicInterface listener) {
    this.pi = listener;
}
}