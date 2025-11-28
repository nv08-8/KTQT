package tdung.com.ktqt;

public class Book {
    private String title;
    private String price;
    private int imageResId; // ID của ảnh trong drawable

    public Book(String title, String price, int imageResId) {
        this.title = title;
        this.price = price;
        this.imageResId = imageResId;
    }

    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public int getImageResId() { return imageResId; }
}
