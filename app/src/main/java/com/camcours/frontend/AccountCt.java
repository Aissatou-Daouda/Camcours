package com.camcours.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.camcours.R;
import com.camcours.data.AgentManager;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountCt extends AppCompatActivity {
    EditText name, pass0, pass1;
    TextView school, login, mbuttonConnexion,deconnexion, results;

    JSONObject agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_ct);

        name = findViewById(R.id.nom);
        school = findViewById(R.id.ecole);
        login = findViewById(R.id.mail);
        pass0 = findViewById(R.id.password3);
        pass1 = findViewById(R.id.password4);
        deconnexion=findViewById(R.id.deconnecion);
        results = findViewById(R.id.results);

        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgentManager.getInstance().forgetAgent();
                finish();
            }
        });

        results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AccountCt.this, ActivityAdd.class);
                startActivity(i);
            }
        });

        mbuttonConnexion=findViewById(R.id.textView7);
        mbuttonConnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agent = AgentManager.getInstance().getAgent();

                String p0 = pass0.getText().toString();
                try {
                    if (p0.compareTo(agent.getString("password")) == 0)
                        update();
                    else
                        Toast.makeText(AccountCt.this, "Bad password !", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        agent = AgentManager.getInstance().getAgent();
        try {
            name.setText(agent.getString("name"));
            school.setText(agent.getJSONObject("school").getString("name"));
            login.setText(agent.getString("login"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        mbuttonConnexion.setEnabled(false);

        try {
            agent.put("name", name.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AgentManager.getInstance().updateAgent(agent, new AgentManager.UpdateListener() {
            @Override
            public void agentUpdated(JSONObject agent) {
                Toast.makeText(AccountCt.this, "Success !", Toast.LENGTH_SHORT).show();
                mbuttonConnexion.setEnabled(true);
            }

            @Override
            public void agentUpdateFailed(JSONObject error) {
                mbuttonConnexion.setEnabled(true);
                Toast.makeText(AccountCt.this, "Error !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}