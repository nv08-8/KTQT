//<!-- NGuyen Van Truong 23162108 -->
 //       <!-- Nguyen Thi Dieu Lien 23162051 -->
package vn.hcmute.ktqt.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public CategoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Category> newCategories) {
        categories.clear();
        if (newCategories != null) {
            categories.addAll(newCategories);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        categories.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategoryIcon;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }

        public void bind(final Category category, final OnItemClickListener listener) {
            tvCategoryName.setText(category.name);
            Glide.with(itemView.getContext())
                    .load(category.imageUrl) // Use the new imageUrl field
                    .placeholder(R.drawable.ic_launcher_background) // Optional: show a placeholder
                    .error(R.drawable.ic_launcher_background) // Optional: show an error image
                    .into(ivCategoryIcon);
            itemView.setOnClickListener(v -> listener.onItemClick(category));
        }
    }
}