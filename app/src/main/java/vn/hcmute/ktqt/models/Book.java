package vn.hcmute.ktqt.models;

import com.google.gson.annotations.SerializedName;

public class Book {
    @SerializedName("_id")
    public String id;

    public String title;
    public String description;
    public double price;

    @SerializedName("image_url")
    public String imageUrl;

    public String category;
    public String publisher;
    public int year;
}
