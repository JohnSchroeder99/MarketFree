package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ViewPostedProductsActivity extends AppCompatActivity {
    private final static String TAG = "ViewPostedActivity";
    public ArrayList<String> usersCustKeyPulledFromFireStore;
    ArrayList<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_posted_products);

        // main setup to get references to texts and to set up the info card for the activity.
        TextView userName = findViewById(R.id.TitleTextView);
        TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView cardImageView = findViewById(R.id.CardImageView);
        Button backButton = findViewById(R.id.viewProductsActivityBackButton);

        userName.setText(getIntent().getStringExtra("CustomerKey"));
        customerKey.setText(getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load(getIntent().getStringExtra("Photo")).into(cardImageView);

        //setting up the onclick listeners for each button in the view
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                String customerKey =
                        Objects.requireNonNull(getIntent().getStringExtra(
                                "CustomerKey"));
                String userName = Objects.requireNonNull(getIntent().getStringExtra(
                        "UserName"));
                String photoURI =
                        Objects.requireNonNull(getIntent().getStringExtra(
                                "Photo"));
                intent.putExtra("CustomerKey", customerKey);
                intent.putExtra("UserName", userName);
                intent.putExtra("Photo", Objects.requireNonNull(photoURI));
                startActivity(intent);
            }
        });


        //check if the user rotated if so then just repopulate instead of going out to firestore
        // again
        if (savedInstanceState != null) {
            try {
                productList = new ArrayList<>();
                productList = savedInstanceState.getParcelableArrayList("SavedProductList");
                setupTheRecyclerView();
                savedInstanceState.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getAllSubedUsers();
        }

    }

    //grabbing all the users that you are subscribed too.
    public void getAllSubedUsers() {
        usersCustKeyPulledFromFireStore = new ArrayList<>();
        Log.d(TAG, "Retrieving all the users that you are subscribed to");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all the people that you are subscribed too");
        db.collection("People")
                .whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                usersCustKeyPulledFromFireStore.addAll(document.toObject(User.class).getSubscribedTo());
                            }
                        }
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                            getAllProdsFromSubedUsers(usersCustKeyPulledFromFireStore);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "You are not " +
                                            "subscribed to anyone to see any products that they " +
                                            "have published yet",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                });
    }


    //grabbing all the products from the users that you are subscribed too

    public void getAllProdsFromSubedUsers(ArrayList<String> usersPulledDown) {
        productList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all products from firestore");
        productList = new ArrayList<>();
        db.collection("Publishings").whereIn("customerKey", usersPulledDown).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                productList.add(document.toObject(Product.class));
                            }
                            //Create the recycler view and load up the adapter
                            setupTheRecyclerView();
                        } else {
                            Log.d(TAG, "failed to retrieve data" + task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,
                        "failed to get documents with error: " + e.getCause() +
                                e.getMessage() + e.getLocalizedMessage());
            }
        });
    }


    //sets up the recycler view and adapter with data put into the products list
    public void setupTheRecyclerView() {
        Log.d(TAG, "setting recycler layout and adapter");
        RecyclerView recyclerView = findViewById(R.id.viewPostedProdsActivityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewPostedProductsActivity.this));
        RecyclerView.Adapter mAdapter =
                new MyRecyclerViewAdapterForPublishing(ViewPostedProductsActivity.this,
                        productList);
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "recyclerview and adapter successfully created and initialized");
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("SavedProductList", productList);
    }

}
