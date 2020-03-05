package johnschroeders.marketfree;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

//TODO add functionality for the user to move forward with payments after an order has been
// accepted.
//TODO see what to do about orders once they are accepted. One way might be to have no canceling
// orders once they are accepted except by the owner of the product.
//TODO we need to handle canceling the order for the product owner after they have accepted the
// order so new keys can be generated
public class OrderFragment extends Fragment{
    private static final String TAG = "OrderStatusActivity";
    private Bundle bundle;
    private Order tempOrder = new Order();
    private User user = new User();
    private OnFragmentInteractionListener mListener;

    public OrderFragment() {
        // Required empty public constructor
    }

    private static OrderFragment newInstance(String param1, String param2) {
        return new OrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //Inflating View with proper items from saved bundle
            Log.d(TAG, "before bundle grab " + getArguments());
            bundle = this.getArguments();
            if (bundle != null) {
                tempOrder = bundle.getParcelable("OrderClicked");
                user = bundle.getParcelable("User");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        Button fragmentXbutton = view.findViewById(R.id.exitOrderFragmentButton);
        Button fragmentCancelOrderButton = view.findViewById(R.id.CancelButtonForOrderFrag);
        Button acceptOrderButton = view.findViewById(R.id.orderAcceptButton);
        acceptOrderButton.setVisibility(View.INVISIBLE);
        fragmentCancelOrderButton.setVisibility(View.INVISIBLE);

        if (tempOrder.getOrderStatus().equals("Pending")) {
            fragmentCancelOrderButton.setVisibility(View.VISIBLE);
            if (bundle.getBoolean("YourOrders")) {
                acceptOrderButton.setVisibility(View.VISIBLE);
                acceptOrderButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptOrder();
                    }
                });
            }

            fragmentCancelOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new CancelReasonFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Order", tempOrder);
                    bundle.putParcelable("User", user);
                    fragment.setArguments(bundle);

                    AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
                    Objects.requireNonNull(appCompatActivity).getSupportFragmentManager().beginTransaction().replace(R.id.manageOrdersOrderStatusFrame,
                            fragment).commit();



