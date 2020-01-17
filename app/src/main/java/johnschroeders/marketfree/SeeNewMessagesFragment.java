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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeeNewMessagesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeeNewMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeeNewMessagesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static String TAG = "MessagingActivity";
    Bundle bundle;
    private OnFragmentInteractionListener mListener;
    private ArrayList<String> conversationKeys;

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
               getConversations(bundle.getString("CustomerKey"));
            }else{
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


    public void getConversations(String customerKey){
            conversationKeys = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Getting all the people that you are subscribed too");
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
                                populateAndDisplay();
                            }
                            if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                               Log.d(TAG, "Conversations Existed and were added");
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

    public void populateAndDisplay(){
        Log.d(TAG, "setting recycler layout and adapter for see new Messages fragmentRecyclerview");
        RecyclerView recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.seeNewMessagesConversationRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.Adapter mAdapter =
                new MyRecyclerViewForMessages(getActivity(),
                        conversationKeys);
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "recyclerview and adapter successfully created and initialized for messages");

    }


}
