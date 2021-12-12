package com.example.bhasha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void loadApp(View view) {
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
    }

    public void loadDataApp(View view) {
        startActivity(new Intent(WelcomeActivity.this, DatasetActivity.class));
    }
}