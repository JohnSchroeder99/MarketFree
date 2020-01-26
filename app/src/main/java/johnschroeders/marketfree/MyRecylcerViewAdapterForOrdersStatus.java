package johnschroeders.marketfree;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class MyRecylcerViewAdapterForOrdersStatus extends RecyclerView.Adapter<MyRecylcerViewAdapterForOrdersStatus.ViewHolder> {

    private ArrayList<Order> passedInArrayList;
    private LayoutInflater mInflater;
    User currentUser;
    private Context context;

    private static final String TAG = "OrderStatusActivity";

    // data is passed into the constructor
    MyRecylcerViewAdapterForOrdersStatus(Context context, ArrayList<Order> data, User user) {
        Log.d(TAG, "RecyclerviewApapter Created ");
        this.mInflater = LayoutInflater.from(context);
        this.passedInArrayList = data;
        this.currentUser = user;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item_2, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row for each member in the passedInArraylist member
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.orderIDpopulate.setText(this.passedInArrayList.get(position).getOrderTitle());

        Glide.with(context).asBitmap().
                load(this.passedInArrayList.get(position).getProductURI()).into(holder.productImage);

        // setting up which color to use depending on the order status in mdata.
        try {

            switch (this.passedInArrayList.get(position).getOrderStatus()) {
                case "Pending":
                    holder.orderStatusImage.
                            setCompoundDrawables(holder.imageYellow, null, null, null);
                    break;
                case "Approved":
                    holder.orderStatusImage.
                            setCompoundDrawables(holder.imageBlue, null, null, null);
                    break;
                case "Canceled":
                    holder.orderStatusImage.
                            setCompoundDrawables(holder.imageRed, null, null, null);
                    break;
                case "Complete":
                    holder.orderStatusImage.
                            setCompoundDrawables(holder.imageGreen, null, null, null);
                    break;
            }

        } catch (Exception e) {
            holder.orderStatusImage.
                    setCompoundDrawables(holder.imageYellow, null, null, null);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        try {
            return this.passedInArrayList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView orderIDpopulate;
        TextView orderStatusImage;
        ImageView productImage;

        Drawable imageYellow;
        Drawable imageGreen;
        Drawable imageBlue;
        Drawable imageRed;

        // Individually creates the rows and then ties up the onclick listener to all of the rows
        // after all of them have been created and resources have been referenced
        ViewHolder(View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.orderStatusPopulateProductImage);

            orderIDpopulate = itemView.findViewById(R.id.OrderIDPopulate);
            orderStatusImage = itemView.findViewById(R.id.OrderStatusIconPopulate);

            imageYellow = itemView.getContext().getResources().getDrawable(R.drawable.yellowbutton);
            int h = imageYellow.getIntrinsicWidth();
            int w = imageYellow.getIntrinsicWidth();
            imageYellow.setBounds(1, 1, w, h);

            imageGreen = itemView.getContext().getResources().getDrawable(R.drawable.greenbutton);
            int he = imageGreen.getIntrinsicWidth();
            int wi = imageGreen.getIntrinsicWidth();
            imageGreen.setBounds(1, 1, he, wi);

            imageBlue = itemView.getContext().getResources().getDrawable(R.drawable.bluebutton);
            int hei = imageBlue.getIntrinsicWidth();
            int wid = imageBlue.getIntrinsicWidth();
            imageBlue.setBounds(1, 1, hei, wid);

            imageRed = itemView.getContext().getResources().getDrawable(R.drawable.redbutton);
            int heigh = imageRed.getIntrinsicWidth();
            int widt = imageRed.getIntrinsicWidth();
            imageRed.setBounds(1, 1, heigh, widt);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // create the fragment and display it according to the order that was clicked.
            Fragment orderFragment = new OrderFragment();
            Order order = passedInArrayList.get(this.getAdapterPosition());
            Log.d(TAG, "In ONCLICK with Order ID clicked: " +
                    passedInArrayList.get(this.getAdapterPosition()).getOrderID() +
                    " and order ID" + order.getOrderID());

            //Adding data to bundle to pass on to the fragment class for population.
            Bundle bundle = new Bundle();
            bundle.putParcelable("OrderClicked", order);
            bundle.putParcelable("User", currentUser);
            orderFragment.setArguments(bundle);


            //get reference to calling activity to utilize getsupportfragmentmanager method
            AppCompatActivity appCompatActivity = (AppCompatActivity) view.getContext();
            appCompatActivity.getSupportFragmentManager().beginTransaction().add(R.id.manageOrdersOrderStatusFrame,
                    orderFragment).commit();
            Log.d(TAG, "after order fragment inflation");
        }
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}