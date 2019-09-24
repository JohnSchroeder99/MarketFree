package johnschroederregis.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class UserLoginActivity extends AppCompatActivity {
Button registerButton = null;
Button loginButton = null;
String userlogin = null;
String userPassword = null;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        registerButton  = findViewById(R.id.loginPageregisterButton );
        loginButton = findViewById(R.id.loginPageLoginButton);






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
                TextView tempPaSS =  findViewById(R.id.loginPagepassWordText);
                userPassword = tempPaSS.getText().toString();

                if((userlogin.equals("admin"))&& (userPassword.equals("admin"))){

                    Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                    startActivity(intent);
                }

                else{

                    Toast toast = Toast.makeText(getApplicationContext(), "Incorrect login", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

    }


}
