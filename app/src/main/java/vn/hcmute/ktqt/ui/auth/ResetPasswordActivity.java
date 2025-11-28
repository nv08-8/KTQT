package vn.hcmute.ktqt.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.network.RetrofitClient;
import vn.hcmute.ktqt.network.ApiService;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmNewPassword;
    private Button btnUpdatePassword;
    private ImageView ivBack;
    private String userEmail;
    private String otp;

    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("RESET_PASSWORD", "onCreate() called");

        setContentView(R.layout.activity_reset_password);

        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);
        ivBack = findViewById(R.id.ivBack);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        // Get data from OtpVerificationActivity
        Intent intent = getIntent();

        // Handle both "email" and "user_email" keys for compatibility
        userEmail = intent.getStringExtra("email");
        if (userEmail == null) {
            userEmail = intent.getStringExtra("user_email");
        }

        otp = intent.getStringExtra("otp"); // The verified OTP

        android.util.Log.d("RESET_PASSWORD", "Received extras: email=" + userEmail + ", otp=" + otp);

        if (userEmail == null || otp == null) {
            android.util.Log.e("RESET_PASSWORD", "Missing email or otp! userEmail=" + userEmail + ", otp=" + otp);
            Toast.makeText(this, "Lỗi: Phiên đặt lại mật khẩu không hợp lệ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        android.util.Log.d("RESET_PASSWORD", "Setup complete for email: " + userEmail);
        Toast.makeText(this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();

        btnUpdatePassword.setOnClickListener(v -> updatePassword());
        ivBack.setOnClickListener(v -> finish());
    }

    private void updatePassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmNewPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu mới.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", userEmail);
        body.put("otp", otp);
        body.put("newPassword", newPassword);

        apiService.resetPassword(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null && (Boolean) response.body().get("success")) {
                    Toast.makeText(ResetPasswordActivity.this, "Mật khẩu đã được cập nhật thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Không thể cập nhật mật khẩu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
