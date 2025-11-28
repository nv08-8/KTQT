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
import vn.hcmute.ktqt.models.Book;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.VH> {
    private final List<Book> items = new ArrayList<>();

    /**
     * Replaces the existing data with a new list of books.
     * @param books The new list of books to display.
     */
    public void setItems(List<Book> books) {
        items.clear();
        if (books != null) {
            items.addAll(books);
        }
        notifyDataSetChanged();
    }

    public void addItems(List<Book> books) {
        int start = items.size();
        items.addAll(books);
        notifyItemRangeInserted(start, books.size());
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
        Book b = items.get(position);
        holder.title.setText(b.title);
        holder.price.setText(String.format("%.2f", b.price));
        Glide.with(holder.thumb.getContext())
                .load(b.thumbnailUrl)
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
