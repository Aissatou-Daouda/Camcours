package com.camcours.data;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.camcours.R;
import com.camcours.backend.ViewHolder;
import com.camcours.backend.ViewHolderAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompetitionAdapter extends ViewHolderAdapter {
    public class CompetitionViewHolder extends ViewHolder {

        public CompetitionViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void display(JSONObject compet) {
            TextView name = itemView.findViewById(R.id.competitionName);
            TextView date = itemView.findViewById(R.id.competitionDate);
            try {
                String d = compet.getString("date");
                name.setText(compet.getString("name"));
                date.setText(d.substring(5, 7) + '/' + d.substring(0, 4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public CompetitionAdapter(ViewHolderAdapter.HolderClickListener listener) {
        super(listener);
    }

    public void get(int schoolId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("school", schoolId);
        super.get("/competitions", params);
    }

    @Override
    protected int layoutItem() {
        return R.layout.item_competition;
    }

    @Override
    protected ViewHolder newViewHolder(View parent) {
        return new CompetitionViewHolder(parent);
    }
}
