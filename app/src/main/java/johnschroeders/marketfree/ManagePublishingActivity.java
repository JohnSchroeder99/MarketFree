package johnschroeders.marketfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ManagePublishingActivity extends AppCompatActivity implements CreatePublishingFragment.OnFragmentInteractionListener {
    static final String TAG = "PublishingActivity";
    public static int count = 0;
    public ArrayList<Product> productList;

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

        // Mock Data
        //TODO create and pull actual products from firestore. This will need to reference the
        // correct URL for the images as well to show them to each user who is subscribed and
        // make sure that this data is only populated one time on screen rotation just like the
        // orders activity.
        productList = new ArrayList<>();
        createProducts();

        //Create the recycler view and load up the adapter
        setupTheRecyclerView();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    // mock product object for random creation
    public void createProducts() {
        for (int i = 0; i < 15; i++) {
            Date currentTime = Calendar.getInstance().getTime();
            Product product = new Product();
            product.setCost(1.00 * count);
            product.setCustomerKey("a;lsdkjf" + count);
            product.setDateCreated(currentTime);
            product.setImage(null);
            product.setImageURL(null);
            product.setProductDescription("nails or some crap");
            product.setProductID("askl;djfh" + count);
            product.setQuantity(12);
            productList.add(product);
        }
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
