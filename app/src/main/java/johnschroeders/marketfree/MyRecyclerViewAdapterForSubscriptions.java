package johnschroeders.marketfree;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

//recycler view for subscription views
public class MyRecyclerViewAdapterForSubscriptions extends RecyclerView.Adapter<MyRecyclerViewAdapterForSubscriptions.ViewHolder> {

    private ArrayList<User> users;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private static final String TAG = "SubscriptionsActivity";
    private Context context;

    // data is passed into the constructor
    MyRecyclerViewAdapterForSubscriptions(Context context, ArrayList<User> data) {
        this.mInflater = LayoutInflater.from(context);
        this.users = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "on create view holder for subscriptions recyclerview class");
        View view = mInflater.inflate(R.layout.recycler_view_item_1, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    //TODO bind data from actual firestore data for subscriptions

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG,
                "setting up for user "+ users.get(position).getUserName()+ users.get(position).getProfileImageURL());

        try{
            holder.myTextView.setText(users.get(position).getUserName());
            Uri uri = Uri.parse(users.get(position).getProfileImageURL());
            Glide.with(context).asBitmap().
                    load( uri).into(holder.imageView);
        }catch (Exception e){
            Toast toast = Toast.makeText(context, "Great! now its time to subscribe to people and" +
                            " see what they can offer you!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        try {
            return this.users.size();
        } catch (Exception e) {
            return 0;
        }
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView imageView;


        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.mangeSubsRecyclerViewUserName);
            imageView = itemView.findViewById(R.id.manageSubsRecyclerViewImage);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }



    // allows clicks events to be caught
    //TODO retrieve actual firestore data when a subscription is clicked for subscription fragment
    void setClickListener(ItemClickListener itemClickListener) {
        Log.d(TAG, "in on click for recyclerview class");
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}