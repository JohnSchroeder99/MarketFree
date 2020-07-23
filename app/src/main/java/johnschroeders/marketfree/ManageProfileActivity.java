package johnschroeders.marketfree;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
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

public class ManageProfileActivity extends AppCompatActivity {
    static final String TAG = "ProfileActivity";
    String newCustomerKey = null;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        final EditText userInput = findViewById(R.id.manageProfileInputKeyField);
        TextView userName = findViewById(R.id.UserName);
        final TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        customerKey.setText(getIntent().getStringExtra("CustomerKey"));
        userName.setText(getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load(getIntent().getStringExtra("Photo")).into(userImage);


        // ManageProfile button referenceing and onclicks for future use
        Button manageProfileSaveButton = findViewById(R.id.manageProfileSaveButton);
        Button manageProfileBackButton = findViewById(R.id.manageProfileBackButton);
        progressBar = findViewById(R.id.manageProfileProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        manageProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCustomerKey(getIntent().getStringExtra("CustomerKey"));
                activitySetupAndStart();
            }
        });


        manageProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(userInput.getText().toString())) {
                    warningSetup(userInput.getText().toString());
                } else {
                    toastShow("You need to input a key to save");
                }
            }
        });
    }

    public void activitySetupAndStart() {
        Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("SavedTab", 1);
        String userName = Objects.requireNonNull(getIntent().getStringExtra(
                "UserName"));
        String photoURI =
                Objects.requireNonNull(getIntent().getStringExtra(
                        "Photo"));
        intent.putExtra("CustomerKey", this.newCustomerKey);
        intent.putExtra("UserName", userName);
        intent.putExtra("Photo", Objects.requireNonNull(photoURI));
        startActivity(intent);

    }

    public void toastShow(String whatToSay) {
        Toast toast = Toast.makeText(getApplicationContext(),
                whatToSay,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    }

    public void setCustomerKey(String newCustomerKey) {
        this.newCustomerKey = newCustomerKey;
    }

    public void warningSetup(final String editText) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setIndeterminate(true);
                        checkForKeyExistingAlready(editText);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProfileActivity.this);
        builder.setMessage("WARNING!!! Are you sure you want to change your key? Your " +
                "conversations will be removed and your orders will be automatically canceled").setPositiveButton(
                "Yes",
                dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


    public void checkForKeyExistingAlready(final String editText) {
        Log.d(TAG, "Checking if user exists already");
        FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
        firestoreDatabase.collection("People")
                .whereEqualTo("customerKey", editText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if ((task.isSuccessful() && task.isComplete()) && (!Objects.requireNonNull(task.getResult()).isEmpty())) {
                            toastShow("This customer key is already in use. Please create a new " +
                                    "key");
                            progressBar.setVisibility(View.INVISIBLE);
                        } else if ((task.isSuccessful() && task.isComplete()) && (Objects.requireNonNull(task.getResult()).isEmpty())) {
                            cancelAllAssociatedOrdersThatYouMade(editText);
                        }
                    }
                });
    }


    public void cancelAllAssociatedOrdersThatYouMade(final String editText) {
        Log.d(TAG, "Canceling all associated orders that you made");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Orders");
        Locale current = getResources().getConfiguration().locale;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
        final Date date = new Date();
        dateFormat.format(date);
        collectionReference.whereEqualTo("producerKey", getIntent().getStringExtra("CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (!document.toObject(Order.class).getOrderStatus().equals("Completed")) {
                            Map<Object, Object> map = new HashMap<>();
                            map.put("orderStatus", "Canceled");
                            map.put("dateCanceled", dateFormat.format(date));
                            map.put("cancelReason", "User migrated to new key");
                            collectionReference.document(document.getId()).set(map,
                                    SetOptions.merge());
                        }
                    }
                }
                if (task.isComplete()) {
                    Log.d(TAG, "Canceled all orders that you made");
                    cancelAllAssociatedOrdersMadeToYou(editText);
                }
            }
        });
    }

    public void cancelAllAssociatedOrdersMadeToYou(final String editText) {
        Log.d(TAG, "Canceling all associated orders that were made to you");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Orders");
        Locale current = getResources().getConfiguration().locale;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
        final Date date = new Date();
        dateFormat.format(date);
        collectionReference.whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (!document.toObject(Order.class).getOrderStatus().equals("Completed")) {
                            Map<Object, Object> map = new HashMap<>();
                            map.put("orderStatus", "Canceled");
                            map.put("dateCanceled", dateFormat.format(date));
                            map.put("cancelReason", "User migrated to new key");
                            collectionReference.document(document.getId()).set(map,
                                    SetOptions.merge());
                        }
                    }
                }
                if (task.isComplete()) {
                    Log.d(TAG, "Canceled orders that were made out to you");
                    removeSubscriptions(editText);
                }
            }
        });
    }


    public void removeSubscriptions(final String editText) {
        Log.d(TAG, "Removing subscriptions");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("People");
        collectionReference.whereEqualTo("customerKey", getIntent().getStringExtra(
                "CustomerKey")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, "Found user");
                        for (String s : document.toObject(User.class).getSubscribedTo()) {
                            if (!s.equals("")) {
                                collectionReference.document(document.getId()).update(
                                        "subscribedTo", FieldValue.arrayRemove(s));
                            }
                        }
                    }
                }
                if (task.isComplete()) {
                    Log.d(TAG, "Subscriptions references were removed");
                    removeConversationReferences(editText);
                }
            }
        });
    }


    public void removeConversationReferences(final String editText) {
        Log.d(TAG, "Removing references to conversations");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("People");
        collectionReference.whereEqualTo("customerKey", getIntent().getStringExtra(
                "CustomerKey")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, "Found user");
                        for (String s : document.toObject(User.class).getConversationsKeys()) {
                            Log.d(TAG, "Removing conversation key: " + s);
                            if (!s.equals("")) {
                                collectionReference.document(document.getId()).update(
                                        "conversationsKeys", FieldValue.arrayRemove(s));
                            }
                        }
                    }
                }

                if (task.isComplete()) {
                    Log.d(TAG, "Conversation references were removed");
                    updateUserKey(editText);
                }
            }
        });
    }


    public void updateUserKey(final String editText) {
        Log.d(TAG, "Updating the user key");
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("People");
        collectionReference.whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    collectionReference.document(document.getId()).update(
                            "customerKey", editText);
                }

                if (task.isComplete() && task.isSuccessful()) {
                    setCustomerKey(editText);
                    toastShow("Great! Your new key is setup and history has been removed!");
                    Log.d(TAG, "User key was updated to: " + editText);
                    activitySetupAndStart();
                }
            }
        });
    }





    /*public void checkYourOrders(final String editText) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Orders");
        Locale current = getResources().getConfiguration().locale;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
        final Date date = new Date();
        dateFormat.format(date);
        collectionReference.whereEqualTo("producerKey", getIntent().getStringExtra("CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (document.toObject(Order.class).getOrderStatus().equals("Approved")) {

                        }
                    }
                }
            }
        });
    }

    public void checkOrdersMadeToYou(final String editText) {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        final CollectionReference collectionReference = rootRef.collection("Orders");
        Locale current = getResources().getConfiguration().locale;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", current);
        final Date date = new Date();
        dateFormat.format(date);
        collectionReference.whereEqualTo("producerKey", getIntent().getStringExtra("CustomerKey")).
                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        if (document.toObject(Order.class).getOrderStatus().equals("Approved")) {
                            setMoveOn(false);
                        }
                    }
                }
                if (task.isComplete() && moveOn) {
                    cancelAllAssociatedOrdersMadeToYou(editText);
                } else {
                    toastShow("You still have orders that you have made that are in an " +
                            "accepted state, get the order completed before you can migrate");

                }
            }
        });
    }


    public void setMoveOn(boolean moveOn) {
        this.moveOn = moveOn;
    }
    */
}


