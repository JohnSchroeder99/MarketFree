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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SeeWhatsNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SeeWhatsNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeeWhatsNewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    private final static String TAGMessage = "MessagingActivity";

    public SeeWhatsNewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeeWhatsNewFragment.
     */

    public static SeeWhatsNewFragment newInstance(String param1, String param2) {
        SeeWhatsNewFragment fragment = new SeeWhatsNewFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_see_whats_new_fragment_view, container,
                false);
        Button newOrdersButton = view.findViewById(R.id.mainActivitySeeNewOrdersButton);
        Button viewPostedProducts = view.findViewById(R.id.mainActivitySeeNewProductsButton);
        Button newSubscribers = view.findViewById(R.id.mainActivitySeeNewSubscribersButton);
        Button messages = view.findViewById(R.id.mainActivitySeeNewMessagesButton);






        newOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "\"Need to set this up just swipe " +
                                "right for now and deal with those ",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        });

        viewPostedProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ViewPostedProductsActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                String customerKey =
                        Objects.requireNonNull(getActivity()).getIntent().getStringExtra(
                                "CustomerKey");
                String userName =   Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                        "UserName"));
                String photoURI =
                        Objects.requireNonNull(getActivity().getIntent().getStringExtra(
                                "Photo"));
                intent.putExtra("CustomerKey", customerKey);
                intent.putExtra("UserName", userName);
                intent.putExtra("Photo", Objects.requireNonNull(photoURI));
                startActivity(intent);
            }
        });

        newSubscribers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "\"Need to set this up just swipe " +
                                "right for now and deal with those ",
                        Toast.LENGTH_LONG);
                toast.show();
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeeNewMessagesFragment seeNewMessagesFragment = new SeeNewMessagesFragment();
                Bundle bundle = new Bundle();
                bundle.putString("CustomerKey", getActivity().getIntent().getStringExtra(
                        "CustomerKey"));
                seeNewMessagesFragment.setArguments(bundle);
                Log.d(TAGMessage, "Loading up the messaging fragment");

               Objects.requireNonNull(getActivity().getSupportFragmentManager()).beginTransaction().
                       replace(R.id.mainActivityFrame,
                               seeNewMessagesFragment).commit();
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
}
