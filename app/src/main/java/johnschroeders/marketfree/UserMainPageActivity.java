package johnschroeders.marketfree;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

// this class is the main layout for the application. This might be a good place to start
// grabbing data for the client- perhaps not if we want to limit calls for only user usage.
public class UserMainPageActivity extends AppCompatActivity {
    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        Button manageSubscriptionsButton = findViewById(R.id.mainActivityManageSubscriptionsButton);
        Button managePublishingButton = findViewById(R.id.mainActivityManagePublishingButton);
        Button manageOrderStatusButton = findViewById(R.id.mainActivityManageOrderStatusButton);
        Button manageProfileButton = findViewById(R.id.mainActivityManageProfileButton);


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
