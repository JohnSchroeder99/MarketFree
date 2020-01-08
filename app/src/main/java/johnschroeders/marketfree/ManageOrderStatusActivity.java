package johnschroeders.marketfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;



public class ManageOrderStatusActivity extends AppCompatActivity implements OrderFragment.OnFragmentInteractionListener {
    static final String TAG = "OrderStatusActivity";
    ArrayList<Order> orders;

    // added just for mock data randomization for order properties
    public static int count = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_status);


        TextView userName = findViewById(R.id.UserName);
        TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        userName.setText( getIntent().getStringExtra("CustomerKey"));
        customerKey.setText( getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load( getIntent().getStringExtra("Photo")).into(userImage);




        //you can use the create mock data method here to load up mock data to firestore
        //createMockData();

        Button manageOrdersBackButton = findViewById(R.id.manageOrdersBackButton);
        manageOrdersBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("SavedTab",1);
                String customerKey =
                        Objects.requireNonNull(getIntent().getStringExtra(
                                "CustomerKey"));
                String userName =   Objects.requireNonNull(getIntent().getStringExtra(
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

        //checking if there are items in the savedInstance because of rotation of the device. If
        // there is then do not go out to firestore to retrieve data.
        if (savedInstanceState != null) {
            try {
                orders = new ArrayList<>();
                orders = savedInstanceState.getParcelableArrayList("SavedOrders");
                populateAdapterAndDisplay();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //go out to firestore and retrieve the data
            getListItems();
        }
    }

    public Order createOrder() {
        //just added for mock data


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

    //Right now goes out to FIrestore and pulls down entire collection of Orders documents,
    // converts them to an Order object and then adds them to the OrdersArraylist
    private void getListItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        orders = new ArrayList<>();
        Log.d(TAG, "Getting all orders from firestore");
        db.collection("Orders").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                orders.add(document.toObject(Order.class));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        populateAdapterAndDisplay();
                    }

                });
    }

    // Create the adapter, load it with the orders arraylist, and pass in the correct activity
    // (ManageOrderStatusAcitivty.this) so the fragment can initialize correctly.
    public void populateAdapterAndDisplay() {


        Log.d(TAG, "setting recycler layout and adapter");
        RecyclerView recyclerView = findViewById(R.id.manageOrdersView);
        recyclerView.setLayoutManager(new LinearLayoutManager(ManageOrderStatusActivity.this));
        RecyclerView.Adapter mAdapter =
                new MyRecylcerViewAdapterForOrdersStatus(ManageOrderStatusActivity.this,
                        orders);
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "recyclerview and adapter successfully created and initialized");
    }

    // implement this method if you want to load up mock data to firestore and then remove it again
    // after enough orders have been uploaded.
    public void createMockData() {
        for (int i = 0; i < 20; i++) {
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Orders").add(createOrder());
        }

    }

    //Not currently using this but it needs to be instantiated to inflate the fragment
    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    // save the order to a bundle and use that information to populate the information on create.
    // If the information is not available then we go out to firestore to retrieve it.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("SavedOrders", orders);
    }
}
