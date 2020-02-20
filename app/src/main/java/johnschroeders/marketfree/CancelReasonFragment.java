package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class CancelReasonFragment extends Fragment {
    Bundle bundle;
    private Order tempOrder = new Order();
    User user = new User();
    static final String TAG = "OrderStatusActivity";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private OnFragmentInteractionListener mListener;

    public CancelReasonFragment() {
        // Required empty public constructor
    }


    public static CancelReasonFragment newInstance(String param1, String param2) {
        CancelReasonFragment fragment = new CancelReasonFragment();
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
            Log.d(TAG, "before bundle grab " + getArguments());
            bundle = this.getArguments();
            if (bundle != null) {
                tempOrder = bundle.getParcelable("Order");
                user = bundle.getParcelable("User");
            }
        }
        //Inflating View with proper items from saved bundle

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_cancel_reason, container, false);
        Button cancelButton = view.findViewById(R.id.cancelReasonCancelButton);
        Button exitButton = view.findViewById(R.id.cancelReasonExitButton);
        final EditText textInput = view.findViewById(R.id.cancelReasonFragmentInput);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textInput.getText().toString().equals("")) {
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    final CollectionReference collectionReference = rootRef.collection("Orders");
                    Locale current = getResources().getConfiguration().locale;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
                    final Date date = new Date();
                    dateFormat.format(date);
                    tempOrder.setDateCanceled(date);
                    collectionReference.whereEqualTo("orderID", tempOrder.getOrderID()).
                            get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Map<Object, Object> map = new HashMap<>();
                                    map.put("orderStatus", "Canceled");
                                    map.put("dateCanceled", tempOrder.getDateCanceled());
                                    map.put("cancelReason", textInput.getText().toString());
                                    collectionReference.document(document.getId()).set(map,
                                            SetOptions.merge());
                                }
                            }
                            if (task.isComplete()) {
                                removeSelfAndPopulate();
                            }
                        }

                    });
                } else {
                    toastShow("Need to input a reason ");
                }


            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
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

    public void removeSelf() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void removeSelfAndPopulate() {
        Log.d(TAG, "Order fragment removing");
        try {
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
            Intent intent = new Intent(getContext(), ManageOrderStatusActivity.class);
            intent.putExtra("CustomerKey", Objects.requireNonNull(user).getCustomerKey());
            intent.putExtra("UserName", Objects.requireNonNull(user).getUserName());
            intent.putExtra("Photo", user.getProfileImageURL());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            intent.putExtra("YourOrders", bundle.getBoolean("YourOrders", false));
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
        }
    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    public void toastShow(String whatToSay) {
        Toast toast = Toast.makeText(getActivity(),
                whatToSay,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    }

}
