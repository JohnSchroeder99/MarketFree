package johnschroeders.marketfree;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

//TODO update conversations list to be able to be removed from the user. This will need to
// be removed from the current users key so it doesnt populate for them. If it is removed then we
// need a way to handle that in firestore if the user decides to go to that same product again
// and ask about that product. One ssolution might be if the conversation still exists then it
// can just be readded to the users conversastion key list.

public class MessagingActivity extends AppCompatActivity implements
        MyRecyclerViewForConversations.ItemClickListener,
        SeeNewMessagesFragment.OnFragmentInteractionListener,
        MyRecyclerViewForMessages.ItemClickListener {

    private ArrayList<String> conversationKeys = new ArrayList<>();
    private final static String TAG = "MessagingActivity";
    private ArrayList<Conversation> conversationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        // getting references to all of the items that we use for the basic layout.
        TextView userName = findViewById(R.id.UserName);
        TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        userName.setText(getIntent().getStringExtra("CustomerKey"));
        customerKey.setText(getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load(getIntent().getStringExtra("Photo")).into(userImage);

        getConversations(getIntent().getStringExtra("CustomerKey"));

    }

    // go out to firestore and grab the conversation that is stored with you.

    public void getConversations(String customerKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all the conversations that you have");
        db.collection("People")
                .whereEqualTo("customerKey", customerKey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                conversationKeys.addAll(document.toObject(User.class).getConversationsKeys());
                            }
                        }
                        if (task.isComplete()) {
                            try {
                                getTrueConversationLilstDetailed(cleanList(conversationKeys));
                            } catch (Exception e) {
                                Toast toast = Toast.makeText(getApplicationContext(), "You do not" +
                                                " have any conversations started yet",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                                Log.d(TAG,
                                        "failed to load messages " + e.getCause() + e.getMessage()+ e.getLocalizedMessage());
                            }

                        }
                    }
                });


    }

    // go out to firestore again and retrieve the true conversation details.
    //TODO This needs to be able to handle more then 10 possibel queries at a time per firestore
    // documentation for  "whereIn" (only 10 items allowed to be pulled in at a time)
    public void getTrueConversationLilstDetailed(ArrayList<String> conversationKeysPasedIn) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all parrelel conversations");
        db.collection("ConversationReferences")
                .whereIn("conversationKey", conversationKeysPasedIn)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                conversationsList.addAll(Collections.singleton(document.toObject(Conversation.class)));
                            }

                        } else if (Objects.requireNonNull(task.getResult()).isEmpty()) {
                            Log.d(TAG, "There were no conversations populated");
                        }
                        if(task.isComplete()&&task.isSuccessful()){
                            populateAndDisplay();
                        }
                    }
                });
    }


    // once the conversations have been grabbed we need populate the recylcerview
    public void populateAndDisplay() {
        Log.d(TAG, "setting recycler layout and adapter messaging activity");
        RecyclerView recyclerView = findViewById(R.id.seeNewMessagesConversationsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessagingActivity.this));
        RecyclerView.Adapter mAdapter =
                new MyRecyclerViewForConversations(MessagingActivity.this,
                        conversationsList);
        recyclerView.setAdapter(mAdapter);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("SavedConvos", conversationsList);
    }

    // must be implemented to work with the recyclerview
    @Override
    public void onItemClick(View view, int position) {

    }

    //must be implemented to work with the fragment
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //method for setting up the arraylist for population so you can add a clean list to the
    // conversation list without nulls or other potential issues.
    public ArrayList<String> cleanList(ArrayList<String> dirtyList) {
        ArrayList<String> cleanList = new ArrayList<>();
        for (String s : dirtyList) {
            if (s.equals("")) {
                cleanList.remove(s);
            }
        }
        return dirtyList;
    }


}
