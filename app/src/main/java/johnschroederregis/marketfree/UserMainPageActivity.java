package johnschroederregis.marketfree;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class UserMainPageActivity extends AppCompatActivity {

    Button manageSubscriptionsButton = null;
    Button managePublishingButton = null;
    Button manageOrderStatusButton = null;
    Button manageProfileButton = null;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        manageSubscriptionsButton = findViewById(R.id.manageSubscriptionsButton);
        managePublishingButton = findViewById(R.id.managePublishingButton);
        manageOrderStatusButton = findViewById(R.id.manageOrderStatusButton);
        manageProfileButton = findViewById(R.id.manageProfileButton);


        // create listeners for each button select that is tied to specific activities

        manageSubscriptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ManageSubsciptionsActivity.class);
                startActivity(intent);
            }
        });

        managePublishingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ManagePublishingActivity.class);
                startActivity(intent);
            }
        });

        manageOrderStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ManageOrderStatusActivity.class);
                startActivity(intent);
            }
        });

        manageProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ManageProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
