package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ManageProfileActivity extends AppCompatActivity {
    private Button manageProfileSaveButton = null;
    private Button manageProfileUpdateBankingButton = null;
    private Button manageProfileBackButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);

        manageProfileSaveButton = findViewById(R.id.manageProfileSaveButton);
        manageProfileUpdateBankingButton = findViewById(R.id.manageProfileUpdateBankingButton);
        manageProfileBackButton = findViewById(R.id.manageProfileBackButton);

        manageProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });

        manageProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Profile Saved functionality here", Toast.LENGTH_LONG);
                toast.show();
            }
        });

        manageProfileUpdateBankingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(getApplicationContext(), "Banking update Fragement called here", Toast.LENGTH_LONG);
                toast.show();
            }
        });


    }


}


