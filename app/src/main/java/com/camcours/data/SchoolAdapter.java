package com.camcours.data;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.camcours.R;
import com.camcours.backend.ViewHolder;
import com.camcours.backend.ViewHolderAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SchoolAdapter extends ViewHolderAdapter {
    public static class SchoolViewHolder extends ViewHolder {
        private TextView name;

        public SchoolViewHolder(@NonNull View view) {
            super(view);
            name = view.findViewById(R.id.schoolName);
        }

        @Override
        public void display(JSONObject object) {
            try {
                name.setText(object.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public SchoolAdapter(HolderClickListener listener) {
        super(listener);
    }

    public void get(@Nullable String query) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (query != null && query.length() > 0)
            params.put("query", query);

        super.get("/schools", params);
    }

    @Override
    protected int layoutItem() {
        return R.layout.item_school;
    }

    @Override
    protected ViewHolder newViewHolder(View parent) {
        return new SchoolViewHolder(parent);
    }
}
