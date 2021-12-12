package com.example.bhasha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.VideoCapture;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.view.CameraView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DatasetActivity extends AppCompatActivity {

    private final String APP_ASSET_FOLDER = "Bhasha";
    private static final String TAG = "MainActivity";
    private static DatasetActivity instance;
    private final int CAMERA_LENS_FRONT = 0;


    FloatingActionButton flipCameraButton;
    FloatingActionButton startRecordButton;
    FloatingActionButton stopRecordButton;
    FloatingActionButton retakeButton;
    FloatingActionButton predictionButton;

    long startTime = 0;
    long endTime = 0;

    private final Executor executor = Executors.newSingleThreadExecutor();
    CameraSelector cameraSelector;
    CameraView mCameraView;

    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final String[] REQUIRED_PERMISSIONS = new String[] {
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.RECORD_AUDIO"};

    private int milliseconds;
    private boolean isRunning;
    private boolean wasRunning;
    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataset);
        instance = this;

        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                handler.postDelayed(this, 1000);
            }
        };

        if(savedInstanceState != null) {
            savedInstanceState.getInt("milliseconds");
            savedInstanceState.getBoolean("isRunning");
            savedInstanceState.getBoolean("wasRunning");
        }
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void startCamera() {
        startRecordButton = findViewById(R.id.startRecordButton);
        stopRecordButton = findViewById(R.id.stopRecordButton);
        flipCameraButton = findViewById(R.id.flipCameraButton);
        retakeButton = findViewById(R.id.retakeButton);
        predictionButton = findViewById(R.id.predictionButton);

        stopRecordButton.setVisibility(View.GONE);
        predictionButton.setVisibility(View.GONE);


        mCameraView = findViewById(R.id.cameraViewFinder);

        mCameraView.setCameraLensFacing(CAMERA_LENS_FRONT);


        mCameraView.setFlash(ImageCapture.FLASH_MODE_OFF);
        ImageCapture.Builder builder = new ImageCapture.Builder();
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mCameraView.bindToLifecycle((LifecycleOwner) DatasetActivity.this);

        mCameraView.setCaptureMode(CameraView.CaptureMode.VIDEO);

        startRecordButton.setOnClickListener(v -> {
            if(mCameraView.isRecording()) {
                return;
            }

            startRecordButton.setVisibility(View.GONE);
            stopRecordButton.setVisibility(View.VISIBLE);
            predictionButton.setVisibility(View.VISIBLE);

            onStart();
            startTime = System.currentTimeMillis();
            endTime = startTime;
            startTimer();

            String externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_DCIM).toString();
            File file = new File(getBatchDirectoryName(externalFilesDir, APP_ASSET_FOLDER), "input.mp4");

            mCameraView.startRecording(file, executor, new VideoCapture.OnVideoSavedCallback() {

                @Override
                public void onVideoSaved(@NonNull VideoCapture.OutputFileResults outputFileResults) {
                    String dir = Environment.getExternalStorageDirectory().toString() + "/DCIM/" + APP_ASSET_FOLDER;
                    File folder = new File(dir);

                    if(!folder.exists()) {
                        folder.mkdirs();
                    } else {
                        File[] files = folder.listFiles();
                        for(File f: files) {
                            Log.e(TAG, "file: " + f.toString());
                            if(f.exists()) f.delete();
                        }
                    }

                    Log.e(TAG, "App dir: " + dir);


                    galleryAddPic(file, 1);
                }

                @Override
                public void onError(int videoCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    mCameraView.stopRecording();
                }

            });
        });

        stopRecordButton.setOnClickListener(v -> {
            Log.e(TAG, "Stop Button Clicked");
            if (mCameraView.isRecording()) {
                mCameraView.stopRecording();
            }
            isRunning = false;
        });

        flipCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCameraView.isRecording()) {
                    return;
                }
                if (ActivityCompat.checkSelfPermission(DatasetActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mCameraView.toggleCamera();
                startTime=endTime=0;
                isRunning = false;
            }
        });

        retakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.stopRecording();
                onReset();
                startRecordButton.setVisibility(View.VISIBLE);
                stopRecordButton.setVisibility(View.GONE);
                predictionButton.setVisibility(View.GONE);
                startTime = endTime = 0;
                isRunning = false;
            }
        });

        final  InProgress inProgress = new InProgress(DatasetActivity.this);
        predictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inProgress.on();

                startRecordButton.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        inProgress.off();
                        Intent intent = new Intent(DatasetActivity.this, PredictionServiceActivity.class);
