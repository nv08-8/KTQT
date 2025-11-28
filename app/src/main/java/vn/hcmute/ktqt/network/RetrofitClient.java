//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.hcmute.ktqt.data.SessionManager;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    // Use the emulator loopback address to reach the host machine's backend during development
    private static final String BASE_URL = "https://refreshing-respect-production.up.railway.app/"; // was: "https://api.uteboo.com/" // TODO: use BuildConfig for env-specific URLs
    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            SessionManager session = new SessionManager(context);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging);

            httpClient.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder();
                String token = session.getToken();
                if (token != null && !token.isEmpty()) {
                    builder.header("Authorization", "Bearer " + token);
                }
                Request request = builder.build();
                return chain.proceed(request);
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
