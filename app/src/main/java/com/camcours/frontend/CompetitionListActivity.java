package com.camcours.frontend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.Toast;

import com.camcours.R;
import com.camcours.backend.Client;
import com.camcours.backend.ViewHolderAdapter;
import com.camcours.data.CompetitionAdapter;
import com.camcours.frontend.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class CompetitionListActivity extends AppCompatActivity implements ViewHolderAdapter.HolderClickListener {
    private TextView schoolName;
    private CompetitionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_list);

        schoolName = findViewById(R.id.nomSchool);
        schoolName.setText(getIntent().getStringExtra("schoolName"));

        RecyclerView view = findViewById(R.id.competitionRecyclerView);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setAdapter(adapter = new CompetitionAdapter(this));

        fetchData();
    }

    protected void fetchData() {
        ProgressDialog dialog = new ProgressDialog(adapter, this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Downloading competitions data");
        dialog.show();

        adapter.get(getIntent().getIntExtra("schoolId", 0));
    }

    @SuppressLint("NewApi")
    @Override
    public void dataClicked(JSONObject data) {
        if (true) {
            String perms[] = {
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            };
            requestPermissions(perms, 1);
        }

        try {
            String fileName = data.getInt("id") + ('-' + data.getString("name") + ".pdf");
            String dirType = Environment.DIRECTORY_DOCUMENTS;
            String subPath = "Camcours/" + fileName;
            String pdf = Environment.getExternalStoragePublicDirectory(dirType).getPath() + '/' + subPath;

            File file = new File(pdf);
            if (!file.exists()) {
                Uri uri = Uri.parse(Client.fileBaseUrl + data.getString("pdfPath"));
                System.out.println("GET " + uri.toString());
                DownloadManager.Request req = new DownloadManager.Request(uri);
                req.setTitle(fileName);
                req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                req.setDestinationInExternalPublicDir(dirType, subPath);

                DownloadManager man = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                long id = man.enqueue(req);

                Toast.makeText(this, "Download in progress...", Toast.LENGTH_SHORT).show();

                BroadcastReceiver r = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Toast.makeText(context, "Location: Documents/" + subPath, Toast.LENGTH_LONG).show();
                        openPdf(pdf);
                    }
                };

                IntentFilter f = new IntentFilter();
                f.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(r, f);
            } else
                openPdf(pdf);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openPdf(String file) {
        File f = new File(file);
        Uri uri = FileProvider.getUriForFile(this, "com.camcours.PdfContentProvider", f);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(uri, "application/pdf");
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(i);
    }
}