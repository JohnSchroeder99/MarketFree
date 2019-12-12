package johnschroeders.marketfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class ManageOrderStatusActivity extends AppCompatActivity implements OrderFragment.OnFragmentInteractionListener {
    static final String TAG = "OrderStatusActivity";
    public int count = 0;

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

        Log.d(TAG, "Loading arraylist with orders");
        ArrayList<Order> orders = new ArrayList<>();
        orders.clear();
        orders.add(createOrder());
        orders.add(createOrder());
        orders.add(createOrder());
        orders.add(createOrder());
        orders.add(createOrder());
        orders.add(createOrder());
        Log.d(TAG, "Orders loaded into arraylist");


        //get collections from firestore and add them
        //TODO find a clean way for using firestore for orders instead of firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (Order o: orders){
            db.collection("Orders").add(o);
        }



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

        Order order1 =new Order();
        order1.setOrderID("19283aererterrtdfsa74"+ count++);
        order1.setProducerKey("a;asdfklhjasdkljfhaklsjdhflkjashdf;ksjdf");
        order1.setCustomerKey("new key");
        order1.setAmountPaid(12.00);
        order1.setProductDescription("Nails");
        order1.setProductQuantity(234);
        Date date = new Date();
        date.setTime(234235L);
        order1.setDateCanceled(date);
        order1.setDateDelivered(date);
        order1.setDateOrdered(date);
        order1.setProductID("qosikedujfh3425");
        order1.setOrderStatus("Pending");
        Log.d(TAG, "Order created: " + order1.getOrderID());
        return order1;
    }

    // TODO need to understand how to use this interface for the fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
