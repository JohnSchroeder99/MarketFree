package johnschroeders.marketfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ManageOrderStatusActivity extends AppCompatActivity implements OrderFragment.OnFragmentInteractionListener {
    static final String TAG = "OrderStatusActivity";
    public static int count = 6;
    ArrayList<Order> orders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_status);
        Button manageOrdersBackButton = findViewById(R.id.manageOrdersBackButton);

        manageOrdersBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });

        // Creating a mock list to populate order list and stuff into adapter
        //TODO retrieve actual firestore orders and populate the array list full of orders

        //Retreiving mock data from firestore
        getListItems();


        Log.d(TAG, "setting recycler layout");
        RecyclerView recyclerView = findViewById(R.id.manageOrdersView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "recycler layout set");
        RecyclerView.Adapter mAdapter = new MyRecylcerViewAdapterForOrdersStatus(this, orders);
        Log.d(TAG, "adapter successfully initialized");
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "adapter successfully created");


    }


    //TODO create orders from actual input from the user and store them in firestore
    public Order createOrder() {

        Order order1 = new Order();
        order1.setOrderID("19283aererterrtdfsa74" + count++);
        order1.setProducerKey("a;asdfklhjasdkljfhaklsjdhflkjashdf;ksjdf");
        order1.setCustomerKey("new key" + count++);
        order1.setAmountPaid(12.00);
        order1.setProductDescription("Nails");
        order1.setProductQuantity(234);
        Date date = new Date();
        date.setTime(234235L);
        order1.setDateCanceled(date);
        order1.setDateDelivered(date);
        order1.setDateOrdered(date);
        order1.setProductID("qosikedujfh3425");
        //just moccking out random orders for now
        if (count % 3 == 0) {
            order1.setOrderStatus("Canceled");
            count++;
        } else if (count % 5 == 0) {
            order1.setOrderStatus("Approved");
            count++;
        } else if (count % 7 == 0) {
            order1.setOrderStatus("Complete");
            count++;
        } else if (count % 2 == 0) {
            order1.setOrderStatus("Pending");
            count++;
        } else {
            order1.setOrderStatus("Canceled");
            count++;
        }

        Log.d(TAG,
                "Order created: " + order1.getOrderID() + " with order status " + order1.getOrderStatus() + " with count " +
                        "being " + count);
        return order1;
    }

    private void getListItems() {
    //TODO finish retrieving objects from firestore and converting correctly.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all orders from firestore");
        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }


    // TODO need to understand how to use this interface for the fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
