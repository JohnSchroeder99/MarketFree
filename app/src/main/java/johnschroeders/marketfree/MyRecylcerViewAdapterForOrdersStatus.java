package johnschroeders.marketfree;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

public class MyRecylcerViewAdapterForOrdersStatus extends RecyclerView.Adapter<MyRecylcerViewAdapterForOrdersStatus.ViewHolder> {

    private ArrayList<Order> mData;
    private LayoutInflater mInflater;
    private FirebaseFirestore db;
    private Order ordertemp;

    private static final String TAG = "OrderStatusActivity";

    // data is passed into the constructor
    MyRecylcerViewAdapterForOrdersStatus(Context context, ArrayList<Order> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "on create view holder recyclerview class");


        View view = mInflater.inflate(R.layout.recycler_view_item_2, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "setting text values in adapterview for orders");
        // TODO need to fix when the user rotates the device so it doesnt add 3 more items to the
        //  database and need to dynamically change colors based off of the order status.
        for (Order o : this.mData){
            Log.d(TAG, o.getOrderID());
            holder.orderIDpopulate.setText(o.getOrderID());
            if(o.getOrderStatus().equals("Pending")){
                holder.orderStatusImage.
                        setCompoundDrawables(holder.imageYellow, null, null, null);
            }
        }
        // holder.statusTextView.setText(R.string.OrderStatusApproved);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //these members reflect the two fields in the reclerview
        TextView orderIDpopulate;
        TextView orderStatusImage;
        Drawable imageYellow;

        ViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "setting text values viewHolder");
            //Get references to the items in the recyclerview2 layout
            //TODO get drawables for each color or find a way to dynamically change which one to
            // populate based off of order status.

            orderIDpopulate = itemView.findViewById(R.id.OrderIDPopulate);
            orderStatusImage = itemView.findViewById(R.id.OrderStatusIconPopulate);
            imageYellow = itemView.getContext().getResources().getDrawable( R.drawable.yellowbutton);
            int h = imageYellow.getIntrinsicHeight();
            int w = imageYellow.getIntrinsicWidth();
            imageYellow.setBounds( 5, 5, w, h );

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG, "before order fragment inflation");
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new OrderFragment();
            Order order = new Order();
            order.setOrderID(orderIDpopulate.getText().toString());

            //Adding data to bundle to pass on to the fragment class for population.
            Bundle bundle = new Bundle();
            bundle.putParcelable("OrderClicked", order);
            myFragment.setArguments(bundle);

            activity.getSupportFragmentManager().beginTransaction().add(R.id.OrderStatusFrame,
                    myFragment).commit();
            Log.d(TAG, "after order fragment inflation");
        }
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    //Get the order from the position that was clicked that has a populated OrderID
    //TODO need to add more (like a composite key with customerkey and orderID) for clicked order
    private Order getOrder(String orderID) {
        Log.d(TAG, "ItemClicked with orderID 19283aererterrtdfsa74");
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Orders");
        final Query query =collectionReference.whereEqualTo("Orders", orderID );
        Log.d(TAG, "Getting query now");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "task was successful");
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Order orderReturned2 = document.toObject(Order.class);
                        copyOutOrder(orderReturned2);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Task failed "+e.getCause());
            }
        });
        return this.ordertemp;
    }

    private void copyOutOrder(Order order) {

        Log.d(TAG, "Order Copied out to "+new Gson().toJson(order));
        this.ordertemp = order;

    }

}