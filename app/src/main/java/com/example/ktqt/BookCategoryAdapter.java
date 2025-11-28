package com.example.ktqt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ktqt.model.BookCategory;

import java.util.List;

public class BookCategoryAdapter extends RecyclerView.Adapter<BookCategoryAdapter.BookCategoryViewHolder> {

    private Context context;
    private List<BookCategory> bookCategoryList;

    public BookCategoryAdapter(Context context, List<BookCategory> bookCategoryList) {
        this.context = context;
        this.bookCategoryList = bookCategoryList;
    }

    @NonNull
    @Override
    public BookCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book_category, parent, false);
        return new BookCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookCategoryViewHolder holder, int position) {
        BookCategory bookCategory = bookCategoryList.get(position);
        holder.tvCategoryName.setText(bookCategory.getName());
        Glide.with(context).load(bookCategory.getImage()).into(holder.ivCategoryImage);
    }

    @Override
    public int getItemCount() {
        return bookCategoryList.size();
    }

    public static class BookCategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public BookCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}