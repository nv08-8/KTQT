//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.ui.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.MainActivity;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.network.RetrofitClient;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.data.SessionManager;
import vn.hcmute.ktqt.ui.auth.ForgotPasswordActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvForgot, tvRegister;
    private ApiService apiService;
    private SessionManager sessionManager;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgot = findViewById(R.id.tvForgot);
        tvRegister = findViewById(R.id.tvRegister);

        String text = getString(R.string.signup_link);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvRegister.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvRegister.setText(Html.fromHtml(text));
        }

        apiService = RetrofitClient.getClient(this).create(ApiService.class);
        sessionManager = new SessionManager(this);

        btnLogin.setOnClickListener(v -> doLogin());
        tvForgot.setOnClickListener(v -> startActivity(new Intent(this, ForgotPasswordActivity.class)));
        tvRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        Log.d("LOGIN", "doLogin() called");
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        // Show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang đăng nhập...\n(Lần đầu có thể chậm do server đang khởi động)");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Show a toast after 15 seconds to let user know we're still waiting
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (progressDialog != null && progressDialog.isShowing()) {
                Toast.makeText(LoginActivity.this, "Server đang khởi động, vui lòng đợi...", Toast.LENGTH_LONG).show();
            }
        }, 15000);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        long startTime = System.currentTimeMillis();
        Log.d("LOGIN", "Request started at: " + startTime);

        apiService.login(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                long elapsed = System.currentTimeMillis() - startTime;
                Log.d("LOGIN", "Response received after " + elapsed + "ms, code=" + response.code());

                dismissProgress();
                btnLogin.setEnabled(true);

                if (!response.isSuccessful() || response.body() == null) {
                    String err = null;
                    try {
                        err = response.errorBody() != null ? response.errorBody().string() : "null";
                    } catch (Exception e) {
                        Log.e("LOGIN", "error reading errorBody", e);
                    }
                    Log.e("LOGIN_ERROR_BODY", String.valueOf(err));
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại (code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> res = response.body();
                Log.d("LOGIN_BODY", String.valueOf(res));

                Object msg = res.get("message");
                if (msg != null && (msg.toString().contains("Đăng nhập thành công") || msg.toString().contains("Login successful"))) {

                    Map<String, Object> user = (Map<String, Object>) res.get("user");
                    // If server returned token at top-level, include it in the user map so SessionManager can persist it
                    if (res.get("token") instanceof String) {
                        user.put("token", (String) res.get("token"));
                    }
                    sessionManager.saveUser(user);

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String responseMessage = msg != null ? msg.toString() : "Sai tài khoản hoặc mật khẩu";
                    Toast.makeText(LoginActivity.this, responseMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                long elapsed = System.currentTimeMillis() - startTime;
                Log.e("LOGIN_ERROR", "Request failed after " + elapsed + "ms: " + t.getMessage(), t);

                dismissProgress();
                btnLogin.setEnabled(true);

                String errorMsg = "Không thể kết nối máy chủ";
                if (t.getMessage() != null) {
                    if (t.getMessage().contains("timeout") || t.getMessage().contains("timed out")) {
                        errorMsg = "Server không phản hồi trong 45s.\n\nĐề xuất:\n1. Mở trình duyệt và truy cập:\nhttps://refreshing-respect-production.up.railway.app/\n2. Đợi trang load xong (đánh thức server)\n3. Quay lại app và thử đăng nhập lại";
                    } else if (t.getMessage().contains("Unable to resolve host")) {
                        errorMsg = "Không thể kết nối: Kiểm tra mạng internet";
                    } else {
                        errorMsg = "Lỗi kết nối: " + t.getMessage();
                    }
                }
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgress();
        super.onDestroy();
    }
}
