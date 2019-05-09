package com.midounoo.midounoo.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.midounoo.midounoo.Adapters.BoissonViewHolder;
import com.midounoo.midounoo.Model.Boisson;
import com.midounoo.midounoo.R;
import com.midounoo.midounoo.Utility.GridSpacingItemDecoration;
import com.midounoo.midounoo.Utility.ItemClickSupport;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BoissonBottomSheet extends BottomSheetDialogFragment {

    private RecyclerView listBoisson;
    private DatabaseReference drinks;
    private FirebaseRecyclerAdapter adapter;
    private DrinkChosen listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        RecyclerView.LayoutManager layoutManager;
        FirebaseDatabase firebaseDatabase;
        //Firebase Init
        firebaseDatabase = FirebaseDatabase.getInstance();
        drinks = firebaseDatabase.getReference("Boissons");

        View v = inflater.inflate(R.layout.boisson_bottom_sheet, container, false);
        listBoisson = v.findViewById(R.id.boisson_view);
        layoutManager = new GridLayoutManager(getActivity(), 3);
        listBoisson.setLayoutManager(layoutManager);
        listBoisson.addItemDecoration(new GridSpacingItemDecoration(3,
                dpToPx(5), true));
        listBoisson.setItemAnimator(new DefaultItemAnimator());

        return v;
    }

    private void loadDrinks() {

        FirebaseRecyclerOptions<Boisson> options =
                new FirebaseRecyclerOptions.Builder<Boisson>()
                        .setQuery(drinks, Boisson.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Boisson, BoissonViewHolder >(options){

            @NonNull
            @Override
            public BoissonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.boissons, parent, false);

                return new BoissonViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final BoissonViewHolder boissonViewHolder,
                                            final int i,
                                            @NonNull final Boisson boisson) {
                boissonViewHolder.boissonTitle.setText(boisson.getDesignation());
                boissonViewHolder.boissonPrice.setText(String.valueOf(boisson.getPrix()));
                try {
                    Glide.with(Objects.requireNonNull(getActivity()))
                            .load(boisson.getEtiquette())
                            .into(boissonViewHolder.imgBoisson);
                } catch (Exception e){
                    e.printStackTrace();
                }

                ItemClickSupport.addTo(listBoisson).setOnItemClickListener(
                    ((recyclerView, position, v) -> {
                        listener.onDrinkChosen((Boisson) adapter.getItem(position),
                                adapter.getRef(position).getKey());
                        dismiss();
                    })
                );
            }
        };
        listBoisson.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDrinks();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

        /**
         * Converting dp to pixel
         */
        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    dp, r.getDisplayMetrics()));
        }

    public interface DrinkChosen{
        void onDrinkChosen(Boisson boisson, String key);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DrinkChosen) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DrinkChosen");
        }
    }
}
