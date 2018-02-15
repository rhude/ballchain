package com.rhude.app.ballchain.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.rhude.app.ballchain.value.AppValues;
import com.rhude.app.ballchain.value.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sean on 2018-02-14.
 */

public class WordpressApi {
    private static final String TAG = WordpressApi.class.getSimpleName();

    private static WordpressApi api;
    private Retrofit retrofit;
    private WordpressService service;

    private WordpressApi() {
    }

    public static void initialize() {
        Log.i(TAG, "okHttp:initializeApi");
        api = new WordpressApi();
    }

    public static WordpressApi getInstance() {
        if (api == null) {
            throw new IllegalStateException("Must call initialize first");
        }
        return api;
    }

    public Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = createRetrofitInstance();
        }

        return retrofit;
    }

    public WordpressService getService() {
        if (service == null) {
            Log.i(TAG, "getServiceCreate");
            service = getRetrofit().create(WordpressService.class);
        }

        return service;
    }

    private Retrofit createRetrofitInstance() {
        Log.i(TAG, "createRetrofitInstance");

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Constants.endpoint + "/wp-json/")
                .addConverterFactory(GsonConverterFactory.create());

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.addInterceptor(authorizationInterceptor);
        clientBuilder.addInterceptor(loggingInterceptor);
        builder.client(clientBuilder.build());
        return builder.build();
    }

    //<editor-fold desc="Interceptors">
    private Interceptor authorizationInterceptor = new Interceptor() {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String auth = Credentials.basic(AppValues.getInstance().getUsername(), AppValues.getInstance().getPassword());
            if (!AppValues.getInstance().hasAuthorization() || (auth == null || auth.isEmpty())) {
                return chain.proceed(originalRequest);
            }

            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .addHeader("Authorization", auth);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
    };

    private Interceptor loggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            long startNs = System.nanoTime();

            Response response = chain.proceed(request);
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

            if (response.code() < 200 || response.code() > 299) {
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    return response;
                }
                String rawJson = response.body().string();
                Log.e(TAG, String.format("%s %s - %s: %s - %s ms", request.method(), request.url().toString(), response.code(), rawJson, tookMs));

                return response.newBuilder().body(ResponseBody.create(response.body().contentType(), rawJson)).build();
            } else {
                Log.d(TAG, String.format("%s %s - %s ms", request.method(), request.url().toString(), tookMs));
                return response;
            }
        }
    };
    //</editor-fold>

    //<editor-fold desc="Helper">
    public static String checkResponseForError(retrofit2.Response<?> response) {
        String error = null;

        if (response.raw().code() < 200 || response.raw().code() > 299) {
            try {
                error = response.errorBody().string();
            } catch (IOException e) {
                error = response.raw().message();
            }
        }
        return error;
    }
    //</editor-fold>
}
