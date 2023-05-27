package com.camcours.frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.camcours.R;
import com.camcours.data.AgentManager;
import com.camcours.backend.Client;
import com.camcours.backend.ViewHolderAdapter;
import com.camcours.data.SchoolAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ViewHolderAdapter.HolderClickListener {
    SchoolAdapter adapter;
   private View add;
   private TextView search;
   private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Client.getInstance().setActivity(this);

        add=findViewById(R.id.ajout);

        RecyclerView view = findViewById(R.id.schoolRecyclerView);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter = new SchoolAdapter(this));

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.get(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog = new ProgressDialog(adapter, this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Downloading schools data");

        fetchData();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if (AgentManager.getInstance().getAgent() == null) {
                    i = new Intent(MainActivity.this, Iconnexion.class);
                } else {
                    i = new Intent(getApplicationContext(), AccountCt.class);
                }

                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    void fetchData() {
        dialog.show();

        String query = "";
        adapter.get(query);
    }

    @Override
    public void dataClicked(JSONObject data) {
        try {
            Intent i = new Intent(this, CompetitionListActivity.class);
            i.putExtra("schoolId", data.getInt("id"));
            i.putExtra("schoolName", data.getString("name"));
            startActivity(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}