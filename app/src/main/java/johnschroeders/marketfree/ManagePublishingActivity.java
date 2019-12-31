package johnschroeders.marketfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ManagePublishingActivity extends AppCompatActivity implements CreatePublishingFragment.OnFragmentInteractionListener {
    static final String TAG = "PublishingActivity";
    public static int count = 0;
    public ArrayList<Product> productList= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_publishing);


        // Getting references to the buttons and establishing onclick methods for each of them
        Button addPublishingButton = findViewById(R.id.managePublishingAddnewButton);
        Button removePublishings = findViewById(R.id.managePublishingRemovePublishing);
        Button managePublishingBackButton = findViewById(R.id.managePublishingBackButton);

        addPublishingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment publishingFragement = new CreatePublishingFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.PublishingFrame, publishingFragement);
                ft.commit();
                Log.d(TAG, "after publish fragment inflation");
            }
        });

        //TODO find a clean way of removing any of the products that the publisher has published.
        // perhaps doing so on the recycler view onclick for each item would be good just like
        // the orders and get rid of the button click all together
        removePublishings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "moving to remove publishings fragment", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //just go back to the main page activity
        managePublishingBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });


        //TODO create and pull actual products from each user who you are subscribed too

        // Initiallize this method to create mock data for testing purposes.
        //createProducts();

        //TODO hadndle the screen rotation so it does not repopulate the data from firestore and
        // instead populates from the list that is already there.
        // grab real data and set up view
        getProductList();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    // mock product object for random creation and testing purposes
    public void createProducts() {
        for (int i = 0; i < 15; i++) {
            Date currentTime = Calendar.getInstance().getTime();
            Product product = new Product();
            product.setCost(1.00 * count);
            product.setCustomerKey("a;lsdkjf" + count);
            product.setDateCreated(currentTime);
            product.setUri(null);
            product.setProductDescription("nails or some crap");
            product.setProductID("askl;djfh" + count);
            product.setQuantity(12);
            productList.add(product);
        }
    }

    //Method to grab real data from FIrestore
    public void getProductList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all products from firestore");
        productList = new ArrayList<>();
        db.collection("Publishings").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                productList.add(document.toObject(Product.class));
                            }
                            //Create the recycler view and load up the adapter
                            setupTheRecyclerView();
                        }else{
                            Log.d(TAG, "failed to retrieve data"+ task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,
                                "failed to get documents with error: "+ e.getCause()+ e.getMessage()+e.getLocalizedMessage());
            }
        });

    }

    //sets up the recycler view and adapter with data put into the products list
    public void setupTheRecyclerView() {
        Log.d(TAG, "setting recycler layout and adapter");
        RecyclerView recyclerView = findViewById(R.id.publishingProductRecylcerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ManagePublishingActivity.this));
        RecyclerView.Adapter mAdapter =
                new MyRecyclerViewAdapterForPublishing(ManagePublishingActivity.this,
                        productList);
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "recyclerview and adapter successfully created and initialized");
    }


}
