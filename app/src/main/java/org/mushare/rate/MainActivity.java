package org.mushare.rate;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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
//        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id
//                .bottom_navigation);
//        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu
//                .navigation_items);
//        navigationAdapter.setupWithBottomNavigation(bottomNavigation);
//
//        // Disable the translation inside the CoordinatorLayout
//        bottomNavigation.setBehaviorTranslationEnabled(true);
//
//        // Enable the translation of the FloatingActionButton
////        bottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);
//
//        // Change colors
//        bottomNavigation.setDefaultBackgroundResource(R.color.colorBottomNavigationBackground);
//        bottomNavigation.setAccentColor(getResources().getColor(R.color.colorAccent));
////        bottomNavigation.setInactiveColor(Color.argb(153, 35, 35, 35));
//
//        // Change font size
//        bottomNavigation.setTitleTextSize(getResources().getDisplayMetrics().scaledDensity * 14,
//                getResources().getDisplayMetrics().scaledDensity * 12);
//
//        // Manage titles
//        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
//
//        // Customize notification (title, background, typeface)
//        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));
//
//        // Add or remove notification for each item
//        bottomNavigation.setNotification("1", 3);
//
//        // Set listeners
//        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
//            @Override
//            public boolean onTabSelected(int position, boolean wasSelected) {
//                // Do something cool here...
//                return true;
//            }
//        });
//        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation
//                .OnNavigationPositionListener() {
//            @Override
//            public void onPositionChange(int y) {
//                // Manage the new y position
//            }
//        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
