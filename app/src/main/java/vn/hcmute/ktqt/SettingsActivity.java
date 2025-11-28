package vn.hcmute.ktqt;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.Map;

import vn.hcmute.ktqt.data.SessionManager;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvPhone, tvNameHeader, tvEmailHeader;
    private ImageView imgAvatar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvNameHeader = findViewById(R.id.tvNameHeader);
        tvEmailHeader = findViewById(R.id.tvEmailHeader);
        imgAvatar = findViewById(R.id.imgAvatar);

        sessionManager = new SessionManager(this);

        loadUserInfo();
    }

    private void loadUserInfo() {
        Map<String, Object> user = sessionManager.getUser();
        if (user != null) {
            String name = (String) user.get("name");
            String email = (String) user.get("email");
            String phone = (String) user.get("phone");
            String avatarUrl = (String) user.get("ava");

            tvName.setText(name != null ? name : "");
            tvEmail.setText(email != null ? email : "");
            tvPhone.setText(phone != null ? phone : "");
            tvNameHeader.setText(name != null ? name : "");
            tvEmailHeader.setText(email != null ? email : "");

            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(this).load(avatarUrl).circleCrop().into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.placeholder_avatar);
            }
        }
    }
}
