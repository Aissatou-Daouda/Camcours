package com.camcours.frontend;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.camcours.R;
import com.camcours.data.AgentManager;
import com.camcours.data.CompetitionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AjoutResult extends AppCompatActivity {
    TextView ajoutpdf, pdfName;
    EditText competName;
    CalendarView calendar;
    InputStream pdfFile;
    ProgressDialog d;

    private Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_result);

        int year = new Date().getYear();

        calendar = findViewById(R.id.calendar);
        calendar.setMinDate(new Date(year, 0, 1).getTime());
        calendar.setMaxDate(new Date( year + 2, 11, 31).getTime());
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date.setDate(dayOfMonth);
                date.setMonth(month);
                date.setYear(year - 1900);
            }
        });

        d = new ProgressDialog(this);
        d.setTitle("Uploading...");
        d.setMessage("Uploading compet data");

        ajoutpdf=findViewById(R.id.textView3);
        ajoutpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();;
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf");
                i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(Intent.createChooser(i, "Selectionner le pdf"), 1);
            }
        });

        pdfName = findViewById(R.id.pdfOutput);

        competName = findViewById(R.id.competName);
        try {
            competName.setText("Concours " + AgentManager.getInstance().getAgent().getJSONObject("school").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView v = findViewById(R.id.addButton);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);

        if (reqCode == 1 && resCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                pdfFile = getContentResolver().openInputStream(data.getData());
                List<String> paths = data.getData().getPathSegments();
                pdfName.setText(paths.get(paths.size() - 2));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    protected void upload() {
        if (pdfFile != null) {
            String name = competName.getText().toString();
            uploadResult(name, date, pdfFile);
        }
    }

    private void uploadResult(String name, Date date, InputStream pdf) {
        JSONObject compet = new JSONObject();
        try {
            compet.put("name", name);
            compet.put("date", (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate());
            compet.put("pdf", pdfFile);
            compet.put("school", AgentManager.getInstance().getAgent().getJSONObject("school").getInt("id"));
            compet.put("agent", AgentManager.getInstance().getAgent().getInt("id"));
            System.out.println(compet.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        d.show();

        CompetitionManager man = new CompetitionManager();
        man.registerCompetition(compet, new CompetitionManager.RegistrationListener() {
            @Override
            public void competitionAdded(JSONObject compet) {
                d.dismiss();
                finish();
                // en cas de réussite
            }

            @Override
            public void competitionAddFailed(JSONObject compet) {
                d.dismiss();
                // En cas d'echec
            }

            @Override
            public void fileUpload(long uploaded, long total) {
                d.setMax((int) total);
                d.setProgress((int) uploaded);
            }
        });
    }
}