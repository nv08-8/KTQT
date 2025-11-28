package vn.hcmute.ktqt.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hcmute.ktqt.models.AuthResponse;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.PagedResponse;
import vn.hcmute.ktqt.models.Product;
import vn.hcmute.ktqt.models.User;
import vn.hcmute.ktqt.models.requests.LoginRequest;
import vn.hcmute.ktqt.models.requests.OtpRequest;
import vn.hcmute.ktqt.models.requests.RegisterRequest;

public interface ApiService {

    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("categories/{id}/products")
    Call<PagedResponse<Product>> getProductsByCategory(@Path("id") String categoryId,
                                                       @Query("page") int page,
                                                       @Query("pageSize") int pageSize,
                                                       @Query("sort") String sort);

    @POST("auth/register")
    Call<Void> register(@Body RegisterRequest body);

    @POST("auth/verify-otp")
    Call<AuthResponse> verifyOtp(@Body OtpRequest body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @GET("auth/profile")
    Call<User> profile();
}

