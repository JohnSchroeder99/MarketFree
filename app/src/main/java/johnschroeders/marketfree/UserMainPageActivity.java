package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class UserMainPageActivity extends AppCompatActivity {

    private Button manageSubscriptionsButton = null;
    private Button managePublishingButton = null;
    private Button manageOrderStatusButton = null;
    private Button manageProfileButton = null;
    private Intent intent = null;

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
