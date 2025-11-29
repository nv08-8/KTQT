package vn.hcmute.ktqt;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 1. Khai báo các biến View
    private ImageView imgAvatar;
    private TextView tvName;
    private TextView tvUsername;
    private LinearLayout btnHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Thiết lập layout từ file XML
        setContentView(R.layout.activity_main);
        // (Lưu ý: Tên file XML mặc định là activity_main.xml)

// Thiết lập dữ liệu ban đầu cho Header (nếu dữ liệu không tĩnh)
// Ví dụ: Lấy dữ liệu người dùng từ database và gán vào View
// tvName.setText(userData.getName());
// tvUsername.setText("@" + userData.getUsername());
// imgAvatar.setImageResource(userData.getAvatarResId());
    }
}