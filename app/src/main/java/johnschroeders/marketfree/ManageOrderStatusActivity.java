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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ManageOrderStatusActivity extends AppCompatActivity implements OrderFragment.OnFragmentInteractionListener {
    static final String TAG = "OrderStatusActivity";

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
        orders.add(createOrder());
        orders.add(createOrder());
        orders.add(createOrder());
        Log.d(TAG, "Orders loaded into arraylist");

        //get collections from firestore and add them
        //TODO find a clean way for using firestore for orders instead of firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Orders").document(createOrder().getOrderID()).set(createOrder());


        Log.d(TAG, "setting recycler layout");
        RecyclerView recyclerView = findViewById(R.id.manageOrdersView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "recycler layout set");
        RecyclerView.Adapter mAdapter = new MyRecylcerViewAdapterForOrdersStatus(this, orders);
        Log.d(TAG, "adapter successfully initialized");
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "adapter successfully created");

        // setting up firebase references to the firebase storage
        final FirebaseDatabase database = FirebaseDatabase.getInstance("https://marketfree-67cb9.firebaseio.com/");
        final DatabaseReference databaseReference = database.getReference();


        // TODO create dynamic orders and push to firestore
        Order order = createOrder();
        databaseReference.child("OrderID").setValue(order.getOrderID());
        databaseReference.child("ProducerKey").setValue(order.getProducerKey());
        databaseReference.child("CustomerKey").setValue(order.getCustomerKey());
        databaseReference.child("CustomerKey").push().setValue(order.getCustomerKey());
        databaseReference.child("ProducerKey").push().setValue(order.getCustomerKey());
        databaseReference.child("OrderID").push().setValue(order.getOrderID());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Log.d(TAG, "getting children from datasnapshot for order: " + d.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Values changed on listener " + databaseError.getDetails() + databaseError
                        + databaseError.getMessage() + databaseError.getCode());
            }
        });
    }

    //TODO create orders from actual input from the user and store them in firestore
    public Order createOrder() {

        Order order1 = new Order();
        order1.setProducerKey("a;asdfklhjasdkljfhaklsjdhflkjashdf;ksjdf");
        order1.setAmountPaid(12.00);
        order1.setOrderID("19283asdfsa74");
        order1.setCustomerKey("new key");
        order1.setDateDelivered(new Date());
        order1.setOrderDescriptionAndQuantity(new HashMap<String, Integer>());
        order1.putOrderDescriptionAndQuantity("nails", 1);
        Log.d(TAG, "Order created: " + order1.getOrderID());
        return order1;
    }

    // TODO need to understand how to use this interface for the fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
