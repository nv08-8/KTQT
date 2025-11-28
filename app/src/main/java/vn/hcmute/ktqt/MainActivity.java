package vn.hcmute.ktqt;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        setupViews();
        setupAdapters();

        loadCategories();
        loadAllProducts(); // Initially load all books
    }

    private void setupViews() {
        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupAdapters() {
        categoryAdapter = new CategoryAdapter(category -> {
            // When a category is clicked, load books for that category
            loadProductsByCategory(category.getName());
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        productAdapter = new ProductAdapter();
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        rvProducts.setAdapter(productAdapter);
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    categoryAdapter.setItems(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAllProducts() {
        progressBar.setVisibility(View.VISIBLE);
        api.getAllBooks().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setItems(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load books", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductsByCategory(String categoryName) {
        progressBar.setVisibility(View.VISIBLE);
        api.getBooksByCategory(categoryName).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    productAdapter.setItems(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load books for category: " + categoryName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
