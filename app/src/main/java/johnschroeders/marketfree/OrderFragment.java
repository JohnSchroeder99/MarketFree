package johnschroeders.marketfree;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button fragmentXbutton;
    static final String TAG = "OrderStatusActivity";
    private String mParam1;
    private String mParam2;
    Bundle bundle;
    Order tempOrder = new Order();

    private OnFragmentInteractionListener mListener;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: match parameters of fragment class to populate data from adapter bundle
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
            }
            Log.d(TAG,
                    "Temp order ID = " + Objects.requireNonNull(tempOrder).getOrderID() + "Temp Producer Key is = " + tempOrder.getProducerKey());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        fragmentXbutton = view.findViewById(R.id.exitOrderFragmentButton);
        fragmentXbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelf();
            }
        });
        //TODO finish mapping out the rest of this to the order class passed in and erase from
        // the bundle after pushing the X button
        try {
            TextView orderID = view.findViewById(R.id.orderIDResult);
            orderID.setText(tempOrder.getOrderID());
            TextView productKey = view.findViewById(R.id.producerKeyResult);
            productKey.setText(tempOrder.getProductID());
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
        } catch (Exception e) {
            Log.d(TAG, "One of the values are null");
        }


        return view;
    }

    // allow the fragment to remove itself from the view
    private void removeSelf() {
        Log.d(TAG, "Order fragment removing");
        try {
            bundle.clear();
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(this).commit();
        } catch (Exception e) {
            Log.d(TAG, " failed to pop fragment " + e.getMessage() + e.getCause());
            e.printStackTrace();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    //Not exactly sure what the proper way to use this interface is
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
