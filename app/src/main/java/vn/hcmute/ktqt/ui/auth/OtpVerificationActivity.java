package vn.hcmute.ktqt.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.network.RetrofitClient;
import vn.hcmute.ktqt.network.ApiService;

public class OtpVerificationActivity extends AppCompatActivity {

    private EditText edtOtp;
    private Button btnVerify;
    private TextView tvResendOtp;
    private ImageView ivBack;

    // Đổi tên biến để nhất quán
    private String email, phone, name, password, context;

    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("OTP_VERIFICATION", "onCreate() called");

        setContentView(R.layout.activity_otp_verification);

        edtOtp = findViewById(R.id.edtOtp);
        btnVerify = findViewById(R.id.btnVerify);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        ivBack = findViewById(R.id.ivBack);

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        Intent intent = getIntent();

        // Handle both "email" and "user_email" keys for compatibility
        email = intent.getStringExtra("email");
        if (email == null) {
            email = intent.getStringExtra("user_email");
        }

        phone = intent.getStringExtra("phone");
        name = intent.getStringExtra("name");
        password = intent.getStringExtra("password");
        context = intent.getStringExtra("context");

        android.util.Log.d("OTP_VERIFICATION", "Received extras: email=" + email + ", context=" + context);

        if (email == null || context == null) {
            android.util.Log.e("OTP_VERIFICATION", "Missing email or context! email=" + email + ", context=" + context);
            Toast.makeText(this, "Lỗi: Không nhận được email hoặc context.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        android.util.Log.d("OTP_VERIFICATION", "Setup complete for context: " + context);

        if ("register".equals(context)) {
            if (name == null || password == null || phone == null) {
                Toast.makeText(this, "Lỗi: Thiếu thông tin đăng ký. Vui lòng thử lại từ đầu.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }

        btnVerify.setOnClickListener(v -> verifyOtp());
        tvResendOtp.setOnClickListener(v -> resendOtp());
        ivBack.setOnClickListener(v -> finish());
    }

    private void verifyOtp() {
        String otp = edtOtp.getText().toString().trim();
        android.util.Log.d("OTP_VERIFICATION", "verifyOtp() called, otp=" + otp + ", context=" + context);

        if (otp.length() != 6) {
            Toast.makeText(this, "Vui lòng nhập mã OTP gồm 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("otp", otp);

        apiService.verifyOtp(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                android.util.Log.d("OTP_VERIFICATION", "verifyOtp response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> res = response.body();
                    android.util.Log.d("OTP_VERIFICATION", "Response body: " + res.toString());

                    Object successObj = res.get("success");
                    boolean success = successObj != null && Boolean.TRUE.equals(successObj);

                    android.util.Log.d("OTP_VERIFICATION", "success=" + success);

                    if (success) {
                        Toast.makeText(OtpVerificationActivity.this, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show();

                        android.util.Log.d("OTP_VERIFICATION", "Checking context: " + context);

                        if ("register".equals(context)) {
                            android.util.Log.d("OTP_VERIFICATION", "Context=register, calling completeRegistration()");
                            completeRegistration();
                        } else if ("forgot_password".equals(context)) {
                            android.util.Log.d("OTP_VERIFICATION", "Context=forgot_password, navigating to ResetPasswordActivity");
                            android.util.Log.d("OTP_VERIFICATION", "Email: " + email);

                            try {
                                Intent intent = new Intent(OtpVerificationActivity.this, ResetPasswordActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("otp", otp);

                                android.util.Log.d("OTP_VERIFICATION", "Intent created, calling startActivity()");
                                startActivity(intent);

                                android.util.Log.d("OTP_VERIFICATION", "startActivity() succeeded, calling finish()");
                                finish();
                            } catch (Exception e) {
                                android.util.Log.e("OTP_VERIFICATION", "Error navigating to ResetPasswordActivity", e);
                                Toast.makeText(OtpVerificationActivity.this, "Lỗi khi chuyển màn hình: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            android.util.Log.w("OTP_VERIFICATION", "Unknown context: " + context);
                        }
                    } else {
                        android.util.Log.w("OTP_VERIFICATION", "success=false from backend");
                        Toast.makeText(OtpVerificationActivity.this, "Mã OTP không hợp lệ hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    android.util.Log.e("OTP_VERIFICATION", "Response not successful or body is null");
                    Toast.makeText(OtpVerificationActivity.this, "Mã OTP không hợp lệ hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                android.util.Log.e("OTP_VERIFICATION", "verifyOtp request failed", t);
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeRegistration() {
        if (name == null || password == null || phone == null || email == null) {
            Toast.makeText(this, "Không thể hoàn tất đăng ký do thiếu thông tin.", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, String> registerBody = new HashMap<>();
        registerBody.put("name", name);
        registerBody.put("email", email);
        registerBody.put("phone", phone);
        registerBody.put("password", password);

        apiService.finishRegister(registerBody).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OtpVerificationActivity.this, "Đăng ký tài khoản thành công!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errorObj = new JSONObject(response.errorBody().string());
                            String errorMessage = errorObj.optString("message", "Đăng ký thất bại. Vui lòng thử lại.");
                            Toast.makeText(OtpVerificationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(OtpVerificationActivity.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(OtpVerificationActivity.this, "Có lỗi xảy ra khi xử lý phản hồi.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Không thể hoàn tất đăng ký: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resendOtp() {
        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        apiService.sendOtp(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OtpVerificationActivity.this, "Mã OTP mới đã được gửi.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtpVerificationActivity.this, "Không thể gửi lại mã.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(OtpVerificationActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
