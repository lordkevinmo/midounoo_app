package com.midounoo.midounoo.Base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.midounoo.midounoo.Common.CommonClass;
import com.midounoo.midounoo.Db.OrderViewModel;
import com.midounoo.midounoo.Fragment.BoissonBottomSheet;
import com.midounoo.midounoo.Model.Boisson;
import com.midounoo.midounoo.Model.Food;
import com.midounoo.midounoo.Model.Order;
import com.midounoo.midounoo.Model.Rating;
import com.midounoo.midounoo.R;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

public class FoodDetailsActivity extends AppCompatActivity
        implements RatingDialogListener, BoissonBottomSheet.DrinkChosen {

    TextView foodName, foodPrice, foodDescription, total;
    TextView boissonDesignation, boissonPrix;
    TextView btnText, btText;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart, btnRating;
    AppCompatRatingBar ratingBar;
    ImageButton minusBtn, plusBtn;
    ImageButton moinsBtn, pplusBtn;
    private static int nber = 1;
    private static int drinkN = 1;

    String FoodId;
    String restau;
    String cle;

    DatabaseReference food, rate;
    Food foods;
    List<Order> orderList = new ArrayList<>();
    int temoin = 0;

    private OrderViewModel viewModel;
    private MaterialCardView boissonLayout;
    private Boisson mBoisson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Firebase Init
        FirebaseDatabase db;
        db = FirebaseDatabase.getInstance();
        food = db.getReference("Food");
        rate = db.getReference("Rating");

        //View Init
        foodName = findViewById(R.id.food_dname);
        foodPrice = findViewById(R.id.food_dprice);
        foodDescription = findViewById(R.id.food_description);
        foodImage = findViewById(R.id.img_food);
        collapsingToolbarLayout = findViewById(R.id.collapseActionView);
        btnCart = findViewById(R.id.shopButton);
        minusBtn = findViewById(R.id.minusBtn);
        plusBtn = findViewById(R.id.plusBtn);
        moinsBtn = findViewById(R.id.moinsBtn);
        pplusBtn = findViewById(R.id.pplusBtn);
        btText = findViewById(R.id.btText);
        btnText = findViewById(R.id.btnText);
        btnRating = findViewById(R.id.rattingButton);
        ratingBar = findViewById(R.id.ratingBar);
        total = findViewById(R.id.somme);

        boissonDesignation = findViewById(R.id.boisson_name);
        boissonPrix = findViewById(R.id.boisson_prix);
        boissonLayout = findViewById(R.id.boisson_layout);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedAppBar);

        Toolbar bar = findViewById(R.id.foodetail_toolbar);
        setSupportActionBar(bar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        bar.setNavigationOnClickListener(v -> finish());

        viewModel = ViewModelProviders.of(this).get(OrderViewModel.class);

        if (getIntent() != null){
            FoodId = getIntent().getStringExtra("FoodId");
            restau = getIntent().getStringExtra("restau");

        }
        if (!TextUtils.isEmpty(FoodId)){
            getDetailFood(FoodId);
            getRatingFood(FoodId);
        }

        //Behavior of choosing drink
        findViewById(R.id.add_boisson).setOnClickListener(
                (v -> {
                    BoissonBottomSheet sheet = new BoissonBottomSheet();
                    sheet.show(getSupportFragmentManager(), "DRINK");
                })
        );

        //Event trigerred when clicking number button
        minusBtn.setOnClickListener(
            (v -> {
                if (nber == 1) return;
                else
                    nber--;
                btnText.setText(String.valueOf(nber));
                total.setText(String.valueOf(nber * temoin));
            })
        );

        plusBtn.setOnClickListener(
            (v -> {
                nber++;
                btnText.setText(String.valueOf(nber));
                total.setText(String.valueOf(nber * temoin));
            })
        );

        moinsBtn.setOnClickListener(
            (v -> {
                if (drinkN == 1){
                    if (mBoisson != null)
                        boissonPrix.setText(String.
                                valueOf(drinkN * mBoisson.getPrix()));
                    return;
                } else {
                    drinkN--;
                    btText.setText(String.valueOf(drinkN));
                    boissonPrix.setText(String.
                            valueOf(drinkN * mBoisson.getPrix()));
                }
            })
        );

        pplusBtn.setOnClickListener(
            (v -> {
                drinkN++;
                btText.setText(String.valueOf(drinkN));
                boissonPrix.setText(String.
                        valueOf(drinkN * mBoisson.getPrix()));
            })
        );


        //Event triggered when click cart button
        btnCart.setOnClickListener(
                (v -> {
                    if (checkIntegrity()) {
                        Order order = new Order(FoodId, foods.getName(), String.valueOf(nber),
                                foods.getPrice(), foods.getDiscount(), restau);
                        viewModel.insert(order);
                        if (mBoisson != null){
                            Order o = new Order(
                                    cle, mBoisson.getDesignation(), String.valueOf(drinkN),
                                    String.valueOf(mBoisson.getPrix()), "0",
                                    restau);
                            viewModel.insert(o);
                        }
                        CommonClass.numberAddToShop += 1;
                        drinkN = 1;
                        nber = 1;
                        Toast.makeText(FoodDetailsActivity.this,
                                "Ajouter au panier", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("OPEN_PANIER", "panier");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.food_det_layout),
                                getString(R.string.caution),
                                Snackbar.LENGTH_SHORT).show();
                    }
                })
        );
        btnRating.setOnClickListener(
                (v -> showRatingDialog())
        );


    }

    private void getRatingFood(String foodId) {
        Query query = rate.orderByChild("foodId").equalTo(foodId);

        query.addValueEventListener(new ValueEventListener() {
            int count =0, sum = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot nextData : dataSnapshot.getChildren()){
                    Rating item = nextData.getValue(Rating.class);
                    assert item != null;
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }
                CommonClass.rating = count;
                if (count!=0) {
                    float average = (float) sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Soumettre")
                .setNegativeButtonText("Annulé")
                .setNoteDescriptions(Arrays.asList("Très mauvais", "Pas bon",
                        "Assez bien", "Très bien", "Excellent !!!"))
                .setDefaultRating(1)
                .setTitle("Noter cette nourriture")
                .setDescription("Veuillez sélectionner quelques étoiles et donner votre avis")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.colorPrimaryDark)
                .setNoteDescriptionTextColor(R.color.overlayActionBar)
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setDescriptionTextColor(R.color.overlayActionBar)
                .setHint("S'il vous plaît écrivez votre commentaire ici ...")
                .setHintTextColor(R.color.colorPrimaryDark)
                .setCommentTextColor(R.color.overlayActionBar)
                .setCommentBackgroundColor(R.color.colorTask)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(FoodDetailsActivity.this)
                .show();
    }

    private void getDetailFood(String foodId) {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foods = dataSnapshot.getValue(Food.class);

                //Populate view
                Glide.with(getApplicationContext()).load(foods.getImage()).into(foodImage);
                collapsingToolbarLayout.setTitle(foods.getName());
                foodName.setText(foods.getName());
                foodPrice.setText(foods.getPrice());
                foodDescription.setText(foods.getDescription());
                total.setText(foods.getPrice());
                temoin = Integer.valueOf(foods.getPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NonNull String comment) {
        final Rating rating = new Rating(Objects.requireNonNull(
                FirebaseAuth.getInstance().getCurrentUser()).getUid(),
                FoodId, String.valueOf(value), comment);

        rate.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                            //Delete old value
                            rate.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                            //Update Value
                            rate.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rating);
                        } else {
                            rate.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(rating);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private boolean checkIntegrity(){
        AtomicBoolean test = new AtomicBoolean(false);
        if (CommonClass.numberAddToShop == 0) {
            test.set(true);
        } else {
            OrderViewModel model = ViewModelProviders.of(FoodDetailsActivity.this)
                    .get(OrderViewModel.class);
            model.getAllOrders().observe(FoodDetailsActivity.this,
                    (orders -> {
                        orderList = orders;
                        for (Order o : orderList){
                            if (!o.getRestaurantName().equals(restau)){
                                test.set(false);
                                break;
                            } else {
                                test.set(true);
                            }
                        }
                    }));
        }

        return test.get();
    }

    @Override
    public void onDrinkChosen(Boisson boisson, String key) {
        if (boisson != null){
            mBoisson = boisson;
            cle = key;
            boissonLayout.setVisibility(View.VISIBLE);
            boissonDesignation.setText(mBoisson.getDesignation());
            boissonPrix.setText(String.valueOf(mBoisson.getPrix()));
        } else System.out.println("N'est pas passé");
    }
}
