package com.example.bhasha;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit client;
    private static final String BASE_URL = "http://192.168.0.101:5000";

    public static Retrofit get() {
        if(client == null) {
            client = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return client;
    }

}
