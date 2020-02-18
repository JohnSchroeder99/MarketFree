package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private User you = new User();
    private User them = new User();
    private EditText editTextForReply;
    private Message message;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Query query;
    private ListenerRegistration listenerRegistration;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view = inflater.inflate(R.layout.fragment_see_new_messages, container, false);
        this.editTextForReply = view.findViewById(R.id.seeNewMessagesEditText);
        Button xButton = view.findViewById(R.id.seeWhatsNewFragmentExitButton);
        Button replyButton = view.findViewById(R.id.seeNewMessagesButton);

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (TextUtils.isEmpty(editTextForReply.getText())) {
                        Toast toast = Toast.makeText(getContext(), "You need to fill out " +
                                        "the information to send a message",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();
                    } else {
                        Log.d(TAG,
                                "Message is being added for the replys: " + editTextForReply.getText());
                        message = new Message();
                        message.setMessageFromCustomerKey(you.getCustomerKey());
                        message.setMessageToCustomerKey(them.getCustomerKey());
                        message.setMessageContent(editTextForReply.getText().toString());
                        editTextForReply.setText(null);
                        String date;
                        SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);
                        date = spf.format(new Date());
                        message.setDateSent(date);
                        Log.d(TAG, "Replying to message and adding to the conversation");
                        addMessageToConversations(message, bundle.getString("ConversationKey"));
                    }
                } catch (Exception e) {
                    Log.d(TAG,
                            "Failed to grab the edit text input field: " + e.getMessage() + Arrays.toString(e.getStackTrace()) + e.getCause() + e.getLocalizedMessage());
                }
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        if (savedInstanceState != null) {
            this.editTextForReply.setText(savedInstanceState.getString("InputText"));
        }
        if (getArguments() != null) {
            bundle = this.getArguments();
            getMessages(bundle.getString("ConversationKey"));

        } else {
            Log.d(TAG, "Nothing was passed in");
        }
        setupListener();
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
                    Log.d(TAG, "Message was added to firestore");
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
        Log.d(TAG, "Getting All the messages for the conversation");
        db.collection("Conversations").document(convoKey).collection("Messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                messages.addAll(Collections.singleton(document.toObject(Message.class)));
                            }
                            try {
                                messages = cleanMessageList(messages);
                            } catch (Exception e) {
                                Log.d(TAG, "Couldnt clean empty set");
                            }

                        }
                        Log.d(TAG, "Trying to add all the messages to the list");
                        if (!Objects.requireNonNull(task.getResult()).isEmpty() && task.isComplete()) {
                            Log.d(TAG, "Messages existed and were added to the list");
                            for (Message message : messages) {
                                try {
                                    if ((!message.getMessageFromCustomerKey().equals("")) &&
                                            (!message.getMessageFromCustomerKey()
                                                    .equals(Objects.requireNonNull(getActivity()).
                                                            getIntent().getStringExtra("CustomerKey")))) {
                                        them.setCustomerKey(message.getMessageFromCustomerKey());
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG,
                                            "Couldnt assign key: " + e.getCause() + e.getMessage() + e.getLocalizedMessage());
                                }
                            }
                            getYourselfAsAUserFirst();
                        } else {
                            Toast toast = Toast.makeText(getContext(), "There are no current " +
                                            "Messages",
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
                                getWhoFromInConversation();
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "Nothing to do here");
                        }

                    }
                });
    }

    // finally find the person that you have been talking with and provide the information to the
    // recylcerview when you populate.

    public void getWhoFromInConversation() {
        Log.d(TAG, "getting the conversation outline to get who you are talking with");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection(
                "ConversationReferences");
        collectionReference.whereEqualTo("conversationKey", bundle.getString("ConversationKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isComplete() && task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, "getting data" + Objects.requireNonNull(documentSnapshot.getData()).toString());
                        if (!Objects.requireNonNull(documentSnapshot.get("productOwnerUniqueKey")).toString()
                                .equals(Objects.requireNonNull(getActivity()).getIntent().getStringExtra("CustomerKey"))) {
                            them.setCustomerKey(Objects.requireNonNull(documentSnapshot.get("productOwnerUniqueKey")).toString());
                        }
                    }
                    getWhoYouAreTalkingWithNext(them.getCustomerKey());
                }
            }
        });
    }

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

    // populate the recycler view with the appropriate user information to make the conversation
    // more readable.
    public void populateAndDisplay() {
        recyclerView =
                Objects.requireNonNull(getActivity()).findViewById(R.id.seeNewMessagesMessagesRecyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter =
                new MyRecyclerViewForMessages(getActivity(),
                        this.messages, this.you, this.them);
        try {
            Collections.sort(this.messages, new Comparator<Message>() {
                public int compare(Message obj1, Message obj2) {
                    return obj1.getDateSent().compareToIgnoreCase(obj2.getDateSent());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Nothing to compare against");
        }
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "recyclerview and adapter successfully created and initialized for messages");
    }

    public void setupListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        query = db.collection("Conversations").document(Objects.requireNonNull(bundle.getString(
                "ConversationKey"))).collection("Messages");
        listenerRegistration = query.addSnapshotListener(MetadataChanges.EXCLUDE,
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {
                        Log.d(TAG,
                                "Registered an event:  " + Objects.requireNonNull(queryDocumentSnapshots).getDocuments().get(0).toString());
                        if (!queryDocumentSnapshots.getMetadata().isFromCache()) {
                            if (!Objects.requireNonNull(queryDocumentSnapshots).getDocumentChanges().isEmpty()) {
                                Log.d(TAG, "Snapshot was not empty");
                                getMessages(bundle.getString("ConversationKey"));
                            }
                        }

                    }
                });
    }

    // a method for cleaning up messages.
    private ArrayList<Message> cleanMessageList(ArrayList<Message> dirtyList) {
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
        listenerRegistration.remove();

    }

    // onclick for the fragment to be able to remove itself from the view.
    private void removeSelf() {
        Log.d(TAG, "Send Message fragment removing");
        try {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().
                    beginTransaction().remove(this).commit();
            Intent intent = new Intent(getContext(), MessagingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            String customerKey =
                    Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                            "CustomerKey"));
            String userName = Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                    "UserName"));
            String photoURI =
                    Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                            "Photo"));
            intent.putExtra("CustomerKey", customerKey);
            intent.putExtra("UserName", userName);
            intent.putExtra("Photo", Objects.requireNonNull(photoURI));
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (TextUtils.isEmpty(editTextForReply.getText().toString())) {
            Log.d(TAG, "text was empty");
        } else {
            outState.putString("InputText", editTextForReply.getText().toString());
            Log.d(TAG, "text was not empty");
        }
    }


}
