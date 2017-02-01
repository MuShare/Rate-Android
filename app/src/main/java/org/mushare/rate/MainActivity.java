package org.mushare.rate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.mushare.rate.fragment.rate.RateFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (NoSwipeViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.tab_rate:
                                viewPager.setCurrentItem(0, false);
                                break;
                            case R.id.tab_subscribe:
                                viewPager.setCurrentItem(1, false);
                                break;
                            case R.id.tab_news:
                                viewPager.setCurrentItem(2, false);
                                break;
                            case R.id.tab_me:
                                viewPager.setCurrentItem(3, false);
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0 &&
                ((RateFragment) ((FragmentPagerAdapter) viewPager.getAdapter()).getItem(0))
                        .closeSearch()) return;
        super.onBackPressed();
    }
}
