//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.adapters.CategoryAdapter;
import vn.hcmute.ktqt.adapters.ProductAdapter;
import vn.hcmute.ktqt.data.SessionManager;
import vn.hcmute.ktqt.model.User;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.responses.PagedResponse;
import vn.hcmute.ktqt.models.Product;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.network.RetrofitClient;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private ApiService api;

    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    private RecyclerView rvCategories;
    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private LinearLayout profileBtn;

    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String selectedCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        session = new SessionManager(this);
        api = RetrofitClient.getClient(this).create(ApiService.class);

        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
        profileBtn = findViewById(R.id.profileBtn);

        profileBtn.setOnClickListener(v -> {
            // Navigate to ProfileActivity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            User user = session.getUserDetails();
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_EMAIL", user.getEmail());
            intent.putExtra("USER_PHONE", user.getPhone());
            intent.putExtra("USER_AVATAR", user.getAvatarUrl());
            startActivity(intent);
        });

        categoryAdapter = new CategoryAdapter(category -> {
            // on category click
            selectedCategoryId = category.id;
            currentPage = 1;
            isLastPage = false;
            productAdapter.clear();
            loadProducts(selectedCategoryId, currentPage);
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter();
        GridLayoutManager gm = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(gm);
        rvProducts.setAdapter(productAdapter);

        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return;
                int visibleItemCount = gm.getChildCount();
                int totalItemCount = gm.getItemCount();
                int firstVisibleItemPosition = gm.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        loadProducts(selectedCategoryId, currentPage + 1);
                    }
                }
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        // Mock data for categories
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Beef"));
        categories.add(new Category("2", "Chicken"));
        categories.add(new Category("3", "Dessert"));
        categories.add(new Category("4", "Drink"));
        categories.add(new Category("5", "Pork"));
        categoryAdapter.setItems(categories);

        if (!categories.isEmpty()) {
            // auto select first
            selectedCategoryId = categories.get(0).id;
            currentPage = 1;
            productAdapter.clear();
            loadProducts(selectedCategoryId, currentPage);
        }
    }

    private void loadProducts(String categoryId, int page) {
        if (categoryId == null) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        api.getProductsByCategory(categoryId, page, pageSize, "price_asc").enqueue(new Callback<PagedResponse<Product>>() {
            @Override
            public void onResponse(Call<PagedResponse<Product>> call, Response<PagedResponse<Product>> response) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                if (response.isSuccessful()) {
                    PagedResponse<Product> body = response.body();
                    if (body != null && body.items != null) {
                        if (page == 1) productAdapter.clear();
                        productAdapter.addItems(body.items);
                        currentPage = body.page;
                        isLastPage = body.page >= body.totalPages;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(MainActivity.this, "Lỗi mạng khi tải sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
