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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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


        // TODO manage the input to handle errors for if the key already exists
        //TODO remove conversation references and cancel all associated orders with reason
        // ("User has migrated to new key").
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
                                                "customerKey", editText);
                                    }
                                }
                                if (task.isComplete()) {
                                    setCustomerKey(editText);
                                    toastShow("Great! Your new key is ready!");
                                    activitySetupAndStart();
                                }
                            }
                        });
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ManageProfileActivity.this);
        builder.setMessage("Are you sure you want to change your key?").setPositiveButton("Yes",
                dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

}


