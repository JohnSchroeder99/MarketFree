package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResponse;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class UserLoginActivity extends AppCompatActivity {
    Button registerButton = null;
    Button loginButton = null;
    String userlogin = null;
    String userPassword = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        registerButton = findViewById(R.id.loginPageregisterButton);
        loginButton = findViewById(R.id.loginPageLoginButton);


        //store a credential
        Credential credential = new Credential.Builder("Johnschroedercs@gmail.com")
                .setAccountType(IdentityProviders.GOOGLE)
                .setName("John")
                .build();


        //retreive credentials
        CredentialsClient credentials = Credentials.getClient(this);
        credentials.save(credential).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("Credentials", "SAVE: OK");
                    Toast.makeText(getApplicationContext(), "Credentials saved", Toast.LENGTH_LONG).show();

                } else {
                    Exception e = task.getException();
                    if (e != null) {
                        Log.d("Credentials", "Did not save" + e.getLocalizedMessage() + e.getMessage()
                                + e.getCause());
                        e.printStackTrace();
                    }
                }
            }
        });


        CredentialRequest credentialRequest = new CredentialRequest.Builder().
                setAccountTypes(IdentityProviders.GOOGLE, IdentityProviders.FACEBOOK, IdentityProviders.TWITTER)
                .build();


        credentials.request(credentialRequest).addOnCompleteListener(new OnCompleteListener<CredentialRequestResponse>() {
            @Override
            public void onComplete(@NonNull Task<CredentialRequestResponse> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                    startActivity(intent);

                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Need to register or input credentials", Toast.LENGTH_LONG);
                    toast.show();

                    Exception e = task.getException();
                    if(e != null){
                        e.printStackTrace();
                    }
                }
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView tempLogin = findViewById(R.id.loginPageEmailAddress);
                userlogin = tempLogin.getText().toString();
                TextView tempPaSS = findViewById(R.id.loginPagepassWordText);
                userPassword = tempPaSS.getText().toString();


                if (userPassword.equals("admin")) {

                    Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                    startActivity(intent);
                } else {

                    Toast toast = Toast.makeText(getApplicationContext(), "Incorrect login", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }


}
