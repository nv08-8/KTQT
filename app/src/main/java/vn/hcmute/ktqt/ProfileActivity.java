package vn.hcmute.ktqt;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.model.User;
import vn.hcmute.ktqt.network.RetrofitClient;
import vn.hcmute.ktqt.network.ApiService;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvUsername;
    private ImageView imgAvatar;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        imgAvatar = findViewById(R.id.imgAvatar);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        fetchUserProfile();
        setupOrderStatusViews();
    }

    private void fetchUserProfile() {
        apiService.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    updateUI(user);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        tvName.setText(user.getName());
        tvUsername.setText(user.getEmail()); // Assuming username is the email

        Glide.with(this)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.placeholder_avatar)
                .into(imgAvatar);
    }

    private void setupOrderStatusViews() {
        // Status 1: Chờ xác nhận
        View statusWaitConfirm = findViewById(R.id.statusWaitConfirm);
        ImageView ivStatus1 = statusWaitConfirm.findViewById(R.id.ivStatus);
        TextView tvStatus1 = statusWaitConfirm.findViewById(R.id.tvStatus);
        ivStatus1.setImageResource(R.drawable.ic_box);
        tvStatus1.setText(R.string.status_wait_confirm);

        // Status 2: Chờ lấy hàng
        View statusWaitPickup = findViewById(R.id.statusWaitPickup);
        ImageView ivStatus2 = statusWaitPickup.findViewById(R.id.ivStatus);
        TextView tvStatus2 = statusWaitPickup.findViewById(R.id.tvStatus);
        ivStatus2.setImageResource(R.drawable.ic_creditcard);
        tvStatus2.setText(R.string.status_wait_pickup);

        // Status 3: Đang giao
        View statusShipping = findViewById(R.id.statusShipping);
        ImageView ivStatus3 = statusShipping.findViewById(R.id.ivStatus);
        TextView tvStatus3 = statusShipping.findViewById(R.id.tvStatus);
        ivStatus3.setImageResource(R.drawable.ic_order);
        tvStatus3.setText(R.string.status_shipping);

        // Status 4: Đánh giá
        View statusRating = findViewById(R.id.statusRating);
        ImageView ivStatus4 = statusRating.findViewById(R.id.ivStatus);
        TextView tvStatus4 = statusRating.findViewById(R.id.tvStatus);
        ivStatus4.setImageResource(R.drawable.ic_star);
        tvStatus4.setText(R.string.status_rating);
    }
}
