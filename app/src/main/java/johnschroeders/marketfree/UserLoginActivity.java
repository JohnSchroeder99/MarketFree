package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;


public class UserLoginActivity extends AppCompatActivity {

    private Button registerButton = null;
    private Button loginButton = null;
    private CredentialsClient mCredentialsApiClient;
    private CredentialRequest mCredentialRequest;
    private static final int RC_READ = 3;
    private static final int RC_SAVE = 1;
    private static final int RC_HINT = 2;
    private static final int RC_SIGN_IN = 9001;
    private boolean isResolving;
    private GoogleSignInClient mSignInClient;
    private GoogleSignInAccount acct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        TextView tempLogin = (EditText) findViewById(R.id.loginPageEmailAddress);
        TextView tempPaSS = (EditText) findViewById(R.id.loginPagepassWordText);
        registerButton = findViewById(R.id.loginPageregisterButton);
        loginButton = findViewById(R.id.loginPageLoginButton);
        tempPaSS.setText(R.string.passwordText);
        tempLogin.setText(R.string.loginText);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                acct = task.getResult();
                Log.d("Creds", "successful results for account: "
                        + acct.getEmail() + acct.getDisplayName());
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);

            } else {
                Log.d("Creds", "failed to login" + task.getException());
            }
        }
    }


}





