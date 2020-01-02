package johnschroeders.marketfree;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;


public class UserLoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mSignInClient;
    static final String TAG = "LoginActivity";


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


        //TODO handle various user login accounts and let them choose which one to login with
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " Logging in and setting up Google sign-in options");
                GoogleSignInOptions options =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                                .build();
                mSignInClient = GoogleSignIn.getClient(getApplicationContext(), options);
                signIn();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
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
                Log.d(TAG, "successful results for account: " + acct.getEmail() + " for " + acct.getDisplayName());
                Intent intent = new Intent(getApplicationContext(), UserMainPageManagePersonalsActivity.class);
                startActivity(intent);

            } else {
                Log.d(TAG, "failed to login" + task.getException());
            }
        }
    }
}