//                        Intent intent = new Intent(HomeActivity.this, UploadActivity.class);
                        intent.putExtra("label", "001");
                        startActivity(intent);

                        mCameraView.stopRecording();
                        onReset();
                        startTime=endTime=0;
                        isRunning=false;
                        stopRecordButton.setVisibility(View.GONE);
                        predictionButton.setVisibility(View.GONE);
                    }
                }, 1000);
            }
        });
    }

    public boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            } else{
                this.finish();
            }
        }
    }


    public String getBatchDirectoryName(String externalFilesDir, String appAssetFolder) {
        String app_folder_path;
        app_folder_path = externalFilesDir + "/" + appAssetFolder;
        File dir = new File(app_folder_path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return app_folder_path;
    }


    private void galleryAddPic(File originalFile, int mediaType) {
        if (!originalFile.exists()) {
            return;
        }

        int pathSeparator = String.valueOf(originalFile).lastIndexOf('/');
        int extensionSeparator = String.valueOf(originalFile).lastIndexOf('.');
        String filename = pathSeparator >= 0 ? String.valueOf(originalFile).substring(pathSeparator + 1) : String.valueOf(originalFile);
        String extension = extensionSeparator >= 0 ? String.valueOf(originalFile).substring(extensionSeparator + 1) : "";

        // Credit: https://stackoverflow.com/a/31691791/2373034
        String mimeType = extension.length() > 0 ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ENGLISH)) : null;
        Log.e(TAG, "MimeType: " + mimeType);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, filename);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);

        if (mimeType != null && mimeType.length() > 0)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

        Uri externalContentUri;
        if (mediaType == 0)
            externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else if (mediaType == 1)
            externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        else
            externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Android 10 restricts our access to the raw filesystem, use MediaStore to save media in that case
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + APP_ASSET_FOLDER);
            values.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.IS_PENDING, true);

            Uri uri = getContentResolver().insert(externalContentUri, values);
            Log.e(TAG, "Gallery uri" + uri.toString());
            if (uri != null) {
                this.uri = uri;
                try {
                    if (WriteFileToStream(originalFile, getContentResolver().openOutputStream(uri))) {
                        values.put(MediaStore.MediaColumns.IS_PENDING, false);
                        getContentResolver().update(uri, values, null, null);
                    }
                } catch (Exception e) {
                    getContentResolver().delete(uri, null, null);
                }
            }
            originalFile.delete();
        } else {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(originalFile));
            sendBroadcast(mediaScanIntent);
        }

    }


    private static boolean WriteFileToStream(File file, OutputStream out) {
        try {
            InputStream in = new FileInputStream(file);
            try {
                byte[] buffer = new byte[1024];
                int len;
                while( ( len = in.read( buffer ) ) > 0 )
                    out.write( buffer, 0, len );
            }
            finally {
                try {
                    in.close();
                }
                catch( Exception e ) {
                    //Log.e( "Unity", "Exception:", e );
                }
            }
        }
        catch( Exception e ) {
            //Log.e( "Unity", "Exception:", e );
            return false;
        }
        finally {
            try {
                out.close();
            }
            catch( Exception e ) {
                //Log.e( "Unity", "Exception:", e );
            }
        }
        return true;
    } //write end

    List<String> getVocabs(String filename) {
        BufferedReader reader = null;
        List<String> sentences = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(filename), "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sentences.add(line);
            }
            return sentences;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return sentences;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("milliseconds", milliseconds);
        outState.putBoolean("isRunning", isRunning);
        outState.putBoolean("wasRunning", wasRunning);
    }

    private void startTimer() {
        final TextView timeView = findViewById(R.id.timeView);
        Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long d = endTime - startTime;
                long millisecs = (d % 1000) / 10;
                long secs = d / 1000;
                String time = String.format(Locale.getDefault(), "%02d:%02d", secs, millisecs);

                timeView.setText(time);
                if(secs >= 3) {
                    mCameraView.stopRecording();
                    isRunning = false;
                }

                if (isRunning) {
                    endTime = System.currentTimeMillis();
                    milliseconds += 1;
                }
                handler.postDelayed(this, 1);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        isRunning = true;
        wasRunning = false;
        milliseconds = 0;
    }


    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
            onReset();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRunning = false;
        milliseconds = 0;
        wasRunning = false;
        TextView timeView = findViewById(R.id.timeView);
        timeView.setText("00:00");
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = false;
        milliseconds = 0;
        wasRunning = false;
        TextView timeView = findViewById(R.id.timeView);
        timeView.setText("00:00");
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        milliseconds = 0;
        wasRunning = false;
        TextView timeView = findViewById(R.id.timeView);
        timeView.setText("00:00");
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
            onReset();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        milliseconds = 0;
        wasRunning = false;
        TextView timeView = findViewById(R.id.timeView);
        timeView.setText("00:00");
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
            onReset();
        }
        finish();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        isRunning = false;
        milliseconds = 0;
        wasRunning = false;
        TextView timeView = findViewById(R.id.timeView);
        timeView.setText("00:00");
    }

    public void onReset() {
        isRunning = false;
        milliseconds = 0;
        wasRunning = false;
        TextView timeView = findViewById(R.id.timeView);
        timeView.setText("00:00");
    }
}