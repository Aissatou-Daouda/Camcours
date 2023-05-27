package com.camcours.backend;

import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public abstract class ViewHolderAdapter extends RecyclerView.Adapter<com.camcours.backend.ViewHolder> implements Client.OperationListener {
    public interface HolderClickListener {
        void dataClicked(JSONObject data);
    }

    private JSONArray results;
    private HolderClickListener clickListener;
    private Vector<Client.OperationListener> operationListeners;

    public ViewHolderAdapter(HolderClickListener clickListener) {
        this.clickListener = clickListener;
        this.operationListeners = new Vector<Client.OperationListener>();
    }

    public void attachOperationListener(Client.OperationListener listener) {
        operationListeners.add(listener);
    }

    public void detachOperationListener(Client.OperationListener listener) {
        operationListeners.remove(listener);
    }

    protected abstract int layoutItem();
    protected abstract com.camcours.backend.ViewHolder newViewHolder(View parent);

    public void get(String endpoint, Map<String, Object> params) {
        Client.getInstance().get(endpoint, params, this);
    }

    @NonNull
    @Override
    public com.camcours.backend.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutItem(), parent, false);

        ViewHolder h = newViewHolder(view);
        if (clickListener != null) {
            h.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.dataClicked(h.getJsonObject());
                }
            });
        }
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull com.camcours.backend.ViewHolder holder, int position) {
        try {
            JSONObject o = results.getJSONObject(position);
            holder.setJsonObject(o);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (results != null ? results.length() : 0);
    }

    @Override
    public void operationRunning(long processed, long total) {
        for (int i = 0; i < operationListeners.size(); ++i)
            operationListeners.get(i).operationRunning(processed, total);
    }

    @Override
    public void operationFinished(int httpStatusCode, JSONObject response) {
        try {
            results = response.getJSONArray("results");
            notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < operationListeners.size(); ++i)
            operationListeners.get(i).operationFinished(httpStatusCode, response);
    }

    @Override
    public void operationFailed(Exception exception) {
        exception.printStackTrace();

        for (int i = 0; i < operationListeners.size(); ++i)
            operationListeners.get(i).operationFailed(exception);
    }
}
