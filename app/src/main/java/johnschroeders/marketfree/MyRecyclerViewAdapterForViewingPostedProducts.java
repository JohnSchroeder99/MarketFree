package johnschroeders.marketfree;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyRecyclerViewAdapterForViewingPostedProducts extends RecyclerView.Adapter<MyRecyclerViewAdapterForViewingPostedProducts.ViewHolder> {
    private final static String TAG = "ViewPostedActivity";
    private LayoutInflater mInflater;
    private ArrayList<Product> productList;
    public Context context;

    MyRecyclerViewAdapterForViewingPostedProducts(Context context, ArrayList<Product> passedInProductList) {
        Log.d(TAG, "Publishing RecylcerView Created ");
        this.mInflater = LayoutInflater.from(context);
        this.productList = passedInProductList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyRecyclerViewAdapterForViewingPostedProducts.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item_3, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapterForViewingPostedProducts.ViewHolder holder, int position) {
        try {
            holder.productID.setText(this.productList.get(position).getProductTitle());
            Uri myUri = Uri.parse(this.productList.get(position).getUri());
            Glide.with(context).asBitmap().
                    load(myUri).into(holder.productImage);
        } catch (Exception e) {
            Log.d(TAG, "Nothing in the item yet");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView productImage;
        TextView productID;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // getting references to the layout items in recycler_view_item_3
            productID = itemView.findViewById(R.id.ProductIDRecycler);
            productImage = itemView.findViewById(R.id.ProductListingIconPopulate);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            Log.d(TAG, "In ONCLICK with ProductID clicked: " +
                    productList.get(this.getAdapterPosition()).getProductID() +
                    " and Product URI" + productList.get(this.getAdapterPosition()).getUri());


            Fragment viewPostedFragment = new ViewPostedProductFragment();
            Product productPublished = productList.get(this.getAdapterPosition());


            //Adding data to bundle to pass on to the fragment class for population.
            Bundle bundle = new Bundle();
            bundle.putParcelable("PassedInProduct", productPublished);
            viewPostedFragment.setArguments(bundle);
            Log.d(TAG, "Loading up the fragment into the container");
            //get reference to calling activity to utilize getsupportfragmentmanager method

            try {
                AppCompatActivity appCompatActivity = (AppCompatActivity) context;
                appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.viewProductActivityFrame,
                        viewPostedFragment).commit();
            } catch (Exception e) {

                Log.d(TAG, "failed to swap containers: " + e.getMessage() + e.getLocalizedMessage());
            }

        }
    }


    @Override
    public int getItemCount() {
        try {
            return this.productList.size();
        } catch (Exception e) {
            return 0;
        }
    }
}
