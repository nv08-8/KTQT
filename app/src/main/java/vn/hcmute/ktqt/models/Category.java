//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.models;

public class Category {
    public String id;
    public String name;
    public String iconUrl;

    // Constructor mặc định (cần thiết cho một số thư viện như GSON)
    public Category() {
    }

    // Constructor với id và name
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

