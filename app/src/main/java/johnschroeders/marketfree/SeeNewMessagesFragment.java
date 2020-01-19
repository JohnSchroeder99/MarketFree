package johnschroeders.marketfree;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SeeNewMessagesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "MessagingActivity";
    Bundle bundle;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Message> messages;
    User you;
    User them;
    String theirKey;

    public SeeNewMessagesFragment() {
        // Required empty public constructor
    }

    public static SeeNewMessagesFragment newInstance(String param1, String param2) {
        SeeNewMessagesFragment fragment = new SeeNewMessagesFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //Inflating View with proper items from saved bundle
            Log.d(TAG, "before bundle grab in messages fragment " + getArguments());
            bundle = this.getArguments();
            if (bundle != null) {
                getMessages(bundle.getString("ConversationKey"));

            } else {
                Log.d(TAG, "There is no new conversations to be found for this user");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see_new_messages, container, false);
        Button xButton = view.findViewById(R.id.seeWhatsNewFragmentExitButton);
        Button replyButton = view.findViewById(R.id.seeNewMessagesButton);
        final TextView editTextForReply = view.findViewById(R.id.seeNewMessagesEditTextReply);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setMessageFromCustomerKey(you.getCustomerKey());
                message.setMessageToCustomerKey(them.getCustomerKey());
                message.setMessageContent(editTextForReply.getText().toString());

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Log.d(TAG, "Replying to message and adding to the conversation");
                db.collection("Conversations").document(Objects.requireNonNull(bundle.getString("ConversationKey"))).
                        collection("Messages").add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        getMessages(bundle.getString("ConversationKey"));
                    }
                });
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    // get all the messages that are involved in the conversation. Then use that message to see
    // who it was that wrote it and add it as the users to handle things accordingly with the
    // recylcerview.
    public void getMessages(String convoKey) {
        messages = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting conversations from each conversation that you have started");
        db.collection("Conversations").document(convoKey).collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                messages.addAll(Collections.singleton(document.toObject(Message.class)));
                            }
                        }
                        if (!Objects.requireNonNull(task.getResult()).isEmpty() && task.isComplete()) {
                            Log.d(TAG, "Messages existed and were added to the list");
                            for (Message message : messages) {
                                try {
                                    if (!message.getMessageToCustomerKey().equals(getActivity().getIntent().getStringExtra("CustomerKey"))) {
                                        theirKey = message.getMessageFromCustomerKey();
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG,
                                            "Couldnt assign key: " + e.getCause() + e.getMessage() + e.getLocalizedMessage());
                                }
                            }
                            getYourselfAsAUserFirst();
                        } else {
                            Toast toast = Toast.makeText(getContext(), "There are no current " +
                                            "Conversations",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                });
    }

    //once the messages have been found then add yourself as a user and find the information from
    // the person that you were talking with.
    public void getYourselfAsAUserFirst() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Adding yourself to the list of users");
        db.collection("People").whereEqualTo("customerKey",
                Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                you = document.toObject(User.class);
                            }
                        }
                        if (task.isComplete()) {
                            for (Message message : messages) {
                                if (!message.getMessageFromCustomerKey().equals(you.getCustomerKey())) {
                                    Log.d(TAG, "new key was set:  " + message.getMessageFromCustomerKey());
                                    theirKey = message.getMessageFromCustomerKey();
                                }
                            }
                            getWhoYouAreTalkingWithNext(theirKey);
                        }
                    }
                });
    }


    // finally find the person that you have been talking with and provide the information to the
    // recylcerview when you populate.
    public void getWhoYouAreTalkingWithNext(String userKeyPassedIN) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "adding who you are talking to to the list of people");
        db.collection("People").whereEqualTo("customerKey",
                Objects.requireNonNull(userKeyPassedIN))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                them = document.toObject(User.class);
                            }
                        }
                        if (task.isComplete()) {
                            populateAndDisplay();
                        }
                    }
                });
    }

    // onclick for the fragment to be able to remove itself from the view.
    private void removeSelf() {
        Log.d(TAG, "Send Message fragment removing");
        try {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                    beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }

    // populate the recycler view with the appropriate user information to make the conversation
    // more readable.
    public void populateAndDisplay() {
        Log.d(TAG, "setting recycler layout and adapter for see new Messages fragmentRecyclerview");
        try {
            RecyclerView recyclerView =
                    Objects.requireNonNull(getView()).findViewById(R.id.seeNewMessagesMessagesRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            RecyclerView.Adapter mAdapter =
                    new MyRecyclerViewForMessages(getActivity(),
                            this.messages, you, them);
            recyclerView.setAdapter(mAdapter);
            Log.d(TAG, "recyclerview and adapter successfully created and initialized for messages");
        } catch (Exception e) {
            Log.d(TAG, "Failed to initialize the recylcerview and to create the adapter");
        }
    }

}
