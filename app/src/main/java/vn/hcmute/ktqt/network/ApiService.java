//Vo Nguyen Quynh Nhu - 23162074
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
import vn.hcmute.ktqt.models.responses.AuthResponse;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.responses.PagedResponse;
import vn.hcmute.ktqt.models.Product;
import vn.hcmute.ktqt.models.requests.*;

public interface ApiService {

    // Corrected Paths
    @GET("api/categories")
    Call<List<Category>> getCategories();

    @GET("api/categories/{id}/products")
    Call<PagedResponse<Product>> getProductsByCategory(@Path("id") String categoryId,
                                                       @Query("page") int page,
                                                       @Query("pageSize") int pageSize,
                                                       @Query("sort") String sort);

    // Corrected Paths
    @POST("api/register")
    Call<Void> register(@Body RegisterRequest body);

    @POST("api/verify-otp")
    Call<Map<String, Object>> verifyOtp(@Body Map<String, String> body);

    @POST("api/login")
    Call<Map<String, Object>> login(@Body Map<String, String> body);

    @POST("api/send-otp")
    Call<Map<String, Object>> sendOtp(@Body Map<String, String> body);

    @POST("api/finish-register")
    Call<Map<String, Object>> finishRegister(@Body Map<String, String> body);

    @POST("api/reset-password")
    Call<Map<String, Object>> resetPassword(@Body Map<String, String> body);

    @POST("api/forgot-password")
    Call<Map<String, Object>> forgotPassword(@Body Map<String, String> body);

    Call<List<Category>> getAllCategories();

    Call<User> getUserProfile();
}
