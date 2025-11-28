package vn.hcmute.ktqt.ui.auth;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtPhoneNumber, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        String text = getString(R.string.login_link);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvLogin.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvLogin.setText(Html.fromHtml(text));
        }

        apiService = RetrofitClient.getClient(this).create(ApiService.class);

        btnRegister.setOnClickListener(v -> requestOtpForRegistration());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void requestOtpForRegistration() {
        String fullName = edtFullName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);

        apiService.sendOtp(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, OtpVerificationActivity.class);
                    intent.putExtra("name", fullName);
                    intent.putExtra("phone", phoneNumber);
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);
                    intent.putExtra("context", "register");
                    startActivity(intent);
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errorObj = new JSONObject(response.errorBody().string());
                            String errorMessage = errorObj.optString("message", "Không gửi được OTP!");
                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Không gửi được OTP!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Có lỗi xảy ra khi xử lý phản hồi.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                String errorMessage = (t.getMessage() != null) ? t.getMessage() : "Lỗi mạng không xác định";
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + errorMessage, Toast.LENGTH_LONG).show();
                Log.e("RegisterApiError", "onFailure: " + t.toString());
            }
        });
    }
}
