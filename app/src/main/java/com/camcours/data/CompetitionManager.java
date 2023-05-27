package com.camcours.data;

import android.os.ParcelFileDescriptor;

import com.camcours.backend.Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompetitionManager {
    public interface RegistrationListener {
        void competitionAdded(JSONObject compet);
        void competitionAddFailed(JSONObject compet);
        void fileUpload(long uploaded, long total);
    }

    public interface UpdateListener {
        void competitionUpdated(JSONObject compet);
        void competitionUpdateFailed(JSONObject compet);
    }

    public interface RemoveListener {
        void competitionRemoved(JSONObject compet);
        void competitionRemoveFailed(JSONObject compet);
    }

    public void registerCompetition(JSONObject compet, RegistrationListener listener) {
        try {
            InputStream file = (InputStream) compet.get("pdf");

            Client.OperationListener dataListener = new Client.OperationListener() {
                @Override
                public void operationRunning(long processed, long total) {
                    //
                }

                @Override
                public void operationFinished(int httpStatusCode, JSONObject response) {
                    listener.competitionAdded(response);
                }

                @Override
                public void operationFailed(Exception e) {
                    listener.competitionAddFailed(compet);
                }
            };

            Client.getInstance().postFile(file, new Client.OperationListener() {
                @Override
                public void operationRunning(long processed, long total) {
                    listener.fileUpload(processed, total);
                }

                @Override
                public void operationFinished(int httpStatusCode, JSONObject response) {
                    try {
                        compet.remove("pdf");
                        compet.put("pdfPath", response.getString("pdfPath"));
                        Client.getInstance().post("/competition", null, compet, dataListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void operationFailed(Exception e) {
                    listener.competitionAddFailed(compet);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateCompetition(JSONObject compet, UpdateListener listener) {
        int id = 0;
        try {
            id = compet.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Client.getInstance().put("/competition/" + id, null, compet, new Client.OperationListener() {
            @Override
            public void operationRunning(long processed, long total) {
                //
            }

            @Override
            public void operationFinished(int httpStatusCode, JSONObject response) {
                listener.competitionUpdated(response);
            }

            @Override
            public void operationFailed(Exception e) {
                listener.competitionUpdateFailed(compet);
                e.printStackTrace();
            }
        });
    }

    public void removeCompetition(JSONObject compet, RemoveListener listener) {
        int id = 0;
        try {
            id = compet.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Client.getInstance().delete("/competition/" + id, null, new Client.OperationListener() {
            @Override
            public void operationRunning(long processed, long total) {
                //
            }

            @Override
            public void operationFinished(int httpStatusCode, JSONObject response) {
                listener.competitionRemoved(compet);
            }

            @Override
            public void operationFailed(Exception e) {
                listener.competitionRemoveFailed(compet);
            }
        });
    }
}
