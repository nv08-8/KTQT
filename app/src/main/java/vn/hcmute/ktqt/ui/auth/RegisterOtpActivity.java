//Vo Nguyen Quynh Nhu - 23162074
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
import vn.hcmute.ktqt.MainActivity;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.data.SessionManager;
import vn.hcmute.ktqt.models.AuthResponse;
import vn.hcmute.ktqt.models.requests.OtpRequest;
import vn.hcmute.ktqt.network.ApiService;
import vn.hcmute.ktqt.network.RetrofitClient;

public class RegisterOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerify;
    private ApiService api;
    private SessionManager session;
    private String userIdentifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerify = findViewById(R.id.btnVerify);

        api = RetrofitClient.getClient(this).create(ApiService.class);
        session = new SessionManager(this);

        userIdentifier = getIntent().getStringExtra("userIdentifier");

        btnVerify.setOnClickListener(v -> doVerify());
    }

    private void doVerify() {
        String otp = etOtp.getText().toString().trim();
        if (otp.length() != 6) {
            Toast.makeText(this, "Nhập mã OTP 6 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        OtpRequest req = new OtpRequest(userIdentifier, otp);
        api.verifyOtp(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    session.saveToken(response.body().accessToken);
                    startActivity(new Intent(RegisterOtpActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegisterOtpActivity.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(RegisterOtpActivity.this, "Lỗi mạng khi xác thực OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

