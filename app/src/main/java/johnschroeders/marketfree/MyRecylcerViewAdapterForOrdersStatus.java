package johnschroeders.marketfree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecylcerViewAdapterForOrdersStatus extends RecyclerView.Adapter<MyRecylcerViewAdapterForOrdersStatus.ViewHolder> {

    private ArrayList<Order> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    MyRecylcerViewAdapterForOrdersStatus(Context context, ArrayList<Order> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("Manage", "on create view holder recyclerview class");
        View view = mInflater.inflate(R.layout.recycler_view_item_2, parent, false);
        return new ViewHolder(view);
    }


    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("Manage", "on bind myrecylcer");


        holder.myTextView.setText(mData.get(1).getProducerKey());
        holder.statusTextView.setText(R.string.OrderStatusApproved);
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
            statusTextView = itemView.findViewById(R.id.orderStatus);
            myTextView = itemView.findViewById(R.id.OrderStatusName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        //TODO need to return data on click.
        return null;
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        Log.d("Manage", "in on click for recyclerview class");
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}