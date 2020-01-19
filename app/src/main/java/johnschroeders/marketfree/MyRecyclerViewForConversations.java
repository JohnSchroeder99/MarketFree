package johnschroeders.marketfree;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class MyRecyclerViewForConversations extends RecyclerView.Adapter<MyRecyclerViewForConversations.ViewHolder> {
    private final static String TAG = "MessagingActivity";
    ArrayList<Conversation> conversationList;
    private LayoutInflater mInflator;
    Context context;

    MyRecyclerViewForConversations(Context context, ArrayList<Conversation> converstationListsPassedIn) {
        this.mInflator = LayoutInflater.from(context);
        this.conversationList = converstationListsPassedIn;
        this.context = context;
    }

    @NonNull
    @Override
    public MyRecyclerViewForConversations.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.mInflator.inflate(R.layout.recycler_view_item_4, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewForConversations.ViewHolder holder, int position) {

        try {
            Glide.with(Objects.requireNonNull(context)).asBitmap().
                    load(conversationList.get(position).getAssociatedProductImage()).into(holder.conversationImage);
            holder.conversationID.setText(conversationList.get(position).getAssociatedProductTitle());
        } catch (Exception e) {

            Log.d(TAG, "Nothing to populate");
        }
    }


    //this must be set to the size of the array that is passed in
    @Override
    public int getItemCount() {
        try {
            return this.conversationList.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView conversationID;
        ImageView conversationImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            conversationImage = itemView.findViewById(R.id.seeNewConversationsImageView);
            conversationID = itemView.findViewById(R.id.seeNewConversationsText);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d(TAG, "item in line was clicked");
            SeeNewMessagesFragment seeNewMessagesFragment = new SeeNewMessagesFragment();
            String key = conversationList.get(this.getAdapterPosition()).getConversationKey();
            Bundle bundle = new Bundle();
            bundle.putString("ConversationKey", key);
            seeNewMessagesFragment.setArguments(bundle);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.seeNewMessagingActivityFrame,
                    seeNewMessagesFragment).commit();
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
