package vn.hcmute.ktqt.ui.intro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import vn.hcmute.ktqt.MainActivity;
import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.data.SessionManager;
import vn.hcmute.ktqt.ui.auth.LoginActivity;

public class IntroActivity extends AppCompatActivity {

    private Button btnStart;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btnStart = findViewById(R.id.btnStart);
        session = new SessionManager(this);

        btnStart.setOnClickListener(v -> {
            if (session.isLoggedIn()) {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            }
            finish();
        });
    }
}

