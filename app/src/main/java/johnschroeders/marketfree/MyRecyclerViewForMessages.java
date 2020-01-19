package johnschroeders.marketfree;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyRecyclerViewForMessages extends RecyclerView.Adapter<MyRecyclerViewForMessages.ViewHolder> {
    private final static String TAG = "MessagingActivity";
    ArrayList<String> conversationList;
    private LayoutInflater mInflator;
    Context context;

    MyRecyclerViewForMessages (Context context, ArrayList<String> converstationListsPassedIn ){
        this.mInflator = LayoutInflater.from(context);
        this.conversationList = converstationListsPassedIn;
        this.context = context;
    }


    //TODO change this to be the the recylcer view for converstations, not messages, create
    // another recylcer view to handle the messages.
    @NonNull
    @Override
    public MyRecyclerViewForMessages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.mInflator.inflate(R.layout.recycler_view_item_4, parent, false);
        return new ViewHolder(view);
    }

    //TODO make the layout for the messages cleaner with the picture of the person who wrote
    // it and a change in sides depending on if it was from you or the other

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewForMessages.ViewHolder holder, int position) {
        //TODO handle if the conversation key is blank
        //TODO handle if conversation key already exists
        //TODO make the layouts for the conversation keys cleaner with picture and the product
        // title

        Log.d(TAG, "Conversation key was added to the list: "+ conversationList.get(position));
            try{
                holder.conversationID.setText(conversationList.get(position));
            }catch (Exception e){

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


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            conversationID = itemView.findViewById(R.id.seeNewMessagesRecyclerviewText);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d(TAG, "item in line was clicked");
            SeeNewMessagesFragment seeNewMessagesFragment = new SeeNewMessagesFragment();
            String key = conversationList.get(this.getAdapterPosition());

           Bundle bundle = new Bundle();
           bundle.putString("ConversationKey",  key );
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
