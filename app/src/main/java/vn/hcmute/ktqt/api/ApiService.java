package vn.hcmute.ktqt.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import vn.hcmute.ktqt.models.Category;

public interface ApiService {
    @GET("categories") // Replace with your actual endpoint
    Call<List<Category>> getAllCategories();
}