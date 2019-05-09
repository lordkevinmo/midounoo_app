package com.midounoo.midounoo.Fragment;


import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.Adapters.CategoryViewHolder;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Model.Category;
import com.midounoo.midounoo.Model.Food;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.GridSpacingItemDecoration;
import com.midounoo.midounoo.Utility.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private DatabaseReference category, foodList;
    private RecyclerView listSearch;
    private FirebaseRecyclerAdapter adapter, searchAdapter;
    private ShimmerFrameLayout shimmerFrameLayout;
    private SearchView searchMenu;

    List<String> suggestList = new ArrayList<>();


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RecyclerView.LayoutManager layoutManager;
        FirebaseDatabase firebaseDatabase;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Firebase Init
        firebaseDatabase = FirebaseDatabase.getInstance();
        category = firebaseDatabase.getReference("Category");
        foodList = firebaseDatabase.getReference("Food");

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();
        listSearch = view.findViewById(R.id.listsearch);
        searchMenu = view.findViewById(R.id.app_searchView);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        listSearch.setLayoutManager(layoutManager);
        listSearch.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        listSearch.setItemAnimator(new DefaultItemAnimator());


        if (CommonClass.isConnectedToInternet(Objects.requireNonNull(getActivity())))
            loadCategory();
        else
            Toast.makeText(getActivity(), R.string.connection, Toast.LENGTH_SHORT).show();
        //loadSuggest();


        return view;
    }

    private void loadSuggest() {
        FirebaseRecyclerOptions<Food> options1 =
                new FirebaseRecyclerOptions.Builder<Food>()
                        .setQuery(foodList.limitToLast(6), Food.class)
                        .build();
    }

    private void loadCategory() {

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(category, Category.class)
                        .build();


         adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.search_item_view, parent, false);

                return new CategoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder categoryViewHolder, int i,
                                            @NonNull Category category) {
                categoryViewHolder.textView.setText(category.getName());
                try {
                    Glide.with(Objects.requireNonNull(getActivity()))
                            .load(category.getUrl()).into(categoryViewHolder.imageView);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ItemClickSupport.addTo(listSearch).setOnItemClickListener(
                    ((recyclerView, position, v) -> {
                        CategoryDetailsFragment cdFragment = new CategoryDetailsFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString("CategoryId", adapter.getRef(
                                position).getKey());
                        cdFragment.setArguments(arguments);
                        final FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                .beginTransaction();
                        transaction.replace(R.id.homeFragment, cdFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    })
                );
            }
        };

        listSearch.setAdapter(adapter);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }


    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }
}


