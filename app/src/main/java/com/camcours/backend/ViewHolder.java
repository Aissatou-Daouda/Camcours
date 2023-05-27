package com.camcours.backend;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

public abstract class ViewHolder extends RecyclerView.ViewHolder {
    private JSONObject jsonObject;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    void setJsonObject(JSONObject object) {
        jsonObject = object;
        display(object);
    }

    public abstract void display(JSONObject object);
}
