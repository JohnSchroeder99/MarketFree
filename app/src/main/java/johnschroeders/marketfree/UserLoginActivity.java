package johnschroeders.marketfree;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;


public class UserLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mSignInClient;
    static final String TAG = "LoginActivity";
    GoogleSignInOptions googleSignInOptions;
    Intent intent;
    User user = new User();
    private boolean found = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        intent = new Intent(getApplicationContext(),
                UserMainPageManagePersonalsActivity.class);


        Button loginButton = findViewById(R.id.loginActivityLoginButton);
        Button signInOtherAccountButton =
                findViewById(R.id.loginActivityLoginWithOtherAccountButton);

        //setting up the image from the drawable resources.
        ImageView marketFreeIcon = findViewById(R.id.loginActivityImageMarketFreeIcon);
        Drawable myDrawable = this.getResources().getDrawable(R.drawable.bluebutton);
        marketFreeIcon.setImageDrawable(myDrawable);

        signInOtherAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                signOut();
                googleSignInOptions =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .requestId()
                                .requestIdToken(getString(R.string.client_ID))
                                .requestServerAuthCode(getString(R.string.client_ID))
                                .requestProfile()
                                .build();
                mSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                signIn();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " Logging in and setting up Google sign-in options");
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (account == null) {
                    googleSignInOptions =
                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .requestId()
                                    .requestIdToken(getString(R.string.client_ID))
                                    .requestServerAuthCode(getString(R.string.client_ID)).requestProfile()
                                    .build();
                    mSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                    signIn();
                } else {
                    // if user has already signed in then just proceed
                    signInCreds(account);
                    setupForNextPage(account);
                    startActivity(intent);
                }
            }
        });
    }

    private void signIn() {
        // Launches the sign in flow, the result is returned in onActivityResult
        Intent intent = mSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                // Sign in succeeded, proceed with account
                GoogleSignInAccount acct = task.getResult();

                signInCreds(Objects.requireNonNull(acct));
                setupForNextPage(acct);
                checkIfUserExistsAlready(user.getCustomerKey());

            } else {
                Log.d(TAG, "failed to login" + task.getException());
            }
        }
    }

    private void revokeAccess() {

        googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestId()
                        .requestIdToken(getString(R.string.client_ID))
                        .requestProfile()
                        .build();
        mSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Removed information from the applicaiton ");
                    }
                });
    }

    // used to sign out of the application. So far it does nothing but it will need to go back to
    // the orginal starting point of the application.
    private void signOut() {
        googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestId()
                        .requestIdToken(getString(R.string.client_ID))
                        .requestProfile()
                        .build();
        mSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        mSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Signed out from the application");
                    }
                });
    }


    public void checkIfUserExistsAlready(final String custKey) {
        boolean exists = false;
        FirebaseFirestore firestoreDatabase = FirebaseFirestore.getInstance();
        firestoreDatabase.collection("People")
                .whereEqualTo("customerKey", custKey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (document.getData().containsValue(custKey)) {
                                    findOutIfFound(true);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        if (!this.found) {
            Log.d(TAG,
                    "User was not found creating the user and adding to firestore for:  " + user.getUserName());
            addPersonToFireStore(user);
        } else {
            Log.d(TAG, "User found for: " + user.getUserName());
            startActivity(intent);
        }
    }

    public void addPersonToFireStore(final User newUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d(TAG, "adding User to firestore collection");
        db.collection("People").add(newUser)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Log.d(TAG,
                                "User was added to firestore " + newUser.getUserName());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,
                        "Failed to publish the product with error: " + e.getLocalizedMessage() + e.getMessage() + e.getCause());
            }
        });
    }

    public void signInCreds(GoogleSignInAccount gsa) {
        Log.d(TAG,
                "successful results for \n"
                        + "account email: "
                        + gsa.getEmail() + "\n" + " with account name:  " + gsa.getDisplayName()
                        + "\n" + "ID TOKEN:  " + gsa.getIdToken() + "\n" + "Account:  " + Objects.requireNonNull(gsa.getAccount())
                        + "\n" + "PHOTO URL:  " + gsa.getPhotoUrl() + "\n" + "FamilyName:  " + gsa.getFamilyName() + "\n"
                        + "AUTH CODE:  " + gsa.getServerAuthCode() + "\n" + "ID:  " + gsa.getId());

    }

    public void findOutIfFound(boolean foundPassin) {
        this.found = foundPassin;
    }

    public void setupForNextPage(GoogleSignInAccount account) {

        //pull down the information for the user
        String customerKey = account.getId();
        String userName = account.getDisplayName();
        String photoURI = Objects.requireNonNull(account.getPhotoUrl()).toString();

        //once pulled down then put in the extras
        intent.putExtra("CustomerKey", customerKey);
        intent.putExtra("UserName", userName);
        intent.putExtra("Photo", Objects.requireNonNull(photoURI));

        //setting up the information for the user. this will be done if the user has never logged
        // in.
        this.user.setCustomerKey(customerKey);
        this.user.setUserName(userName);
        this.user.setProfileImageURL(Objects.requireNonNull(photoURI));

    }
}





