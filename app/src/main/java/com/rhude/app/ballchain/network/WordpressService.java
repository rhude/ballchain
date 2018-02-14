package com.rhude.app.ballchain.network;

import com.rhude.app.ballchain.model.Post;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by sean on 2018-02-14.
 */

public interface WordpressService {
    @POST("wp/v2/posts")
    Call<ResponseBody> createPost(@Body Post post);

    @Multipart
    @POST("wp/v2/media")
    Call<ResponseBody> uploadFile(@Part MultipartBody.Part file);
}

