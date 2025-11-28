package vn.hcmute.ktqt.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("_id")
    public String id;

    public String name;

    @SerializedName("image_url")
    public String imageUrl;
}
