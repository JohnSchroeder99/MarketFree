package johnschroeders.marketfree;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SeeNewMessagesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "MessagingActivity";
    Bundle bundle;
    private OnFragmentInteractionListener mListener;
    private ArrayList<Message> messages = new ArrayList<>();
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

    //TODO create a listener for real time updating of the messages.
    // TODO fix screen rotation, probably just need to add arguments to onsaveInstance
    //TODO Fix the message sizing for a message in the recycler view to handle big messages
    // TODO fix the edit text field to remove all the messages once it has been sent and to
    //  restore the keyboard back to hidden.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see_new_messages, container, false);
        Button xButton = view.findViewById(R.id.seeWhatsNewFragmentExitButton);
        Button replyButton = view.findViewById(R.id.seeNewMessagesButton);
        final TextView editTextForReply = view.findViewById(R.id.seeNewMessagesEditTextReply);

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Edit text value is " + editTextForReply.getText());
                if (TextUtils.isEmpty(editTextForReply.getText())) {
                    Log.d(TAG,
                            "Nothing was in the message for the reply: " + editTextForReply.getText());
                    Toast toast = Toast.makeText(getContext(), "You need to fill out " +
                                    "the information to send a message",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    Log.d(TAG,
                            "Message is being added for the replys: " + editTextForReply.getText());
                    Message message = new Message();
                    message.setMessageFromCustomerKey(you.getCustomerKey());
                    message.setMessageToCustomerKey(them.getCustomerKey());
                    message.setMessageContent(editTextForReply.getText().toString());
                    String date;
                    SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);
                    date = spf.format(new Date());
                    message.setDateSent(date);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Log.d(TAG, "Replying to message and adding to the conversation");
                    addMessageToConversations(message, bundle.getString("ConversationKey"));
                }

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

    //adding the message to the conversations collection then verify that the user has the
    // conversation added to their user profile.
    private void addMessageToConversations(final Message message, final String conversationKey) {
        Log.d(TAG, "Adding to the users conversation keys to firestore");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Conversations");
        collectionReference.document(conversationKey).collection("Messages")
                .add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful() && task.isComplete()) {
                    Log.d(TAG, "Message was updated correcty");
                    getMessages(conversationKey);
                } else {
                    Toast toast = Toast.makeText(getContext(), "Message failed to send try again",
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }


    // get all the messages that are involved in the conversation. Then use that message to see
    // who it was that wrote it and add it as "you" to help with the recylcerview
    public void getMessages(String convoKey) {
        messages.clear();
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
                            messages = cleanMessageList(messages);
                        }
                        if (!Objects.requireNonNull(task.getResult()).isEmpty() && task.isComplete()) {
                            Log.d(TAG, "Messages existed and were added to the list");
                            for (Message message : messages) {
                                try {
                                    if ((!message.getMessageFromCustomerKey().equals("")) && (!message.getMessageFromCustomerKey()
                                            .equals(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey")))) {
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
                        try {
                            if (task.isComplete()) {
                                for (Message message : messages) {
                                    if (!message.getMessageFromCustomerKey().equals(you.getCustomerKey())) {
                                        Log.d(TAG, "new key was set:  " + message.getMessageFromCustomerKey());
                                        theirKey = message.getMessageFromCustomerKey();
                                    }
                                }
                                getWhoYouAreTalkingWithNext(theirKey);
                            }
                        } catch (Exception e) {

                            populateAndDisplay();
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
            try {
                Collections.sort(this.messages, new Comparator<Message>() {
                    public int compare(Message obj1, Message obj2) {
                        return obj1.getDateSent().compareToIgnoreCase(obj2.getDateSent());
                    }
                });
            } catch (Exception e) {
                Log.d(TAG, "Nothing to compare against");
            }

            recyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
            recyclerView.setAdapter(mAdapter);
            Log.d(TAG, "recyclerview and adapter successfully created and initialized for messages");
        } catch (Exception e) {
            Log.d(TAG, "Failed to initialize the recylcerview and to create the adapter");
        }
    }

    public ArrayList<Message> cleanMessageList(ArrayList<Message> dirtyList) {
        ArrayList<Message> cleanList = new ArrayList<>();
        for (Message s : dirtyList) {
            if (!s.getMessageContent().equals("")) {
                cleanList.add(s);
            }
        }
        return cleanList;
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


}
