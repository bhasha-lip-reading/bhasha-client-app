package com.example.bhasha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ErrorMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_message);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String errorMessage = intent.getExtras().getString("message").toString();
            TextView textView = findViewById(R.id.error_message);
            textView.setText(errorMessage);
        }
    }
}