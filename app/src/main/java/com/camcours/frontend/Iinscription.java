package com.camcours.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.camcours.R;
import com.camcours.data.AgentManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Iinscription extends AppCompatActivity {
    EditText name, school, login, pass0, pass1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iinscription);

        name = findViewById(R.id.nom);
        school = findViewById(R.id.ecole);
        login = findViewById(R.id.mail);
        pass0 = findViewById(R.id.password3);
        pass1 = findViewById(R.id.password4);

        TextView button = findViewById(R.id.textView7);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString();
                String l = login.getText().toString();
                String p0 = pass0.getText().toString();
                String p1 = pass1.getText().toString();
                String s = school.getText().toString();

                if (p0.compareTo(p1) == 0)
                    register(n, l, p0, s);
                else
                    Toast.makeText(Iinscription.this, "Passwords mismatch !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void register(String name, String login, String password, String school) {
        JSONObject agent = new JSONObject();
        JSONObject schoolO = new JSONObject();
        try {
            agent.put("name", name);
            agent.put("login", login);
            agent.put("password", login);
            schoolO.put("name", school);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AgentManager man = AgentManager.getInstance();
        man.registerAgent(agent, schoolO, new AgentManager.RegistrationListener() {
            @Override
            public void agentAdded(JSONObject agent) {
                Toast.makeText(Iinscription.this, "Bienvenue !", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void agentAddFailed(JSONObject error) {
                Toast.makeText(Iinscription.this, "Echec d'enregistrement", Toast.LENGTH_SHORT).show();
            }
        });
    }
}