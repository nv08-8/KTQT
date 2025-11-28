package vn.hcmute.ktqt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.adapters.BookAdapter;
import vn.hcmute.ktqt.adapters.CategoryAdapter;
import vn.hcmute.ktqt.data.SessionManager;
import vn.hcmute.ktqt.models.Book;
import vn.hcmute.ktqt.models.Category;
import vn.hcmute.ktqt.models.responses.PagedResponse;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.network.RetrofitClient;
import vn.hcmute.ktqt.ui.intro.IntroActivity;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private ApiService api;

    private CategoryAdapter categoryAdapter;
    private BookAdapter bookAdapter;

    private RecyclerView rvCategories;
    private RecyclerView rvBooks;
    private ProgressBar progressBar;
    private Button btnLogout;
    private TextView tvName, tvEmail;
    private ImageView ivAvatar;

    private int currentPage = 1;
    private final int pageSize = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private String selectedCategoryName = null;

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

        // Bind views
        rvCategories = findViewById(R.id.rvCategories);
        rvBooks = findViewById(R.id.rvBooks);
        progressBar = findViewById(R.id.progressBar);
        btnLogout = findViewById(R.id.btnLogout);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        ivAvatar = findViewById(R.id.ivAvatar);

        // Setup navigation
        setupBottomNavigationBar();

        // Load user info
        loadUserInfo();

        btnLogout.setOnClickListener(v -> {
            session.clear();
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
        });

        categoryAdapter = new CategoryAdapter(category -> {
            selectedCategoryName = category.name;
            currentPage = 1;
            isLastPage = false;
            bookAdapter.clear();
            loadBooks(selectedCategoryName, currentPage);
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        bookAdapter = new BookAdapter();
        GridLayoutManager gm = new GridLayoutManager(this, 2);
        rvBooks.setLayoutManager(gm);
        rvBooks.setAdapter(bookAdapter);

        rvBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return;
                int visibleItemCount = gm.getChildCount();
                int totalItemCount = gm.getItemCount();
                int firstVisibleItemPosition = gm.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 4) {
                        loadBooks(selectedCategoryName, currentPage + 1);
                    }
                }
            }
        });

        loadCategories();
    }

    private void setupBottomNavigationBar() {
        LinearLayout homeBtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);

        homeBtn.setOnClickListener(v -> {
            // Do nothing, already on Home
        });

        profileBtn.setOnClickListener(v -> {
            Map<String, Object> user = session.getUser();
            if (user == null) {
                Toast.makeText(this, "User data not found.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("USER_NAME", (String) user.get("name"));
            intent.putExtra("USER_EMAIL", (String) user.get("email"));
            intent.putExtra("USER_AVATAR", (String) user.get("ava"));
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        Map<String, Object> user = session.getUser();
        if (user != null) {
            String name = (String) user.get("name");
            String email = (String) user.get("email");
            String avatarUrl = (String) user.get("ava");

            tvName.setText(name != null ? name : "");
            tvEmail.setText(email != null ? email : "");

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this).load(avatarUrl).circleCrop().into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.mipmap.ic_launcher); // Default avatar
            }
        }
    }

    private void loadCategories() {
        api.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body();
                    categoryAdapter.setItems(categories);

                    if (!categories.isEmpty()) {
                        selectedCategoryName = categories.get(0).name;
                        currentPage = 1;
                        isLastPage = false;
                        bookAdapter.clear();
                        loadBooks(selectedCategoryName, currentPage);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi mạng khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBooks(String categoryName, int page) {
        if (categoryName == null) return;
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        api.getBooksByCategory(categoryName, page, pageSize, "price_asc").enqueue(new Callback<PagedResponse<Book>>() {
            @Override
            public void onResponse(Call<PagedResponse<Book>> call, Response<PagedResponse<Book>> response) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                if (response.isSuccessful()) {
                    PagedResponse<Book> body = response.body();
                    if (body != null && body.items != null) {
                        if (page == 1) bookAdapter.clear();
                        bookAdapter.addItems(body.items);
                        currentPage = body.page;
                        isLastPage = body.page >= body.totalPages;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Không thể tải sách", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<Book>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                Toast.makeText(MainActivity.this, "Lỗi mạng khi tải sách", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
