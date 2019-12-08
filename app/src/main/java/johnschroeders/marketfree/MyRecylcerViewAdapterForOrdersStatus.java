package johnschroeders.marketfree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyRecylcerViewAdapterForOrdersStatus extends RecyclerView.Adapter<MyRecylcerViewAdapterForOrdersStatus.ViewHolder> {

    private ArrayList<Order> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private FirebaseFirestore db;
    private Order ordertemp;
    ViewGroup viewgroup;
    Context context;

    static final String TAG = "OrderStatus";

    // data is passed into the constructor
    MyRecylcerViewAdapterForOrdersStatus(Context context, ArrayList<Order> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "on create view holder recyclerview class");
        viewgroup = parent;


        View view = mInflater.inflate(R.layout.recycler_view_item_2, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "setting text values in adapterview for orders");
        //TODO need to set text values to correct data for each order
       // holder.myTextView.setText(mData.get(1).getProducerKey());
       // holder.statusTextView.setText(R.string.OrderStatusApproved);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView statusTextView;

        ViewHolder(View itemView) {
            super(itemView);
            //TODO change these text values to be values of the actual orders
            statusTextView = itemView.findViewById(R.id.orderStatus);
            myTextView = itemView.findViewById(R.id.orderIDResult);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
          //  if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

            //TODO get ID of order that was clicked to return the correct order (adapterPosition
            // fix and change getOrderParameter)
            getOrder("19283asdfsa74");
            Log.d(TAG, "before order fragment inflation");
            //TODO pass on values of onclicked object to properly display fragment
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment myFragment = new OrderFragment();
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
    public Order getOrder(String orderID) {
        Log.d(TAG, "ItemClicked with orderID 19283asdfsa74");
        db = FirebaseFirestore.getInstance();
        Query query = db.collection("Orders").whereEqualTo("Orders", orderID);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Order orderReturned2 = document.toObject(Order.class);
                        copyOutOrder(orderReturned2);
                        Log.d(TAG, document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        return this.ordertemp;
    }

    private void copyOutOrder(Order order) {
        this.ordertemp = order;
    }

}