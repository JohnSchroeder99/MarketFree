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


//Recycler View for publishing. Requires a product arraylist ot be passed in for population of
// the itmes in the view.

public class MyRecyclerViewAdapterForPublishing extends RecyclerView.Adapter<MyRecyclerViewAdapterForPublishing.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Product> productList;
    public final static String TAG = "PublishingActivity";
    public Context context;

    MyRecyclerViewAdapterForPublishing(Context context, ArrayList<Product> passedInProductList) {
        Log.d(TAG, "Publishing RecylcerView Created ");
        this.mInflater = LayoutInflater.from(context);
        this.productList = passedInProductList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item_3, parent, false);
        return new ViewHolder(view);
    }

    // sets the text and the image for each of the items.
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapterForPublishing.ViewHolder holder, int position) {

            // setting the text to the passed in product title and the image for each item in the
            // listview from firestore this required using glide which was imported  in
            // the gradle properties file as a dependency
        try{
            holder.productID.setText(this.productList.get(position).getProductTitle());
            Uri myUri = Uri.parse(this.productList.get(position).getUri());
            Glide.with(context).asBitmap().
                    load(myUri).into(holder.productImage);
        }catch (Exception e){
            Log.d(TAG, "Nothing in the item yet");
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
        public void onClick(View view) {
            Log.d(TAG, "In ONCLICK with ProductID clicked: " +
                    productList.get(this.getAdapterPosition()).getProductID() +
                    " and Product URI" +  productList.get(this.getAdapterPosition()).getUri());

            //TODO handle onlcick for each image, probably inflate a fragment that shows a bigger
            // version of the picture with some details and an option to remove the publishing
            Fragment productListViewClickedFragement = new RemovePublishingFragment();
            Product productPublished = productList.get(this.getAdapterPosition());


            //Adding data to bundle to pass on to the fragment class for population.
            Bundle bundle = new Bundle();
            bundle.putParcelable("RemovePublshing", productPublished);
            productListViewClickedFragement.setArguments(bundle);

            //get reference to calling activity to utilize getsupportfragmentmanager method
            AppCompatActivity appCompatActivity = (AppCompatActivity) context;
            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.managePublishingsPublishingFrame,
                    productListViewClickedFragement).commit();
        }
    }


    interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
