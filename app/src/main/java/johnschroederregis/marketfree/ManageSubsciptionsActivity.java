package johnschroederregis.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ManageSubsciptionsActivity extends AppCompatActivity {
    Button subscriptionsBackButton = null;
    Button addSubScriptionsButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subsciptions);

        subscriptionsBackButton = findViewById(R.id.manageSubscriptionsBackButton);
        addSubScriptionsButton = findViewById(R.id.manageSubscriptionsaddButton);


        subscriptionsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });


        addSubScriptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Sub added", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

}
