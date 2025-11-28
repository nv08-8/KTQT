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
import vn.hcmute.ktqt.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
    private final List<Product> items = new ArrayList<>();

    /**
     * Replaces the existing data with a new list of products.
     * @param products The new list of products to display.
     */
    public void setItems(List<Product> products) {
        items.clear();
        if (products != null) {
            items.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<Product> products) {
        int start = items.size();
        items.addAll(products);
        notifyItemRangeInserted(start, products.size());
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = items.get(position);
        holder.title.setText(p.title);
        holder.price.setText(String.format("%.2f", p.price));
        Glide.with(holder.thumb.getContext())
                .load(p.thumbnailUrl)
                .centerCrop()
                .into(holder.thumb);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title;
        TextView price;

        public VH(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.ivProductImage);
            title = itemView.findViewById(R.id.tvProductTitle);
            price = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
