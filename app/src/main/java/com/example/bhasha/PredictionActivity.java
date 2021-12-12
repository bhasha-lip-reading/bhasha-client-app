package com.example.bhasha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionActivity extends AppCompatActivity {
    private static final String TAG = "PredictionActivity";
    private final String AUDIO_PREFIX = "spoken";
    private final String RAW_FOLDER_PATH = "android.resource://com.example.bhasha/raw/";

    FloatingActionButton goToMainActivityButton;
    Button predictionButton1;
    Button predictionButton2;
    Button predictionButton3;
    Button predictionButton4;
    Button predictionButton5;
    Button predictionButton6;
    Button predictionButton7;
    Button predictionButton8;
    Button predictionButton9;
    Button predictionButton10;

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
        setContentView(R.layout.activity_prediction);

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        Log.e(TAG, "PredictionActivity intent called");



        predictionButton1 = findViewById(R.id.predictionButton1);
        predictionButton2 = findViewById(R.id.predictionButton2);
        predictionButton3 = findViewById(R.id.predictionButton3);
        predictionButton4 = findViewById(R.id.predictionButton4);
        predictionButton5 = findViewById(R.id.predictionButton5);
        predictionButton6 = findViewById(R.id.predictionButton6);
        predictionButton7 = findViewById(R.id.predictionButton7);
        predictionButton8 = findViewById(R.id.predictionButton8);
        predictionButton9 = findViewById(R.id.predictionButton9);
        predictionButton10 = findViewById(R.id.predictionButton10);

        if (extras != null) {
            String predictionText1 = intent.getExtras().getString("predictionText1").toString();
            String predictionText2 = intent.getExtras().getString("predictionText2").toString();
            String predictionText3 = intent.getExtras().getString("predictionText3").toString();
            String predictionText4 = intent.getExtras().getString("predictionText4").toString();
            String predictionText5 = intent.getExtras().getString("predictionText5").toString();
            String predictionText6 = intent.getExtras().getString("predictionText6").toString();
            String predictionText7 = intent.getExtras().getString("predictionText7").toString();
            String predictionText8 = intent.getExtras().getString("predictionText8").toString();
            String predictionText9 = intent.getExtras().getString("predictionText9").toString();
            String predictionText10 = intent.getExtras().getString("predictionText10").toString();

            audioFile1 = intent.getExtras().getString("audioFile1").toString();
            audioFile2 = intent.getExtras().getString("audioFile2").toString();
            audioFile3 = intent.getExtras().getString("audioFile3").toString();
            audioFile4 = intent.getExtras().getString("audioFile4").toString();
            audioFile5 = intent.getExtras().getString("audioFile5").toString();
            audioFile6 = intent.getExtras().getString("audioFile6").toString();
            audioFile7 = intent.getExtras().getString("audioFile7").toString();
            audioFile8 = intent.getExtras().getString("audioFile8").toString();
            audioFile9 = intent.getExtras().getString("audioFile9").toString();
            audioFile10 = intent.getExtras().getString("audioFile10").toString();

            predictionButton1.setText(predictionText1);
            predictionButton2.setText(predictionText2);
            predictionButton3.setText(predictionText3);
            predictionButton4.setText(predictionText4);
            predictionButton5.setText(predictionText5);
            predictionButton6.setText(predictionText6);
            predictionButton7.setText(predictionText7);
            predictionButton8.setText(predictionText8);
            predictionButton9.setText(predictionText9);
            predictionButton10.setText(predictionText10);
        }

//        predict();

    }

//    public void predict() {
//        String filePath = Environment.getExternalStorageDirectory().toString() + "/DCIM/Bhasha/input.mp4";
//
//        API api = Client.get().create(API.class);
//
//        File file = new File(filePath);
//        RequestBody requestBody = RequestBody.create(file, MediaType.parse("video/mp4"));
//        MultipartBody.Part video = MultipartBody.Part.createFormData("video", file.getName(), requestBody);
//
//
//        if(file.exists() == false) {
//            Log.e(TAG, file + " NOT EXISTS.");
//        }
//        Call<Model> call = api.getPrediction(video);
//
//        Intent intent = new Intent(PredictionActivity.this, ErrorMessageActivity.class);
//        call.enqueue(new Callback<Model>() {
//            @Override
//            public void onResponse(Call<Model> call, Response<Model> response) {
//
//                if (response.isSuccessful()) {
//                    Log.e(TAG, "onResponse: code: " + response.code());
//
//
//                    String predictionText1 = response.body().getPredictionText1();
//                    String predictionText2 = response.body().getPredictionText2();
//                    String predictionText3 = response.body().getPredictionText3();
//                    String predictionText4 = response.body().getPredictionText4();
//                    String predictionText5 = response.body().getPredictionText5();
//
//                    predictionButton1.setText(predictionText1);
//                    predictionButton2.setText(predictionText2);
//                    predictionButton3.setText(predictionText3);
//                    predictionButton4.setText(predictionText4);
//                    predictionButton5.setText(predictionText5);
//
//                    audioFile1 = response.body().getAudioFile1();
//                    audioFile2 = response.body().getAudioFile2();
//                    audioFile3 = response.body().getAudioFile3();
//                    audioFile4 = response.body().getAudioFile4();
//                    audioFile5 = response.body().getAudioFile5();
//
//                    Uri uri = getRawUriFromURL("audio1");
////                    for(File file : files) {
////                        Log.e(TAG, file.toString());
////                    }
//
//                    Log.e(TAG, "audioFile1: " + audioFile1);
//                    Log.e(TAG, "audioFile2: " + audioFile2);
//
//
//                } else {
//                    Log.e(TAG, response.message());
//                    if( response.code() == 500 ) {
//                        intent.putExtra("message", "Problem detecting face.");
//                    } else {
//                        intent.putExtra("message", response.message());
//                    }
//                    startActivity(intent);
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Model> call, Throwable t) {
//                Log.e(TAG, "onFailure: " + t.getMessage());
//                intent.putExtra("message", t.getMessage());
//                startActivity(intent);
//            }
//        });
//    }

    private Uri getRawUriFromURL(String audioFile) {
        Uri uri = Uri.parse(RAW_FOLDER_PATH + audioFile);
        Log.e(TAG, uri.toString() + " Uri");
        return uri;
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(PredictionActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void playAudio1(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile1));
        mediaPlayer.start();
    }

    public void playAudio2(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile2));
        mediaPlayer.start();
    }

    public void playAudio3(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile3));
        mediaPlayer.start();
    }

    public void playAudio4(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile4));
        mediaPlayer.start();
    }

    public void playAudio5(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile5));
        mediaPlayer.start();
    }

    public void playAudio6(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile6));
        mediaPlayer.start();
    }

    public void playAudio7(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile7));
        mediaPlayer.start();
    }

    public void playAudio8(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile8));
        mediaPlayer.start();
    }

    public void playAudio9(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile9));
        mediaPlayer.start();
    }

    public void playAudio10(View view) {
        buttonOnClickState(view);
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, getRawUriFromURL(AUDIO_PREFIX + audioFile10));
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        finish();
    }

    public void buttonOnClickState(View view) {
        view.setBackgroundResource(R.drawable.ic_button_on_click);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                view.setBackgroundResource(R.drawable.ic_button_on_rest);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 300);
    }
}