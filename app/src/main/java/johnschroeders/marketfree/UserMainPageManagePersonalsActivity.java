package johnschroeders.marketfree;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

// this class is the main layout for the application. This might be a good place to start
// grabbing data for the client- perhaps not if we want to limit calls for only user usage.
public class UserMainPageManagePersonalsActivity extends AppCompatActivity implements ManageYourThingsFragment.OnFragmentInteractionListener,
        SeeWhatsNewFragment.OnFragmentInteractionListener, SeeNewMessagesFragment.OnFragmentInteractionListener {
    private final static String TAG = "MainActivity";
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_page);



        TextView userName = findViewById(R.id.UserName);
        TextView customerKey = findViewById(R.id.CustomerKey);
        ImageView userImage = findViewById(R.id.CardImageView);
        userName.setText( getIntent().getStringExtra("CustomerKey"));
        customerKey.setText( getIntent().getStringExtra("UserName"));
        Glide.with(getApplicationContext()).asBitmap().
                load( getIntent().getStringExtra("Photo")).into(userImage);




        Log.d(TAG, "Setting up Main activity with Intent " + getIntent().getIntExtra("SavedTab",
                100));
        tabLayout = findViewById(R.id.mainActivityTabLayout);
        ViewPager viewPager = findViewById(R.id.mainAcitivityViewPager);



        // do the checks to see if we arrived here from another activity or if we arrived here
        // from screen rotation. Either way check to see where we should be and start with the
        // display at the correct position.
        if (getIntent().getIntExtra("SavedTab", 100) == 1) {
            MyViewPager adapter = new MyViewPager(getSupportFragmentManager(), 1);
            //Adding adapter to pager
            viewPager.setAdapter(adapter);
            //setting up listener event for tab selection
            tabLayout.setupWithViewPager(viewPager);
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            getIntent().removeExtra("SavedTab");
        } else if (savedInstanceState != null) {
            if (savedInstanceState.getInt("SavedTab") == 0) {
                MyViewPager adapter = new MyViewPager(getSupportFragmentManager(), 0);
                //Adding adapter to pager
                viewPager.setAdapter(adapter);
                //setting up listener event for tab selection
                tabLayout.setupWithViewPager(viewPager);
                Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                savedInstanceState.clear();
            } else if (savedInstanceState.getInt("SavedTab") == 1) {
                MyViewPager adapter = new MyViewPager(getSupportFragmentManager(), 1);
                //Adding adapter to pager
                viewPager.setAdapter(adapter);
                //setting up listener event for tab selection
                tabLayout.setupWithViewPager(viewPager);
                Objects.requireNonNull(tabLayout.getTabAt(1)).select();
                savedInstanceState.clear();
            }
        } else {
            MyViewPager adapter = new MyViewPager(getSupportFragmentManager(), 0);
            //Adding adapter to pager
            viewPager.setAdapter(adapter);
            //setting up listener event for tab selection
            tabLayout.setupWithViewPager(viewPager);
            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SavedTab", tabLayout.getSelectedTabPosition());
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
