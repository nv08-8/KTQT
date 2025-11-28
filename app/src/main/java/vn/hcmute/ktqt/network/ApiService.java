package vn.hcmute.ktqt.network;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hcmute.ktqt.model.User;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.Product;
import vn.hcmute.ktqt.models.responses.PagedResponse;

public interface ApiService {

    @GET("api/user/profile")
    Call<User> getUserProfile();

    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/products/category/{categoryId}")
    Call<PagedResponse<Product>> getProductsByCategory(
            @Path("categoryId") String categoryId,
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("sortBy") String sortBy
    );

    @POST("api/auth/forgot-password")
    Call<Map<String, Object>> forgotPassword(@Body Map<String, String> body);

    @POST("api/auth/login")
    Call<Map<String, Object>> login(@Body Map<String, String> body);

    @POST("api/auth/verify-otp")
    Call<Map<String, Object>> verifyOtp(@Body Map<String, String> body);

    @POST("api/auth/finish-register")
    Call<Map<String, Object>> finishRegister(@Body Map<String, String> body);

    @POST("api/auth/send-otp")
    Call<Map<String, Object>> sendOtp(@Body Map<String, String> body);

    @POST("api/auth/reset-password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> body);
}
