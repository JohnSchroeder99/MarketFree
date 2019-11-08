package johnschroeders.marketfree;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class RegisterActivity extends AppCompatActivity {
    Button registerButton = null;
    Button registerBackButton = null;
    CredentialsClient mCredentialsApiClient;
    private static final int RC_SAVE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d("Reg", "at Register activity on create");

        registerButton = findViewById(R.id.RegisterButton);
        registerBackButton = findViewById(R.id.RegisterBackButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Credential credential = new Credential.Builder("TestEmail")
                        .setPassword("TestPass")
                        .build();
                saveCredentials(credential);
                Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                startActivity(intent);
            }
        });

        registerBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
                startActivity(intent);
            }
        });

    }


    public void saveCredentials(Credential credential) {
        mCredentialsApiClient.save(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Creds", "SAVE: OK");
                    showToast("Credentials saved");
                    return;
                }

                Exception e = task.getException();
                if (e instanceof ResolvableApiException) {
                    // Try to resolve the save request. This will prompt the user if
                    // the credential is new.
                    ResolvableApiException rae = (ResolvableApiException) e;
                    try {
                        rae.startResolutionForResult(RegisterActivity.this, RC_SAVE);
                    } catch (IntentSender.SendIntentException f) {
                        // Could not resolve the request
                        Log.e("Creds", "Failed to send resolution.", f);
                        showToast("Saved failed");
                    }
                } else {
                    // Request has no resolution
                    showToast("Saved failed");
                }
            }
        });

    }

    public void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}

