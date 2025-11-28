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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
    private final List<Category> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    public CategoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Category> categories) {
        items.clear();
        if (categories != null) items.addAll(categories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category c = items.get(position);
        holder.name.setText(c.name);
        // You can set a placeholder image or use Glide to load image from a URL if your Category model has an image field
        // For now, I'll just use a placeholder
        Glide.with(holder.itemView.getContext())
                .load(R.drawable.ic_launcher_background) // Replace with your actual image logic
                .into(holder.image);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(c));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public VH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivCategory);
            name = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}

