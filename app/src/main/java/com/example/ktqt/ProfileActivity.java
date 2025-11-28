package com.example.ktqt;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Mục 1: Chờ Xác Nhận
        LinearLayout statusWaitConfirm = findViewById(R.id.statusWaitConfirm);
        ImageView icon1 = statusWaitConfirm.findViewById(R.id.imgOrderIcon);
        TextView label1 = statusWaitConfirm.findViewById(R.id.tvOrderLabel);
        // icon1.setImageResource(R.drawable.ic_wait_confirm); // Thay bằng icon của bạn
        label1.setText("Chờ xác nhận");

        // Mục 2: Chờ Lấy Hàng
        LinearLayout statusWaitPickup = findViewById(R.id.statusWaitPickup);
        ImageView icon2 = statusWaitPickup.findViewById(R.id.imgOrderIcon);
        TextView label2 = statusWaitPickup.findViewById(R.id.tvOrderLabel);
        // icon2.setImageResource(R.drawable.ic_wait_pickup); // Thay bằng icon của bạn
        label2.setText("Chờ lấy hàng");

        // Mục 3: Đang Giao
        LinearLayout statusShipping = findViewById(R.id.statusShipping);
        ImageView icon3 = statusShipping.findViewById(R.id.imgOrderIcon);
        TextView label3 = statusShipping.findViewById(R.id.tvOrderLabel);
        // icon3.setImageResource(R.drawable.ic_shipping); // Thay bằng icon của bạn
        label3.setText("Đang giao");

        // Mục 4: Đánh Giá
        LinearLayout statusRating = findViewById(R.id.statusRating);
        ImageView icon4 = statusRating.findViewById(R.id.imgOrderIcon);
        TextView label4 = statusRating.findViewById(R.id.tvOrderLabel);
        // icon4.setImageResource(R.drawable.ic_rating); // Thay bằng icon của bạn
        label4.setText("Đánh giá");
    }
}
