//Nguyen Van Truong 23162108
package vn.hcmute.ktqt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.adapters.CategoryAdapter;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.network.RetrofitClient;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        rvCategories = findViewById(R.id.rv_categories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Khởi tạo adapter với một listener (có thể trống)
        categoryAdapter = new CategoryAdapter(category -> {
            // Xử lý sự kiện khi một mục được chọn (nếu cần)
        });
        rvCategories.setAdapter(categoryAdapter);

        fetchCategories();
    }

    private void fetchCategories() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Sử dụng phương thức setItems của adapter để cập nhật dữ liệu
                    categoryAdapter.setItems(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Handle failure - e.g., show a toast message
            }
        });
    }
}