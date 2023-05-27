package com.camcours.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.camcours.R;
import com.camcours.data.AgentManager;
import com.camcours.backend.Client;

import org.json.JSONException;
import org.json.JSONObject;

public class IntroActivity extends AppCompatActivity {
    ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("camcours",0);

        // initializing REST client
        Client c = new Client("f9575f33-f3f0-11ed-bba4-028037ec0200", this, getCacheDir());

        // Log in agent
        AgentManager man = new AgentManager(prefs);
        man.loadData(new AgentManager.AuthenticationListener() {
            @Override
            public void agentAuthenticated(JSONObject agent) {
                try {
                    Toast.makeText(IntroActivity.this, "Welcome back " + agent.getString("name"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void agentAuthenticationFailed(JSONObject agent) {
                Toast.makeText(IntroActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        Intent mapage = new Intent(this, MainActivity.class);
        progress= findViewById(R.id.progress);
        progress.setProgress(0);
        CountDownTimer count = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                IntroActivity.this.finish();
                startActivity(mapage);

            }
        };
        count.start();
    }
}