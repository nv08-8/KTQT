//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.adapters.CategoryAdapter;
import vn.hcmute.ktqt.adapters.ProductAdapter;
import vn.hcmute.ktqt.data.SessionManager;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.PagedResponse;
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

    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String selectedCategoryId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            session.clear();
            startActivity(new Intent(MainActivity.this, vn.hcmute.ktqt.ui.auth.LoginActivity.class));
            finish();
        });

        // Load data
        loadCategories();

        // If not logged in, navigate to login
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, vn.hcmute.ktqt.ui.auth.LoginActivity.class));
            finish();
            return;
        }

        // Optionally load profile (not implemented) and UI updates
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Category> cats = response.body();
                    categoryAdapter.setItems(cats);
                    if (cats != null && !cats.isEmpty()) {
                        // auto select first
                        selectedCategoryId = cats.get(0).id;
                        currentPage = 1;
                        productAdapter.clear();
                        loadProducts(selectedCategoryId, currentPage);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Lỗi mạng khi tải categories", Toast.LENGTH_SHORT).show();
            }
        });
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