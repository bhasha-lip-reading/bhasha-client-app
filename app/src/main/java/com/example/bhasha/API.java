package com.example.bhasha;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface API {
    @Multipart
    @POST("/api/prediction")
    Call<Model> getPrediction(
            @Part("label") RequestBody label,
            @Part MultipartBody.Part video);

    @Multipart
    @POST("api/upload")
    Call<ResponseBody> upload(
            @Part("label") RequestBody label,
            @Part MultipartBody.Part  video);
}
