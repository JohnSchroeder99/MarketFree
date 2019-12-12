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

import com.google.gson.Gson;

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
        OrderFragment fragment = new OrderFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            //Inflating View with proper items from saved bundle
            Log.d(TAG, "before bundle grab " + getArguments());

            Bundle bundle = this.getArguments();
            if (bundle != null) {
                tempOrder = bundle.getParcelable("OrderClicked");
            }



                Log.d(TAG, "Temp order ID = "+tempOrder.getOrderID());




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
        TextView orderID = view.findViewById(R.id.orderIDResult);
        orderID.setText(tempOrder.getOrderID());
        try {
            TextView producerKey = getView().findViewById(R.id.productIDResult);
            producerKey.setText(tempOrder.getProducerKey());
            TextView customerKey = getView().findViewById(R.id.customerKeyResult);
            customerKey.setText(tempOrder.getCustomerKey());
            TextView productID = getView().findViewById(R.id.productIDResult);
            productID.setText(tempOrder.getProductID());
            TextView orderdescription = getView().findViewById(R.id.orderDescriptionResult);
            orderdescription.setText(tempOrder.getProductDescription());
            TextView orderQUantity = getView().findViewById(R.id.orderQuantityResult);
            orderQUantity.setText(tempOrder.getProductQuantity());
            TextView dateOrder = getView().findViewById(R.id.dateOrderedResult);
            dateOrder.setText(tempOrder.getDateOrdered().toString());
            TextView dateCompleted = getView().findViewById(R.id.dateCompletedResult);
            dateCompleted.setText(tempOrder.getDateDelivered().toString());
            TextView dateCanceled = getView().findViewById(R.id.dateCanceledResult);
            dateCanceled.setText(tempOrder.getDateCanceled().toString());
            TextView amountPaid = getView().findViewById(R.id.amountPaidResult);
            amountPaid.setText(String.valueOf(tempOrder.getAmountPaid()));
            TextView orderStatus = getView().findViewById(R.id.orderStatusResult);
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
