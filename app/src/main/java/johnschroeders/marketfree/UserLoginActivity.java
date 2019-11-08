package johnschroeders.marketfree;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


public class UserLoginActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<CredentialRequestResult> {

    Button registerButton = null;
    Button loginButton = null;
    CredentialsClient mCredentialsApiClient;
    CredentialRequest mCredentialRequest;
    private static final int RC_READ = 3;
    private static final int RC_SAVE = 1;
    private static final int RC_HINT = 2;
    private static final int RC_SIGN_IN = 5;
    boolean isResolving;
    private GoogleApiClient mGoogleApiClient = null;


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


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .addApi(Auth.CREDENTIALS_API)
                .enableAutoManage(this, this)
                .build();


        //needed for Android Oreo.
        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();

        mCredentialsApiClient = Credentials.getClient(this, options);
        createCredentialRequest();


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestCredentials();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });
    }


    public void createCredentialRequest() {
        mCredentialRequest = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .setAccountTypes(IdentityProviders.FACEBOOK)
                .setAccountTypes(IdentityProviders.TWITTER)
                .build();
    }

    private void requestCredentials() {
        Auth.CredentialsApi.request(mGoogleApiClient, mCredentialRequest).setResultCallback(this);
    }


    private void onCredentialRetrieved(Credential credential) {
        Log.d("Creds", "Credentials retrieved testing values of creds");
        String accountType = credential.getAccountType();
        if (accountType != null) {
            if (accountType.equals(IdentityProviders.GOOGLE)) {
                Log.d("Creds", "Account type = Google");

                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .setAccountName("test")
                        .build();

                Log.d("Creds", "Created GSO " + gso.getAccount());
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        } else {
            Log.d("Creds", "Account type is null");
        }
    }


    public void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Creds", "onConnected signing in automatically");
        requestCredentials();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
        Log.d("Creds", "Creds on result()");
        Status status = credentialRequestResult.getStatus();
        if (status.isSuccess()) {
            Log.d("Creds", "successfully retrieved creds");
            onCredentialRetrieved(credentialRequestResult.getCredential());
        } else {
            if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                try {
                    Log.d("Creds", "is resolving" + RC_READ);
                    isResolving = true;
                    status.startResolutionForResult(this, RC_READ);


                } catch (IntentSender.SendIntentException e) {
                    Log.d("Creds", e.toString());
                }
            } else {
                Log.d("Creds", "Creds not accepted");
                showHintDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Creds", "onActivityResult()");
        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Log.d("Creds", "onActivityResult good RC Read and Result");
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {

                    onCredentialRetrieved(credential);
                }
            } else {
                Log.d("Creds", "Request failed");
            }
            isResolving = false;
        }

        if (requestCode == RC_HINT) {
            Log.d("Creds", "onActivityResult RC HInt " + RC_HINT);
            if (resultCode == RESULT_OK) {
                Log.d("Creds", "onActivityResult RC Hint result okay");
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if (credential != null) {
                    //  populateLoginFields(credential.getId(), credential.getPassword());
                    onCredentialRetrieved(credential);
                }
            } else {
                showToast("Hint dialog closed");
            }
        }

        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d("Creds", "SAVE: OK");
                showToast("Credentials saved");
            }
        }

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Log.d("Creds", "in activity before sign in attempt");
            try {
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
                Log.d("Creds", "in activity sign in successful");
            } catch (Exception e) {

                Log.d("Creds", "silent sign in fail" + e.getCause() + e.getLocalizedMessage() + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void showHintDialog() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setEmailAddressIdentifierSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();

        PendingIntent intent = mCredentialsApiClient.getHintPickerIntent(hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e("Creds", "Could not start hint picker Intent", e);
        }
    }


}





