package vn.hcmute.ktqt.models;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("_id")
    public String id;
    public String name;
    @SerializedName("image_url")
    public String iconUrl;

    public Category() {
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
