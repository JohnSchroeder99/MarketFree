package johnschroeders.marketfree;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

// this class is the main layout for the application. This might be a good place to start
// grabbing data for the client- perhaps not if we want to limit calls for only user usage.
public class UserMainPageManagePersonalsActivity extends AppCompatActivity implements ManageYourThingsFragment.OnFragmentInteractionListener, SeeWhatsNewFragment.OnFragmentInteractionListener {
    private final static String TAG = "MainActivity";
    private int selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);

        TabLayout tabLayout = findViewById(R.id.mainActivityTabLayout);

        //deciding which tab to open on start up and screen rotation.
        if (savedInstanceState != null) {
            if (savedInstanceState.getInt("SavedTab") == 0) {
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                SeeWhatsNewFragment fragment = new SeeWhatsNewFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                        fragment).commit();
                selectedTab = 0;
                savedInstanceState.clear();
            } else {
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                ManageYourThingsFragment fragment = new ManageYourThingsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                        fragment).commit();
                selectedTab = 1;
                savedInstanceState.clear();
            }
        } else {
            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
            SeeWhatsNewFragment fragment = new SeeWhatsNewFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                    fragment).commit();
            selectedTab = 0;
        }

        //setting up listener event for tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab was selected:  " + tab.getPosition() + tab.getText());
                if (tab.getText().equals("See Whats New")) {
                    selectedTab = 0;
                    SeeWhatsNewFragment fragment = new SeeWhatsNewFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                            fragment).commit();
                } else if (tab.getText().equals("Manage Your Things")) {
                    selectedTab = 1;
                    ManageYourThingsFragment fragment = new ManageYourThingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityContainer,
                            fragment).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SavedTab", selectedTab);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
