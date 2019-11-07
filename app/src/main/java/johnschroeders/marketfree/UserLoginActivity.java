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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class UserLoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<CredentialRequestResult> {

    Button registerButton = null;
    Button loginButton = null;

    CredentialsClient mCredentialsApiClient;
    CredentialRequest mCredentialRequest;
    public static final String TAG = "API123";
    private static final int RC_READ = 3;
    private static final int RC_SAVE = 1;
    private static final int RC_HINT = 2;
    boolean isResolving;
    private GoogleApiClient mGoogleApiClient = null;
    private TextView tempLogin = null;
    private TextView tempPaSS = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        tempLogin = (EditText)findViewById(R.id.loginPageEmailAddress);
        tempPaSS = (EditText)findViewById(R.id.loginPagepassWordText);

        registerButton = findViewById(R.id.loginPageregisterButton);
        loginButton = findViewById(R.id.loginPageLoginButton);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
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
                tempLogin.setText(R.string.loginText);
                tempPaSS.setText(R.string.passwordText);
                Credential credential = new Credential.Builder(tempLogin.getText().toString())
                        .setPassword(tempPaSS.getText().toString())
                        .build();
                saveCredentials(credential);

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
                .build();
    }

    private void requestCredentials() {
        Auth.CredentialsApi.request(mGoogleApiClient, mCredentialRequest).setResultCallback(this);
    }


    private void onCredentialRetrieved(Credential credential) {
        String accountType = credential.getAccountType();
        if (accountType == null) {
            // Sign the user in with information from the Credential.
            gotoNext();
        } else if (accountType.equals(IdentityProviders.GOOGLE)) {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
            Task<GoogleSignInAccount> task = signInClient.silentSignIn();

            task.addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()) {
                        Log.d("Creds", "Retrieve successful");
                        Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                        startActivity(intent);
                    } else {
                        if (task.getException() != null) {
                            Log.d("Creds", "Retrieve not successful" + task.getException().getMessage());
                        }
                        showToast("Unable to do a google sign in");
                    }
                }
            });
        }
    }


    public void gotoNext() {
        startActivity(new Intent(this, UserMainPageActivity.class));
        finish();
    }


    public void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Creds", "onConnected");
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

        Status status = credentialRequestResult.getStatus();
        if (status.isSuccess()) {
            onCredentialRetrieved(credentialRequestResult.getCredential());
        } else {
            if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED) {
                try {
                    isResolving = true;
                    status.startResolutionForResult(this, RC_READ);
                } catch (IntentSender.SendIntentException e) {
                    Log.d("Creds", e.toString());
                }
            } else {

                showHintDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult");
        if (requestCode == RC_READ) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if(credential!=null){

                    onCredentialRetrieved(credential);
                }
            } else {
                Log.d(TAG, "Request failed");
            }
            isResolving = false;
        }

        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                if(credential!=null){
                    populateLoginFields(credential.getId(), credential.getPassword());
                }
            } else {
                showToast("Hint dialog closed");
            }
        }

        if (requestCode == RC_SAVE) {
            if (resultCode == RESULT_OK) {
                Log.d("Creds", "SAVE: OK");
                gotoNext();
                showToast("Credentials saved");
            }
        }


    }

    public void populateLoginFields(String email, String password) {
        tempLogin.setText(email);
        tempPaSS.setText(password);
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

    public void saveCredentials(Credential credential) {


        mCredentialsApiClient.save(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "SAVE: OK");
                    showToast("Credentials saved");
                    return;
                }

                Exception e = task.getException();
                if (e instanceof ResolvableApiException) {
                    // Try to resolve the save request. This will prompt the user if
                    // the credential is new.
                    ResolvableApiException rae = (ResolvableApiException) e;
                    try {
                        rae.startResolutionForResult(UserLoginActivity.this, RC_SAVE);
                    } catch (IntentSender.SendIntentException f) {
                        // Could not resolve the request
                        Log.e(TAG, "Failed to send resolution.", f);
                        showToast("Saved failed");
                    }
                } else {
                    // Request has no resolution
                    showToast("Saved failed");
                }
            }
        });

    }

}





