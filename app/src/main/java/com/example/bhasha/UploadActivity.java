package com.example.bhasha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        String label = "000";
        if(extras != null) {
            label = intent.getExtras().getString("label").toString();
        }
        upload(label);
    }

    public void upload(String label) {
        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Bhasha/input.mp4";

        API api = Client.get().create(API.class);

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("video/mp4"));
        MultipartBody.Part video = MultipartBody.Part.createFormData("video", file.getName(), requestBody);

        RequestBody labelPart = RequestBody.create(MultipartBody.FORM, label);
        Call<ResponseBody> call = api.upload(labelPart, video);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UploadActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(UploadActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}