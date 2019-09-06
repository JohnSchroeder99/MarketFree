package johnschroederregis.marketfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);


        Intent intent  = new Intent(getApplicationContext(), UserLoginActivity.class );
        startActivity(intent);
    }
}
