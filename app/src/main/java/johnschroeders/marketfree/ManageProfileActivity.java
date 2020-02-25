package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ManageProfileActivity extends AppCompatActivity {
    static final String TAG = "ProfileActivity";
    String newCustomerKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        final EditText userInput = findViewById(R.id.manageProfileInputKeyField);
        TextView userName = findViewById(R.id.UserName);
        final TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        userName.setText(getIntent().getStringExtra("CustomerKey"));
        customerKey.setText(getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load(getIntent().getStringExtra("Photo")).into(userImage);


        // ManageProfile button referenceing and onclicks for future use
        Button manageProfileSaveButton = findViewById(R.id.manageProfileSaveButton);
        Button manageProfileBackButton = findViewById(R.id.manageProfileBackButton);

        manageProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activitySetupAndStart();
            }
        });

        //TODO need to add a warning for the user before they decide to update to their new key.
        //TODO manage the input to a limited amount of characters
        // TODO add revert options to go back to the default key for the user
        // TODO manage the input to handle errors for if the key already exists and if the user
        //  has not put anything in for the new key
        //TODO handle the errors associated with other parts of the application and standardize
        // user credentials across the application with proper intent extra management
        manageProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastShow("updating the key for the user now");

                if (!userInput.getText().equals("")) {
                    Log.d(TAG, "updating the user key");
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    final CollectionReference collectionReference = rootRef.collection("People");
                    collectionReference.whereEqualTo("customerKey", getIntent().getStringExtra("CustomerKey")).
                            get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    collectionReference.document(document.getId()).update(
                                            "customerKey", userInput.getText().toString());
                                }
                            }
                            if (task.isComplete()) {
                                setCustomerKey(userInput.getText().toString());
                                toastShow("Great! Your new key is ready!");
                                activitySetupAndStart();
                            }
                        }

                    });
                }
            }
        });

    }

    public void activitySetupAndStart() {
        Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.putExtra("SavedTab", 1);
        String customerKey = this.newCustomerKey;
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


}


