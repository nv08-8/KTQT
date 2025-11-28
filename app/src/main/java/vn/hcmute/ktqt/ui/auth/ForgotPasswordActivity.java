package vn.hcmute.ktqt.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.network.RetrofitClient;
import vn.hcmute.ktqt.network.ApiService;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnResetPassword;
    private ImageView ivBack;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        ivBack = findViewById(R.id.ivBack);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        btnResetPassword.setOnClickListener(v -> sendRecoveryEmail());

        ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void sendRecoveryEmail() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ email hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        btnResetPassword.setEnabled(false);
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        apiService.forgotPassword(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnResetPassword.setEnabled(true);

                android.util.Log.d("FORGOT_PASSWORD", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    android.util.Log.d("FORGOT_PASSWORD", "Response body: " + res.toString());

                    // Check success field in response
                    Object successObj = res.get("success");
                    boolean success = successObj != null && (Boolean) successObj;

                    if (success) {
                        android.util.Log.d("FORGOT_PASSWORD", "Success! Navigating to OTP screen...");
                        android.util.Log.d("FORGOT_PASSWORD", "Email: " + email);

                        Toast.makeText(ForgotPasswordActivity.this, "OTP đã được gửi đến email. Vui lòng kiểm tra hộp thư.", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ForgotPasswordActivity.this, OtpVerificationActivity.class);
                        intent.putExtra("user_email", email);
                        intent.putExtra("context", "forgot_password");

                        android.util.Log.d("FORGOT_PASSWORD", "Intent created with extras: user_email=" + email + ", context=forgot_password");

                        try {
                            startActivity(intent);
                            android.util.Log.d("FORGOT_PASSWORD", "startActivity() called successfully");
                        } catch (Exception e) {
                            android.util.Log.e("FORGOT_PASSWORD", "Error starting activity", e);
                            Toast.makeText(ForgotPasswordActivity.this, "Lỗi khi chuyển màn hình: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Small delay before finish to ensure activity transition
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            android.util.Log.d("FORGOT_PASSWORD", "Finishing ForgotPasswordActivity");
                            finish();
                        }, 100);
                    } else {
                        String message = res.get("message") != null ? res.get("message").toString() : "Có lỗi xảy ra";
                        Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error response (404, etc)
                    String errorMsg = "Email không tồn tại trong hệ thống.";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            android.util.Log.e("FORGOT_PASSWORD", "Error body: " + errorBody);
                            // Try to parse error message
                            if (errorBody.contains("message")) {
                                errorMsg = errorBody;
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.e("FORGOT_PASSWORD", "Error reading errorBody", e);
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnResetPassword.setEnabled(true);
                android.util.Log.e("FORGOT_PASSWORD", "Request failed", t);
                Toast.makeText(ForgotPasswordActivity.this, "Không thể kết nối đến máy chủ: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
