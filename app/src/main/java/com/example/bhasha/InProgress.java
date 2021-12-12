package com.example.bhasha;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;

public class InProgress {
    public static final String TAG = "InProgress";
    Activity activity;
    AlertDialog dialog;

    public InProgress(Activity myActivity) {
        activity = myActivity;
    }

    public void on() {
        Log.e(TAG, "Inprogress on");
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_in_progress, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    void off() {
        dialog.dismiss();
    }
}