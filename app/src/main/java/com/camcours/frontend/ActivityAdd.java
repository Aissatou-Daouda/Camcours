package com.camcours.frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.camcours.R;
import com.camcours.data.AgentManager;
import com.camcours.backend.ViewHolderAdapter;
import com.camcours.data.CompetitionAdapter;
import com.camcours.data.CompetitionManager;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityAdd extends AppCompatActivity implements ViewHolderAdapter.HolderClickListener {
RecyclerView recyclerView;
TextView add;
CompetitionAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_add);

        add=findViewById(R.id.add);
        recyclerView=findViewById(R.id.doc_school);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter = new CompetitionAdapter(this));

        Intent ajoutpdf = new Intent(getApplicationContext(), AjoutResult.class);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ajoutpdf);
            }
        });

        fetchData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchData();
    }

    public void fetchData() {
        ProgressDialog d = new ProgressDialog(adapter, this);
        d.setTitle("Loading...");
        d.show();

        try {
            adapter.get(AgentManager.getInstance().getAgent().getJSONObject("school").getInt("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dataClicked(JSONObject data) {
        if (true) {
            CompetitionManager m = new CompetitionManager();
            m.removeCompetition(data, new CompetitionManager.RemoveListener() {
                @Override
                public void competitionRemoved(JSONObject compet) {
                    Toast.makeText(ActivityAdd.this, "Ok", Toast.LENGTH_SHORT).show();
                    ActivityAdd.this.fetchData();
                }

                @Override
                public void competitionRemoveFailed(JSONObject compet) {
                    Toast.makeText(ActivityAdd.this, "Echec !", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}