package com.huynhtrongman.fcm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText edtTitle;
    EditText edtMessage;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA74oRyoM:APA91bEJefDGqb9Ye-gt1S7JKkIXoR_WxZCVdpqHLONtjtWc-eMRiVqGTqi1zNxub0xKB6G1E6ksx-mz_pbFG89WBT5lwG_YCGwo7lM8SWN2gqH22VS0Ep-nnvJ4X46HAsLEaWfH1tzV";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;
    ProgressBar progressBar;
    private FirebaseAnalytics mFirebaseAnalytics;
    RelativeLayout relative_bg;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init
        edtTitle = findViewById(R.id.edt_title);
        edtMessage = findViewById(R.id.edt_body);
        Button btnSend = findViewById(R.id.but_send);
        progressBar = findViewById(R.id.loading_bar);
        progressBar.setVisibility(View.INVISIBLE);
        relative_bg = findViewById(R.id.relative_bg);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        // Đăng Ký Topics
        FirebaseMessaging.getInstance().subscribeToTopic("TEST")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            // nothing
                    }
                });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Loading , Làm Mờ Màn Hình
                // Chạy Luồng Chính để Update giao diện
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                        relative_bg.setBackgroundColor(Color.parseColor("#AAAAAA"));
                        Toast.makeText(getApplicationContext(),"Gửi Thành Công",Toast.LENGTH_LONG).show();
                    }
                });

                TOPIC = "/topics/TEST"; //Topics phải trùng với Topic đăng ký.
                NOTIFICATION_TITLE = edtTitle.getText().toString();
                NOTIFICATION_MESSAGE = edtMessage.getText().toString();
                // Thư viện Gson của của Google.
                JSONObject notification = new JSONObject();
                JSONObject notifcationBody = new JSONObject();
                try {
                    notifcationBody.put("title", NOTIFICATION_TITLE);
                    notifcationBody.put("message", NOTIFICATION_MESSAGE);

                    notification.put("to", TOPIC);
                    notification.put("data", notifcationBody);
                } catch (JSONException e) {
                    Log.e(TAG, "onCreate: " + e.getMessage() );
                }
                sendNotification(notification);
            }
        });

    }
    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        edtTitle.setText("");
                        edtMessage.setText("");
                        // Tắt Loading , Làm Sáng Màn Hình
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setVisibility(View.INVISIBLE);
                                relative_bg.setBackgroundColor(Color.parseColor("#ffffff"));
                            }
                        });

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Request error", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        // Class thực hiện API ( thư viện Volley của Google )
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
