package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

//recycler view for subscription views
public class MyRecyclerViewAdapterForSubscriptions extends RecyclerView.Adapter<MyRecyclerViewAdapterForSubscriptions.ViewHolder> {

    private ArrayList<User> users;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private static final String TAG = "SubscriptionsActivity";
    private Context context;
    private User currentUser;


    // data is passed into the constructor
    MyRecyclerViewAdapterForSubscriptions(Context context, ArrayList<User> data,
                                          User currentUserPassed
    ) {
        this.mInflater = LayoutInflater.from(context);
        this.users = data;
        this.context = context;
        this.currentUser = currentUserPassed;

    }

    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "on create view holder for subscriptions recyclerview class");
        View view = mInflater.inflate(R.layout.recycler_view_item_1, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG,
                "setting up for user " + users.get(position).getUserName() + users.get(position).getProfileImageURL());

        try {

            // setting up the actions for the recyclerview items if any are populated
            holder.myTextView.setText(users.get(position).getUserName());
            Uri uri = Uri.parse(users.get(position).getProfileImageURL());
            Glide.with(context).asBitmap().
                    load(uri).into(holder.imageView);
            holder.floatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                    final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
                    popup.inflate(R.menu.subscriptions_item_click_menu);
                    popup.show();

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                            final CollectionReference collectionReference = rootRef.collection("People");
                            final Intent intent =
                                    new Intent(context.getApplicationContext(),
                                            ManageSubsciptionsActivity.class);
                            collectionReference.whereEqualTo("customerKey", currentUser.getCustomerKey()).
                                    get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                            collectionReference.document(document.getId()).update(
                                                    "subscribedTo",
                                                    FieldValue.arrayRemove(users.get(position).getCustomerKey()));
                                        }
                                        if(task.isComplete()){
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                            intent.putExtra("CustomerKey",
                                                    currentUser.getCustomerKey());
                                            intent.putExtra("Photo", currentUser.getProfileImageURL());
                                            intent.putExtra("UserName", currentUser.getUserName());
                                            context.startActivity(intent);
                                        }
                                    }

                                    if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                                        Log.d(TAG,
                                                "User was not found for removal" + users.get(position).getCustomerKey());
                                    } else {
                                        //customer was found and was removed.
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                                        intent.putExtra("CustomerKey",
                                                currentUser.getCustomerKey());
                                        intent.putExtra("Photo", currentUser.getProfileImageURL());
                                        intent.putExtra("UserName", currentUser.getUserName());
                                        context.startActivity(intent);
                                    }
                                }

                            });
                            return false;
                        }
                    });
                }
            });
        } catch (Exception e) {
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
        FloatingActionButton floatingButton;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.mangeSubsRecyclerViewUserName);
            imageView = itemView.findViewById(R.id.manageSubsRecyclerViewImage);
            floatingButton = itemView.findViewById(R.id.manageSubscriptionsRemoveButton);

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

    void setClickListener(ItemClickListener itemClickListener) {
        Log.d(TAG, "in on click for recyclerview class");
        this.mClickListener = itemClickListener;
    }


    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}