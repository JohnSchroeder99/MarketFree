package johnschroeders.marketfree;

import android.content.Context;
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



    @NonNull
    @Override
    public MyRecyclerViewForMessages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.mInflator.inflate(R.layout.recycler_view_item_4, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewForMessages.ViewHolder holder, int position) {
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

            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.seeNewMessagingActivityFrame,
                    seeNewMessagesFragment).commit();

        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
