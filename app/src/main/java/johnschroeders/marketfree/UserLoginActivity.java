package johnschroeders.marketfree;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;


public class UserLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mSignInClient;
    static final String TAG = "LoginActivity";
    GoogleSignInOptions googleSignInOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        TextView tempLogin = (EditText) findViewById(R.id.loginActivityEmailAddressInput);
        TextView tempPaSS = (EditText) findViewById(R.id.loginActivityPagepassWordText);

        Button registerButton = findViewById(R.id.loginActivityRegisterButton);
        Button loginButton = findViewById(R.id.loginActivityLoginButton);
        tempPaSS.setText(R.string.passwordText);
        tempLogin.setText(R.string.loginText);

        //setting up the image from the drawable resources.
        ImageView marketFreeIcon = findViewById(R.id.loginActivityImageMarketFreeIcon);
        Drawable myDrawable = this.getResources().getDrawable(R.drawable.bluebutton);
        marketFreeIcon.setImageDrawable(myDrawable);


        // handle login procedure for signing into the device
        //TODO add information from successful signin (UserEmail and name) in to a bundle so it can
        // so the correct customer Key can be applied and properly identify the current user
        // with their associated subscriptions, orders, publishings and profile. Label the
        // bundleKey "CurrentUserMetaData".

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
              /*  Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                startActivity(intent);*/
                signOut();

            }
        });

        //TODO handle various user login accounts and let them choose which one to login with
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
                                    .requestServerAuthCode(getString(R.string.client_ID))                                    .requestProfile()
                                    .build();
                    mSignInClient = GoogleSignIn.getClient(getApplicationContext(), googleSignInOptions);
                    signIn();
                } else {
                    Log.d(TAG,
                            "successfully already signed in before with \n"
                                    + "account email: "
                                    + account.getEmail()
                                    + "\n"
                                    + " with account name:  "
                                    + account.getDisplayName()
                                    + "\n"
                                    + "ID TOKEN:  "
                                    + account.getIdToken()
                                    + "\n"
                                    + "Account:  "
                                    + Objects.requireNonNull(account.getAccount())
                                    + "\n"
                                    + "PHOTO URL:  "
                                    + account.getPhotoUrl()
                                    + "\n"
                                    + "FamilyName:  "
                                    + account.getFamilyName()
                                    + "\n"
                                    + "AUTH CODE:  "
                                    + account.getServerAuthCode()
                                    + "\n"
                                    + "ID:  "
                                    + account.getId());
                    Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                    String customerKey = account.getId();
                    String userName = account.getDisplayName();
                    String photoURI = Objects.requireNonNull(account.getPhotoUrl()).toString();
                    intent.putExtra("CustomerKey", customerKey);
                    intent.putExtra("UserName", userName);
                    intent.putExtra("Photo", Objects.requireNonNull(photoURI));
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
                assert acct != null;


                Log.d(TAG,
                        "successful results for \n"
                                + "account email: "
                                + acct.getEmail()
                                + "\n"
                                + " with account name:  "
                                + acct.getDisplayName()
                                + "\n"
                                + "ID TOKEN:  "
                                + acct.getIdToken()
                                + "\n"
                                + "Account:  "
                                + Objects.requireNonNull(acct.getAccount())
                                + "\n"
                                + "PHOTO URL:  "
                                + acct.getPhotoUrl()
                                + "\n"
                                + "FamilyName:  "
                                + acct.getFamilyName()
                                + "\n"
                                + "AUTH CODE:  "
                                + acct.getServerAuthCode()
                                + "\n"
                                + "ID:  "
                                + acct.getId());

                String customerKey = acct.getId();
                String userName = acct.getDisplayName();
                Uri photoURI = acct.getPhotoUrl();
                Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                intent.putExtra("CustomerKey", customerKey);
                intent.putExtra("UserName", userName);
                intent.putExtra("Photo", Objects.requireNonNull(photoURI).toString());
                startActivity(intent);

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


}





