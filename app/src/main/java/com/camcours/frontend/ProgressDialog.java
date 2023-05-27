package com.camcours.frontend;

import android.content.Context;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.camcours.backend.Client;
import com.camcours.backend.ViewHolderAdapter;

import org.json.JSONObject;

public class ProgressDialog extends android.app.ProgressDialog implements Client.OperationListener {
    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(ViewHolderAdapter adapter, Context context) {
        super(context);
        setCancelable(false);

        adapter.attachOperationListener(this);
    }

    @Override
    public void operationRunning(long processed, long total) {
        setMax((int) total);
        setProgress((int) processed);
    }

    @Override
    public void operationFinished(int httpStatusCode, JSONObject response) {
        hide();
    }

    @Override
    public void operationFailed(Exception exception) {
        hide();

        Toast.makeText(getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }
}
