package com.camcours.frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import  com.camcours.R;
import com.camcours.data.AgentManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Iconnexion extends AppCompatActivity {
    TextView inscrit, con;
    EditText mail, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iconnexion);
        inscrit=findViewById(R.id.inscription);
        mail=findViewById(R.id.mail);
        password=findViewById(R.id.password);
        //bouton pour verification de donnee et acces a la page d'acceuil
        con=findViewById(R.id.con);
        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AgentManager man = AgentManager.getInstance();
                man.authenticateAgent(mail.getText().toString(), password.getText().toString(), new AgentManager.AuthenticationListener() {
                    @Override
                    public void agentAuthenticated(JSONObject agent) {
                        try {
                            finish();
                            Toast.makeText(Iconnexion.this, "Bienvenue " + agent.getString("name"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void agentAuthenticationFailed(JSONObject agent) {
                        Toast.makeText(Iconnexion.this, "Login/mot depasse incorrect", Toast.LENGTH_SHORT).show();
                        // Affiche message erreur connexion
                    }
                });
            }
        });

        inscrit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Iconnexion.this, Iinscription.class));
            }
        });
    }
}