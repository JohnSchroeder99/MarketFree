package johnschroeders.marketfree;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

// this class will be for presenting the market free icon on startup
public class StartupActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(getApplicationContext(), UserLoginActivity.class);
        startActivity(intent);
    }
}
