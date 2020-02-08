package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ManageSubsciptionsActivity extends AppCompatActivity {
    private static final String TAG = "SubscriptionsActivity";
    final ArrayList<String> people = new ArrayList<>();
    ArrayList<User> users = new ArrayList<>();
    User currentUser;

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
        userName.setText(getIntent().getStringExtra("CustomerKey"));
        customerKey.setText(getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load(getIntent().getStringExtra("Photo")).into(userImage);
        final EditText editText = findViewById(R.id.manageSubscriptionskeyInsertTextView);


        Log.d(TAG, "Setting up the subscriptions");
        currentUser = new User();
        currentUser.setCustomerKey(Objects.requireNonNull(getIntent().getStringExtra(
                "CustomerKey")));
        currentUser.setProfileImageURL(Objects.requireNonNull(getIntent().getStringExtra(
                "Photo")));
        currentUser.setUserName(Objects.requireNonNull(getIntent().getStringExtra(
                "UserName")));

        subscriptionsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                String customerKey =
                        Objects.requireNonNull(getIntent().getStringExtra(
                                "CustomerKey"));
                String userName = Objects.requireNonNull(getIntent().getStringExtra(
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


        addSubScriptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if the user is in firestore, and then check if we are already subscribed
                if (editText.getText().toString().equals("")) {

                    Toast toast = Toast.makeText(getApplicationContext(), "No input was provided " +
                                    "to search for",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("People")
                            .whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey"))
                            .whereArrayContains("subscribedTo", editText.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();
                            if (task.isComplete())
                                if (!isEmpty) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "You are " +
                                                    "already subscribed to this user",
                                            Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    toast.show();
                                } else {
                                    checkIfUserExists(editText.getText().toString());
                                }
                        }
                    });
                }
            }
        });

        getListItems();

    }


    private void getListItems() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "Getting all the people that you are subscribed too");
        db.collection("People")
                .whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                people.addAll(document.toObject(User.class).getSubscribedTo());
                            }
                        }
                        if (!Objects.requireNonNull(task.getResult()).isEmpty()) {
                            getUsersFromSubscribedPeople(people);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Its time to " +
                                            "subscribe to people",
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                            toast.show();
                        }
                    }
                });
    }


    public void getUsersFromSubscribedPeople(ArrayList<String> passedInCustomerKeys) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final Query query = db.collection("People").whereIn("customerKey", passedInCustomerKeys);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, "User add to the list " + document.toObject(User.class).getUserName());
                        users.add(document.toObject(User.class));
                    }
                    populateAdapterAndDisplay();
                }
            }

        });

    }


    public void populateAdapterAndDisplay() {
        Log.d(TAG, "creating recyclerview for the subscriptions view");
        RecyclerView recyclerView = findViewById(R.id.manageSubscriptionsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapterForSubscriptions mAdapter =
                new MyRecyclerViewAdapterForSubscriptions(this, users, currentUser);
        Log.d(TAG, "recyclerview created for subscriptions and recyclerlayout set to " + this);
        recyclerView.setAdapter(mAdapter);
    }

    public void checkIfUserExists(final String passedEditText) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("People")
                .whereEqualTo("customerKey", passedEditText)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "Checking document");
                boolean isEmpty = Objects.requireNonNull(task.getResult()).isEmpty();
                if (!isEmpty) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Subscribing to the " +
                                    "user:  " + passedEditText,
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();

                    updateAndAddToSubscription(passedEditText);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "The user does not " +
                                    "exist yet or you put in the wrong customer key",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });
    }


    public void updateAndAddToSubscription(final String passedEditText) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("People");
        collectionReference.whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        collectionReference.document(document.getId()).update("subscribedTo",
                                FieldValue.arrayUnion(passedEditText));
                    }
                    startActivity(getIntent());
                }
            }

        });
    }
}