                    Log.d(TAG, " new order status set for order: " + tempOrder.getOrderID());
                }
            });
        }

        fragmentXbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });

        //TODO gravitate towards the center for the textviews if setting some and not all of the
        // values
        if (tempOrder.getOrderStatus().equals("Canceled")) {
            hideEverything(view);
        } else {
            showEverything(view);
        }


        return view;
    }

    private void acceptOrder() {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Orders");
        Locale current = getResources().getConfiguration().locale;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
        final Date date = new Date();
        dateFormat.format(date);

        collectionReference.whereEqualTo("orderID", tempOrder.getOrderID()).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Map<Object, Object> map = new HashMap<>();
                        map.put("orderStatus", "Approved");
                        map.put("dateCanceled", tempOrder.getDateCanceled());
                        collectionReference.document(document.getId()).set(map,
                                SetOptions.merge());
                    }
                    removeSelfAndPopulate();
                }
            }

        });
        Log.d(TAG, " new order status set for order: " + tempOrder.getOrderID());


    }


    // allow the fragment to remove itself from the view
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

    // hide everything but the details for being canceled
    private void hideEverything(View view) {
        try {
            //TODO optimize this crap
            ArrayList<TextView> textViews = new ArrayList<>();
            TextView canceledReason = view.findViewById(R.id.orderCancelReasonInput);
            canceledReason.setText(tempOrder.getCancelReason());
            TextView dateCanceled = view.findViewById(R.id.dateCanceledResult);
            dateCanceled.setText(String.valueOf(tempOrder.getDateCanceled()));

            TextView orderID = view.findViewById(R.id.orderIDResult);
            TextView orderIDLabel = view.findViewById(R.id.orderidplaceholder);

            TextView producerKeyLabel = view.findViewById(R.id.producerkeyplaceholder);
            TextView producerKey = view.findViewById(R.id.producerKeyResult);

            TextView dateorderedLabel = view.findViewById(R.id.dateOrderedplaceholder);
            TextView dateOrderResult = view.findViewById(R.id.dateOrderedResult);

            TextView dateCompletedLabel = view.findViewById(R.id.dateCompletedplaceholder);
            TextView dateCompleteResult = view.findViewById(R.id.dateCompletedResult);

            TextView customerKey = view.findViewById(R.id.customerKeyplaceholder);
            TextView customerKeylabel = view.findViewById(R.id.customerKeyResult);

            TextView productIDLabel = view.findViewById(R.id.prodcutIDplaceholder);
            TextView productIDInput = view.findViewById(R.id.productIDResult);

            TextView oderDescLabel = view.findViewById(R.id.orderdescriptionplaceholder);
            TextView oderDescRes = view.findViewById(R.id.orderDescriptionResult);

            TextView orderQuantityLabel = view.findViewById(R.id.orderQuantityplaceholder);
            TextView orderQuantRest = view.findViewById(R.id.orderQuantityResult);

            TextView amountPaidLabel = view.findViewById(R.id.amountPaidplaceholder);
            TextView amountPaidResult = view.findViewById(R.id.amountPaidResult);

            TextView orderStatusResult = view.findViewById(R.id.orderStatusResult);
            orderStatusResult.setText(tempOrder.getOrderStatus());

            textViews.add(oderDescLabel);
            textViews.add(oderDescRes);


            textViews.add(orderQuantityLabel);
            textViews.add(orderQuantRest);

            textViews.add(amountPaidLabel);
            textViews.add(amountPaidResult);

            textViews.add(customerKey);
            textViews.add(customerKeylabel);

            textViews.add(producerKeyLabel);
            textViews.add(producerKey);

            textViews.add(productIDInput);
            textViews.add(productIDLabel);

            textViews.add(orderID);
            textViews.add(orderIDLabel);

            textViews.add(dateCompletedLabel);
            textViews.add(dateCompleteResult);

            textViews.add(dateOrderResult);
            textViews.add(dateorderedLabel);

            for (TextView textView : textViews) {
                textView.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
            Log.d(TAG, "some items were null in the Order tab"+ e.getCause()+ e.getLocalizedMessage());
        }

    }

    // show everything for the order since it has not been canceled yet
    private void showEverything(View view) {
        try {
            TextView orderID = view.findViewById(R.id.orderIDResult);
            orderID.setText(tempOrder.getOrderID());
            TextView producerKey = view.findViewById(R.id.producerKeyResult);
            producerKey.setText(tempOrder.getProducerKey());
            TextView productID = view.findViewById(R.id.productIDResult);
            productID.setText(tempOrder.getProductID());
            TextView customerKey = view.findViewById(R.id.customerKeyResult);
            customerKey.setText(tempOrder.getCustomerKey());
            TextView orderdescription = view.findViewById(R.id.orderDescriptionResult);
            orderdescription.setText(tempOrder.getProductDescription());
            TextView orderQUantity = view.findViewById(R.id.orderQuantityResult);
            orderQUantity.setText(String.valueOf(tempOrder.getProductQuantity()));
            TextView dateOrder = view.findViewById(R.id.dateOrderedResult);
            dateOrder.setText(tempOrder.getDateOrdered().toString());
            TextView dateCompleted = view.findViewById(R.id.dateCompletedResult);
            dateCompleted.setText(tempOrder.getDateDelivered().toString());
            TextView dateCanceled = view.findViewById(R.id.dateCanceledResult);
            dateCanceled.setText(tempOrder.getDateCanceled().toString());
            TextView amountPaid = view.findViewById(R.id.amountPaidResult);
            amountPaid.setText(String.valueOf(tempOrder.getAmountPaid()));
            TextView orderStatus = view.findViewById(R.id.orderStatusResult);
            orderStatus.setText(tempOrder.getOrderStatus());
            TextView canceledReason = view.findViewById(R.id.orderCancelReasonInput);
            canceledReason.setText(tempOrder.getCancelReason());
        } catch (Exception e) {
            Log.d(TAG, "some items were null in the Order tab"+ e.getCause()+ e.getLocalizedMessage());
        }

    }

    private void removeSelf() {
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
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
