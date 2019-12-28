package johnschroeders.marketfree;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


//Recycler View for publishing. Requires a product arraylist ot be passed in for population of
// the itmes in the view.

public class MyRecyclerViewAdapterForPublishing extends RecyclerView.Adapter<MyRecyclerViewAdapterForPublishing.ViewHolder> {
    private LayoutInflater mInflater;
    private ArrayList<Product> productList;
    public final static String TAG = "PublishingActivity";
    MyRecyclerViewAdapterForPublishing(Context context, ArrayList<Product> passedInProductList) {
        Log.d(TAG, "Publishing RecylcerView Created ");
        this.mInflater = LayoutInflater.from(context);
        this.productList = passedInProductList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item_3, parent, false);
        return new ViewHolder(view);
    }

    // sets the text and the image for each of the items.
    //TODO this holder will need to set the image according to the URL image that is passed in
    // with each product for display instead of the big blue button. Nothing will be saved to the
    // users phone if possible.
    @Override
    public void onBindViewHolder(@NonNull MyRecyclerViewAdapterForPublishing.ViewHolder holder, int position) {
        Log.d(TAG,"setting text values in adapterview for order "+this.productList.get(position).getProductID());
      holder.productID.setText(this.productList.get(position).getProductID());
      holder.productImage.setCompoundDrawables(holder.imageBlue, null, null, null);
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
        Drawable imageBlue;
        TextView productImage;
        TextView productID;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            productID= itemView.findViewById(R.id.ProductIDRecycler);
            productImage = itemView.findViewById(R.id.ProductListingIconPopulate);

            imageBlue = itemView.getContext().getResources().getDrawable(R.drawable.bluebutton);
            int h = imageBlue.getIntrinsicWidth();
            int w = imageBlue.getIntrinsicWidth();
            imageBlue.setBounds(1, 1, w, h);
        }


        @Override
        public void onClick(View v) {

        }
    }
}
