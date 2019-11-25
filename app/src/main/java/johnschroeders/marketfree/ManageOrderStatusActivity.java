package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ManageOrderStatusActivity extends AppCompatActivity {
    private Button manageOrdersBackButton = null;
    private RecyclerView recyclerView = null;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_order_status);
        manageOrdersBackButton = findViewById(R.id.manageOrdersBackButton);

        manageOrdersBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserMainPageActivity.class);
                startActivity(intent);
            }
        });


        ArrayList<String> names = new ArrayList<>();
        names.add("John");
        names.add("Deborah");
        names.add("Useless");
        names.add(" Brandumb");
        names.add("Richard");
        names.add("Idiot");
        names.add("John");
        names.add("Deborah");
        names.add("Useless");
        names.add(" Brandumb");
        names.add("Richard");
        names.add("Idiot");
        names.add("John");
        names.add("Deborah");
        names.add("Useless");
        names.add(" Brandumb");
        names.add("Richard");
        names.add("Idiot"); names.add("John");
        names.add("Deborah");
        names.add("Useless");
        names.add(" Brandumb");
        names.add("Richard");
        names.add("Idiot");


        Log.d("Creds", "before recyclerlayout set");
        recyclerView = findViewById(R.id.manageOrdersView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("Creds", "recyclerlayout set");
        mAdapter = new MyRecylcerViewAdapterForOrdersStatus(this, names);
        Log.d("Creds", "adapter initialized");
        recyclerView.setAdapter(mAdapter);
        Log.d("Creds", "adapter made");


    }

}
