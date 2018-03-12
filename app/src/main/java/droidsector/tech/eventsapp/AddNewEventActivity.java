package droidsector.tech.eventsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import droidsector.tech.eventsapp.AddNewEventActivityFragments.BasicInfoFragment;
import droidsector.tech.eventsapp.AddNewEventActivityFragments.LocationFragment;
import droidsector.tech.eventsapp.AddNewEventActivityFragments.TeamMembersFragment;

public class AddNewEventActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new BasicInfoFragment();
                case 1:
                    return new LocationFragment();
                case 2:
                    return new TeamMembersFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
