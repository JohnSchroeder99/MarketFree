package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ManageOrderStatusActivity extends AppCompatActivity {


    private Button manageOrdersBackButton = null;
    private RecyclerView recyclerView = null;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_status);
        manageOrdersBackButton = findViewById(R.id.manageOrdersBackButton);

        manageOrdersBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });

        ArrayList<Order> orders = new ArrayList<>();
        orders.add(createOrder());
        orders.add(createOrder());
        orders.add(createOrder());

        Log.d("OrderStatus", "before recyclerlayout set");
        recyclerView = findViewById(R.id.manageOrdersView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("OrderStatus", "recyclerlayout set");
        mAdapter = new MyRecylcerViewAdapterForOrdersStatus(this, orders);
        Log.d("OrderStatus", "adapter initialized");
        recyclerView.setAdapter(mAdapter);
        Log.d("OrderStatus", "adapter made");


    }

    //TODO this will be generated using firebase calls
    public Order createOrder() {
        Order order1 = new Order();
        order1.setProducerKey("a;lksdjfla;ksjdf");
        order1.setAmountPaid(12.00);
        order1.setDateDelivered(new Date());
        order1.setOrderDescriptionAndQuantity(new HashMap<String, Integer>());
        order1.putOrderDescriptionAndQuantity("nails", 1);

        return order1;
    }


}
