package vn.hcmute.ktqt.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.models.requests.RegisterRequest;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.network.RetrofitClient;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPhone, etPassword;
    private Button btnRegister;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.edtFullName);
        etEmail = findViewById(R.id.edtEmail);
        etPhone = findViewById(R.id.edtPhoneNumber);
        etPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        api = RetrofitClient.getClient(this).create(ApiService.class);

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String u = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String p = etPassword.getText().toString().trim();

        if (u.isEmpty() || p.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterRequest req = new RegisterRequest(u, p, email, phone);
        api.register(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // assume backend returns 200 and sends OTP to phone/email; navigate to OTP screen
                    Intent it = new Intent(RegisterActivity.this, RegisterOtpActivity.class);
                    // if backend returns a userId, pass it via intent extras; for now we'll pass username as identifier
                    it.putExtra("userIdentifier", u);
                    startActivity(it);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Lỗi mạng khi đăng ký", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
