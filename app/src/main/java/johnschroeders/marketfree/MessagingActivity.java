package johnschroeders.marketfree;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

        // handle screen rotation and first time openings.
        if (savedInstanceState != null) {
            try {
                conversationKeys = savedInstanceState.getStringArrayList("SavedConvos");
                populateAndDisplay();
                savedInstanceState.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //go out to firestore and retrieve the data
            getConversations(getIntent().getStringExtra("CustomerKey"));
        }


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
                            getTrueConversationLilstDetailed(cleanList(conversationKeys));
                        }
                    }
                });


    }

    // go out to firestore again and retrieve the true conversation details.
    public void getTrueConversationLilstDetailed(ArrayList<String> conversationKeysPasedIn){
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
                        }
                        if (task.isComplete()) {
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
            if (!s.equals("")) {
                cleanList.add(s);
            }
        }
        return cleanList;
    }
}
