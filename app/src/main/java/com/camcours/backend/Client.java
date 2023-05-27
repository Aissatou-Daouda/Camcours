package com.camcours.backend;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class Client {
    public interface OperationListener {
        void operationRunning(long processed, long total);
        void operationFinished(int httpStatusCode, JSONObject response);
        void operationFailed(Exception exception);
    }

    private String apiKey;

    private static final String baseUrl = "https://commander-systems.000webhostapp.com";
    //private static final String baseUrl = "http://192.168.8.100";
    public static final String apiBaseUrl = baseUrl + "/Camcours/api/v1";
    public static final String fileBaseUrl = baseUrl + "/Camcours/files";
    public static boolean debug = true;

    private static Client self;

    private Activity activity;
    private OkHttpClient client;
    private Cache cache;

    public Client(String apiKey, Activity activity, File cacheDir) {
        OkHttpClient.Builder b = new OkHttpClient.Builder();

        this.apiKey = apiKey;
        this.activity = activity;
        this.cache = new Cache(cacheDir, 1024 * 1024);
        this.client = b.callTimeout(5, TimeUnit.SECONDS).cache(cache).build();

        self = this;
    }

    public Activity getActivity() {
        return activity;
    }

    public void get(String endpoint, Map<String, Object> params, OperationListener listener) {
        process("GET", endpoint, params, null, listener);
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void post(String endpoint, Map<String, Object> params, JSONObject data, OperationListener listener) {
        process("POST", endpoint, params, data, listener);
    }

    public void put(String endpoint, Map<String, Object> params, JSONObject data, OperationListener listener) {
        process("PUT", endpoint, params, data, listener);
    }

    public void delete(String endpoint, Map<String, Object> params, OperationListener listener) {
        process("DELETE", endpoint, params, null, listener);
    }

    public void postFile(File file, OperationListener listener) {
        try {
            FileInputStream s = new FileInputStream(file);
            postFile(s, listener);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void postFile(InputStream stream, OperationListener listener) {
        if (debug) {
            System.out.println("HTTP POST " + fileBaseUrl + "/upload.php?api_key=" + apiKey);
            try {
                System.out.println("HTTP DATA " + stream.available());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long size = 0;
        try {
            size = stream.available();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long finalSize = size;
        RequestBody body = new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("application/octet-stream");
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source = null;
                try {
                    source = Okio.source(stream);
                    long total = 0;
                    long read;

                    while ((read = source.read(sink.buffer(), 2048)) != -1) {
                        total += read;
                        sink.flush();

                        final long finalTotal = total;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listener.operationRunning(finalTotal, finalSize);
                            }
                        });
                    }
                } finally {
                    Util.closeQuietly(source);
                }
            }
        };

        Request.Builder b = new Request.Builder();
        b.url(fileBaseUrl + "/upload.php?api_key=" + apiKey);
        Request req = b.post(body).build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.operationFailed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                System.out.println("Upload completed, response: " + response.toString());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int httpStatusCode = response.code();
                            JSONObject json = new JSONObject(response.body().string());
                            listener.operationFinished(httpStatusCode, json);
                        } catch (Exception e) {
                            listener.operationFailed(e);
                        }
                    }
                });
            }
        };

        Call c = client.newCall(req);
        c.timeout().timeout(3, TimeUnit.MINUTES);
        c.enqueue(callback);
    }

    public static Client getInstance() {
        return self;
    }

    private void process(@NonNull String method, @NonNull String endpoint, @Nullable Map<String, Object> params, JSONObject data, OperationListener listener) {
        listener.operationRunning(0, 1);

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.operationFailed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (debug)
                    System.out.println("HTTP " + response.code() + ": " + response.request().url().toString());

                try {
                    int code = response.code();
                    JSONObject data = new JSONObject(response.body().string());

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                listener.operationRunning(1, 1);
                                listener.operationFinished(code, data);
                            } catch (Exception e) {
                                listener.operationFailed(e);
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        };

        client.newCall(createRequest(method, endpoint, params, data)).enqueue(callback);
    }

    private Request createRequest(@NonNull String method, @NonNull String endpoint, Map<String, Object> params, JSONObject data) {
        if (debug)
            System.out.println("HTTP " + method + ' ' + createUrl(endpoint, params));

        Request.Builder b = new Request.Builder();
        b.url(createUrl(endpoint, params));

        if (data != null)
            b.method(method, RequestBody.create(MediaType.parse("application/json"), data.toString()));
        else
            b.method(method, null);

        return b.build();
    }

    private String createUrl(@NonNull String endpoint, @Nullable Map<String, Object> params) {
        String query = "api_key=" + apiKey;

        if (params != null) {
            for (String p : params.keySet())
                query += '&' + p + '=' + params.get(p).toString();
        }

        return apiBaseUrl + endpoint + '?' + query;
    }
}
