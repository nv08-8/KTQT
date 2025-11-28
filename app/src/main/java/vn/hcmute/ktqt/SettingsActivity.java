package vn.hcmute.ktqt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

        // Get user info from the session
        Map<String, Object> user = sessionManager.getUser();
        String name = "";
        String email = "";
        String phone = "";
        String avatarUrl = "";

        if (user != null) {
            if (user.containsKey("name")) {
                name = (String) user.get("name");
            }
            if (user.containsKey("email")) {
                email = (String) user.get("email");
            }
            if (user.containsKey("phone")) {
                phone = (String) user.get("phone");
            }
             if (user.containsKey("avatar")) {
                avatarUrl = (String) user.get("avatar");
            }
        }

        // Set the text for the views
        tvName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvNameHeader.setText(name);
        tvEmailHeader.setText(email);

        // Load the avatar image
        Glide.with(this)
                .load(avatarUrl)
                .placeholder(R.drawable.placeholder_avatar)
                .into(imgAvatar);
    }
}
