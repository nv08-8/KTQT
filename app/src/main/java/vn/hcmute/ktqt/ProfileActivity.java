package vn.hcmute.ktqt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.hcmute.ktqt.model.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvUsername;
    private ImageView imgAvatar, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvUsername = findViewById(R.id.tvUsername);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnSettings = findViewById(R.id.btnSettings);

        Intent intent = getIntent();
        String name = intent.getStringExtra("USER_NAME");
        String email = intent.getStringExtra("USER_EMAIL");
        String avatarUrl = intent.getStringExtra("USER_AVATAR");

        tvName.setText(name);
        tvUsername.setText(email);

        btnSettings.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
            settingsIntent.putExtra("USER_NAME", name);
            settingsIntent.putExtra("USER_EMAIL", email);
            settingsIntent.putExtra("USER_AVATAR", avatarUrl);
            startActivity(settingsIntent);
        });

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.placeholder_avatar)
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.placeholder_avatar);
        }

        setupOrderStatusViews();
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
        ivStatus2.setImageResource(R.drawable.ic_order);
        tvStatus2.setText(R.string.status_wait_pickup);

        // Status 3: Đang giao
        View statusShipping = findViewById(R.id.statusShipping);
        ImageView ivStatus3 = statusShipping.findViewById(R.id.ivStatus);
        TextView tvStatus3 = statusShipping.findViewById(R.id.tvStatus);
        ivStatus3.setImageResource(R.drawable.ic_trunk);
        tvStatus3.setText(R.string.status_shipping);

        // Status 4: Đánh giá
        View statusRating = findViewById(R.id.statusRating);
        ImageView ivStatus4 = statusRating.findViewById(R.id.ivStatus);
        TextView tvStatus4 = statusRating.findViewById(R.id.tvStatus);
        ivStatus4.setImageResource(R.drawable.ic_star);
        tvStatus4.setText(R.string.status_rating);
    }
}
