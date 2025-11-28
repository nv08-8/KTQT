package tdung.com.ktqt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rcvBooks;
    private BookAdapter bookAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcvBooks = findViewById(R.id.rcvBooks);

        // 1. Cài đặt Layout dạng lưới (Grid) 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rcvBooks.setLayoutManager(gridLayoutManager);

        // 2. Tạo dữ liệu giả (Mock Data)
        // Vì chưa có ảnh bìa sách thật, mình dùng tạm ic_launcher_background
        // Khi nào có ảnh, bạn copy vào drawable rồi đổi tên ở đây (ví dụ R.drawable.bia_sach_1)
        List<Book> list = new ArrayList<>();
        list.add(new Book("Đắc Nhân Tâm", "86.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Nhà Giả Kim", "79.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Harry Potter", "250.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Sherlock Holmes", "120.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Mắt Biếc", "110.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Dế Mèn", "50.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Tuổi Trẻ", "90.000đ", R.drawable.ic_launcher_background));
        list.add(new Book("Tắt Đèn", "45.000đ", R.drawable.ic_launcher_background));

        // 3. Đưa dữ liệu vào Adapter
        bookAdapter = new BookAdapter(list);
        rcvBooks.setAdapter(bookAdapter);
    }
}