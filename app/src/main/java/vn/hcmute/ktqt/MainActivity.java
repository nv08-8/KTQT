package vn.hcmute.ktqt;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.adapters.CategoryAdapter;
import vn.hcmute.ktqt.adapters.ProductAdapter;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.PagedResponse;
import vn.hcmute.ktqt.models.Product;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.network.RetrofitClient;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        api = RetrofitClient.getClient(this).create(ApiService.class);

        rvCategories = findViewById(R.id.rvCategories);
        rvProducts = findViewById(R.id.rvProducts);
        progressBar = findViewById(R.id.progressBar);

        setupCategoryRecyclerView();
        setupProductRecyclerView();

        loadCategories();
    }

    private void setupCategoryRecyclerView() {
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(category -> {
            selectedCategoryId = category.id;
            currentPage = 1;
            isLastPage = false;
            productAdapter.clear();
            loadProducts(selectedCategoryId, currentPage);
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupProductRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvProducts.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter();
        rvProducts.setAdapter(productAdapter);

        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return;
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        loadProducts(selectedCategoryId, currentPage + 1);
                    }
                }
            }
        });
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        api.getCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryAdapter.setItems(categories);
                    if (!categories.isEmpty()) {
                        // Auto-select the first category
                        categories.get(0).isSelected = true;
                        categoryAdapter.notifyDataSetChanged();
                        selectedCategoryId = categories.get(0).id;
                        loadProducts(selectedCategoryId, 1);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Network error while loading categories", Toast.LENGTH_SHORT).show();
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
                if (response.isSuccessful() && response.body() != null) {
                    PagedResponse<Product> pagedResponse = response.body();
                    if (page == 1) {
                        productAdapter.clear();
                    }
                    productAdapter.addItems(pagedResponse.items);
                    currentPage = pagedResponse.page;
                    isLastPage = pagedResponse.page >= pagedResponse.totalPages;
                } else {
                    Toast.makeText(MainActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Product>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(MainActivity.this, "Network error while loading products", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
