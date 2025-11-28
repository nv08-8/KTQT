package vn.hcmute.ktqt.ui.categories;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hcmute.ktqt.R;
import vn.hcmute.ktqt.adapters.CategoryAdapter;
import vn.hcmute.ktqt.models.Category;

public class CategoriesFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private OnCategoryClickListener mListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryClickListener) {
            mListener = (OnCategoryClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCategories = view.findViewById(R.id.rvCategories);

        setupRecyclerView();
        loadCategories();
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryAdapter(category -> {
            if (mListener != null) {
                mListener.onCategoryClick(category);
            }
        });
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        // Mock data for categories as API is not available
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("1", "Beef"));
        categories.add(new Category("2", "Chicken"));
        categories.add(new Category("3", "Dessert"));
        categories.add(new Category("4", "Drink"));
        categories.add(new Category("5", "Pork"));
        // Add more mock categories as needed

        categoryAdapter.setItems(categories);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
