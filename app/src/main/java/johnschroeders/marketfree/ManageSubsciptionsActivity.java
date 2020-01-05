package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

public class ManageSubsciptionsActivity extends AppCompatActivity {
    private static final String TAG = "SubscriptionsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subsciptions);

        //button referencing for managing subscriptions activity/view/layout
        Button subscriptionsBackButton = findViewById(R.id.manageSubscriptionsBackButton);
        Button addSubScriptionsButton = findViewById(R.id.manageSubscriptionsaddButton);

        TextView userName = findViewById(R.id.UserName);
        TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        userName.setText( getIntent().getStringExtra("CustomerKey"));
        customerKey.setText( getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load( getIntent().getStringExtra("Photo")).into(userImage);

        subscriptionsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                intent.putExtra("SavedTab",1);
                String customerKey =
                        Objects.requireNonNull(getIntent().getStringExtra(
                                "CustomerKey"));
                String userName =   Objects.requireNonNull(getIntent().getStringExtra(
                        "UserName"));
                String photoURI =
                        Objects.requireNonNull(getIntent().getStringExtra(
                                "Photo"));
                intent.putExtra("CustomerKey", customerKey);
                intent.putExtra("UserName", userName);
                intent.putExtra("Photo", Objects.requireNonNull(photoURI));
                startActivity(intent);
            }
        });

        //TODO add functionality to adding subcriptions to firestore
        addSubScriptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Sub added", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        Log.d(TAG, "loading data for subscriptions");
        //TODO need to load true subscriptions for each person from firestore



        //Get the people who you are subscribed too
        ArrayList<String> names = new ArrayList<>();
        names.add("John ");
        names.add("Deborah");
        names.add("Useless");
        names.add("Brandumb");
        names.add("Richard");
        names.add("Idiot");
        Log.d(TAG, "loading data for subscriptions completed successfully");
        //
        Log.d(TAG, "creating recyclerview for the subscriptions view");
        RecyclerView recyclerView = findViewById(R.id.manageSubscriptionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "recyclerview created for subscriptions and recyclerlayout set to " + this);
        RecyclerView.Adapter mAdapter = new MyRecyclerViewAdapterForSubscriptions(this, names);
        Log.d(TAG, "adapter initialized for orders");
        recyclerView.setAdapter(mAdapter);
        Log.d(TAG, "adapter successfully setup");
    }
}
