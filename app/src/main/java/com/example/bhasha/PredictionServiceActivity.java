package com.example.bhasha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionServiceActivity extends AppCompatActivity {
    private final String AUDIO_PREFIX = "spoken";
    private final String RAW_FOLDER_PATH = "android.resource://com.example.bhasha/raw/";

    String audioFile1;
    String audioFile2;
    String audioFile3;
    String audioFile4;
    String audioFile5;
    String audioFile6;
    String audioFile7;
    String audioFile8;
    String audioFile9;
    String audioFile10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        String label = "000";

        if(extras != null) {
            label = intent.getExtras().getString("label").toString();
        }

        predict(label);
    }

    public void predict(String label) {
        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Bhasha/input.mp4";

        API api = Client.get().create(API.class);

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("video/mp4"));
        MultipartBody.Part video = MultipartBody.Part.createFormData("video", file.getName(), requestBody);

        RequestBody labelPart = RequestBody.create(MultipartBody.FORM, label);

        Call<Model> call = api.getPrediction(labelPart, video);

        Intent errorIntent = new Intent(PredictionServiceActivity.this, ErrorMessageActivity.class);
        Intent predictionIntent = new Intent(PredictionServiceActivity.this, PredictionActivity.class);

        call.enqueue(new Callback<Model>() {
            @Override
            public void onResponse(Call<Model> call, Response<Model> response) {

                if (response.isSuccessful()) {
                    String predictionText1 = response.body().getPredictionText1();
                    String predictionText2 = response.body().getPredictionText2();
                    String predictionText3 = response.body().getPredictionText3();
                    String predictionText4 = response.body().getPredictionText4();
                    String predictionText5 = response.body().getPredictionText5();
                    String predictionText6 = response.body().getPredictionText6();
                    String predictionText7 = response.body().getPredictionText7();
                    String predictionText8 = response.body().getPredictionText8();
                    String predictionText9 = response.body().getPredictionText9();
                    String predictionText10 = response.body().getPredictionText10();


                    audioFile1 = response.body().getAudioFile1();
                    audioFile2 = response.body().getAudioFile2();
                    audioFile3 = response.body().getAudioFile3();
                    audioFile4 = response.body().getAudioFile4();
                    audioFile5 = response.body().getAudioFile5();
                    audioFile6 = response.body().getAudioFile6();
                    audioFile7 = response.body().getAudioFile7();
                    audioFile8 = response.body().getAudioFile8();
                    audioFile9 = response.body().getAudioFile9();
                    audioFile10 = response.body().getAudioFile10();

                    predictionIntent.putExtra("predictionText1", predictionText1);
                    predictionIntent.putExtra("predictionText2", predictionText2);
                    predictionIntent.putExtra("predictionText3", predictionText3);
                    predictionIntent.putExtra("predictionText4", predictionText4);
                    predictionIntent.putExtra("predictionText5", predictionText5);
                    predictionIntent.putExtra("predictionText6", predictionText6);
                    predictionIntent.putExtra("predictionText7", predictionText7);
                    predictionIntent.putExtra("predictionText8", predictionText8);
                    predictionIntent.putExtra("predictionText9", predictionText9);
                    predictionIntent.putExtra("predictionText10", predictionText10);

                    predictionIntent.putExtra("audioFile1", audioFile1);
                    predictionIntent.putExtra("audioFile2", audioFile2);
                    predictionIntent.putExtra("audioFile3", audioFile3);
                    predictionIntent.putExtra("audioFile4", audioFile4);
                    predictionIntent.putExtra("audioFile5", audioFile5);
                    predictionIntent.putExtra("audioFile6", audioFile6);
                    predictionIntent.putExtra("audioFile7", audioFile7);
                    predictionIntent.putExtra("audioFile8", audioFile8);
                    predictionIntent.putExtra("audioFile9", audioFile9);
                    predictionIntent.putExtra("audioFile10", audioFile10);

                    startActivity(predictionIntent);


                } else {
                    if (response.code() == 500) {
                        errorIntent.putExtra("message", getString(R.string.FAIL_TO_DETECT_FACE_ERROR_MESSAGE));
                    } else {
                        errorIntent.putExtra("message", getString(R.string.FAIL_TO_RESPONSE_ERROR_MESSAGE));
                    }
                    startActivity(errorIntent);
                }
            }

            @Override
            public void onFailure(Call<Model> call, Throwable t) {
                errorIntent.putExtra("message", getString(R.string.FAIL_TO_CONNECT_SERVER_ERROR_MESSAGE));
                startActivity(errorIntent);
            }
        });
    }
}

