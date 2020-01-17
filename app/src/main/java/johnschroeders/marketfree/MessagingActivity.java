package johnschroeders.marketfree;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MessagingActivity extends AppCompatActivity implements MyRecyclerViewForMessages.ItemClickListener, SeeNewMessagesFragment.OnFragmentInteractionListener {

    private ArrayList<String> conversationKeys = new ArrayList<>();
    private final static String TAG = "MessagingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        TextView userName = findViewById(R.id.UserName);
        TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        userName.setText(getIntent().getStringExtra("CustomerKey"));
        customerKey.setText(getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load(getIntent().getStringExtra("Photo")).into(userImage);


        if (savedInstanceState != null) {
            try {

                conversationKeys = savedInstanceState.getStringArrayList("SavedConvos");
                populateAndDisplay(conversationKeys);
                savedInstanceState.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //go out to firestore and retrieve the data
            getConversations(getIntent().getStringExtra("CustomerKey"));
        }




    }

    public void getConversations(String customerKey){

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
                            for (String s: conversationKeys){
                                Log.d(TAG, "converstation keys include: "+ s);
                            }
                        }
                        if(task.isComplete()){
                            populateAndDisplay(conversationKeys);
                        }

                    }
                });
    }


    public void populateAndDisplay(ArrayList<String> convoKeysPassedIn){
        Log.d(TAG, "setting recycler layout and adapter messaging activity");
        RecyclerView recyclerView = findViewById(R.id.seeNewMessagesConversationsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessagingActivity.this));
        RecyclerView.Adapter mAdapter =
                new MyRecyclerViewForMessages(MessagingActivity.this,
                        convoKeysPassedIn);

        recyclerView.setAdapter(mAdapter);


        for(String s: convoKeysPassedIn){
            Log.d(TAG, "recyclerview and adapter successfully created and initialized for key: "+s);
        }


    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("SavedConvos", conversationKeys);
    }


    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
