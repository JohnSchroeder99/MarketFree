package johnschroeders.marketfree;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyRecyclerViewForMessages extends RecyclerView.Adapter<MyRecyclerViewForMessages.ViewHolder> {
    private final static String TAG = "MessagingActivity";
    private ArrayList<Message> messageList;
    private LayoutInflater mInflator;
    private User you;
    private User them;
    Context context;

    //Constructor that was updated to handle them and you in a message dialogue.
    MyRecyclerViewForMessages(Context context, ArrayList<Message> messageListpassedIn,
                              User you, User them) {
        this.mInflator = LayoutInflater.from(context);
        this.messageList = messageListpassedIn;
        this.context = context;
        this.you = you;
        this.them = them;
    }


    @NonNull
    @Override
    public MyRecyclerViewForMessages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        View view = this.mInflator.inflate(R.layout.recylcer_view_item_5, parent, false);
        Log.d(TAG, "Recylcer view for viewing the messages has been set up");
        return new ViewHolder(view);
    }

    // for each item in the list bind the values to the row and display the messages with the
    // pictures.
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewForMessages.ViewHolder holder, int position) {

        if (messageList.get(position).getMessageFromCustomerKey().equals(you.getCustomerKey())) {
            Glide.with(context).asBitmap().
                    load(you.getProfileImageURL()).into(holder.yourImage);
            holder.yourMessage.setVisibility(View.VISIBLE);
            holder.theirMessage.setVisibility(View.INVISIBLE);
            holder.yourMessage.setText(messageList.get(position).getMessageContent());

        } else {
            Glide.with(context).asBitmap().
                    load(them.getProfileImageURL()).into(holder.theirImage);
            holder.yourMessage.setVisibility(View.INVISIBLE);
            holder.theirMessage.setVisibility(View.VISIBLE);
            holder.theirMessage.setText(messageList.get(position).getMessageContent());
        }
    }

    // this is needed in order to display the values in the recylcerview
    @Override
    public int getItemCount() {
        try {
            return this.messageList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    // getting references to all of the items in the recylcerview.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView theirMessage;
        TextView yourMessage;
        ImageView yourImage;
        ImageView theirImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            theirMessage = itemView.findViewById(R.id.seeMessagesTheirMessages);
            yourMessage = itemView.findViewById(R.id.seeMessagesYourMessages);
            yourImage = itemView.findViewById(R.id.seeMessagesYourImageView);
            theirImage = itemView.findViewById(R.id.seeMessagesTheirImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
