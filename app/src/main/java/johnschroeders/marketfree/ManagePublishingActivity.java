package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ManagePublishingActivity extends AppCompatActivity {
    private Button removePublishings = null;
    private Button managePublishingBackButton = null;
    private Button addPublishingButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_publishing);

        addPublishingButton = findViewById(R.id.managePublishingAddnewButton);
        removePublishings = findViewById(R.id.managePublishingRemovePublishing);
        managePublishingBackButton = findViewById(R.id.managePublishingBackButton);


        addPublishingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "moving to add publishings fragment", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        removePublishings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "moving to remove publishings fragment", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        managePublishingBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });


    }

}
