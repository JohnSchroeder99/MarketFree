package johnschroeders.marketfree;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.jar.Attributes;

public class ManageSubsciptionsActivity extends AppCompatActivity {
    Button subscriptionsBackButton = null;
    Button addSubScriptionsButton = null;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_subsciptions);

        subscriptionsBackButton = findViewById(R.id.manageSubscriptionsBackButton);
        addSubScriptionsButton = findViewById(R.id.manageSubscriptionsaddButton);

        Log.d("Manage", "before arraylist made");


        ArrayList<String> names = new ArrayList<>();
        names.add("John");
        names.add("Deborah");
        names.add("Useless");
        names.add("Brandumb");
        names.add("Richard");
        names.add("Idiot");

        Log.d("Manage", "after arraylist made");

        RecyclerView recyclerView = findViewById(R.id.manageSubscriptionsView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.d("Manage", "recyclerlayout set");
        mAdapter = new MyRecyclerViewAdapter(this, names);
        Log.d("Manage", "adapter initialized");
        recyclerView.setAdapter(mAdapter);
        Log.d("Manage", "adapter made");






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
