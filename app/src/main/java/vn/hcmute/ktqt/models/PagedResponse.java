//Vo Nguyen Quynh Nhu - 23162074
package vn.hcmute.ktqt.models;

import java.util.List;

public class PagedResponse<T> {
    public List<T> items;
    public int page;
    public int pageSize;
    public int totalPages;
    public int totalItems;
}

