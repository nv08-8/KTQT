//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import vn.hcmute.ktqt.models.AuthResponse;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.Product;
import vn.hcmute.ktqt.models.requests.LoginRequest;
import vn.hcmute.ktqt.models.requests.OtpRequest;
import vn.hcmute.ktqt.models.requests.RegisterRequest;
import vn.hcmute.ktqt.models.requests.SendOtpRequest;

public interface ApiService {

    // --- Category APIs ---
    @GET("api/categories")
    Call<List<Category>> getCategories();

    // --- Book/Product APIs ---
    @GET("api/books")
    Call<List<Product>> getAllBooks();

    @GET("api/books/category/{name}")
    Call<List<Product>> getBooksByCategory(@Path("name") String categoryName);

    // --- Auth APIs ---
    @POST("api/register")
    Call<Void> register(@Body RegisterRequest body);

    @POST("api/verify-otp")
    Call<AuthResponse> verifyOtp(@Body OtpRequest body);

    @POST("api/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("api/send-otp")
    Call<Void> sendOtp(@Body SendOtpRequest body);
}
