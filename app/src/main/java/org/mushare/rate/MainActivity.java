package org.mushare.rate;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.CurrencyShowList;
import org.mushare.rate.data.DBOpenHelper;
import org.mushare.rate.data.RateList;
import org.mushare.rate.tab.news.NewsFragment;
import org.mushare.rate.tab.rate.RateFragment;
import org.mushare.rate.tab.subscribe.SubscribeFragment;


public class MainActivity extends AppCompatActivity {
    //    private ViewPager viewPager;

    private Fragment fragment;
    private FragmentManager fragmentManager;

    private int selectedNavigationItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

//        View statusBarCover = findViewById(R.id.status_bar_cover);
//        ViewGroup.LayoutParams layoutParams = statusBarCover.getLayoutParams();
//        layoutParams.height = getStatusBarHeight();
//        statusBarCover.setLayoutParams(layoutParams);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectedNavigationItemId = item.getItemId();
                        switch (selectedNavigationItemId) {
                            case R.id.tab_rate:
                                fragment = new RateFragment();
                                break;
                            case R.id.tab_subscribe:
                                fragment = new SubscribeFragment();
                                break;
                            case R.id.tab_news:
                                fragment = new NewsFragment();
                                break;
                        }
                        fragmentManager.popBackStack(null, FragmentManager
                                .POP_BACK_STACK_INCLUSIVE);
                        final FragmentTransaction transaction = fragmentManager
                                .beginTransaction();
                        transaction.setCustomAnimations(R.anim.fragment_enter, 0).replace(R.id
                                .main_container, fragment).commit();
                        return true;
                    }
                });

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            fragment = new RateFragment();
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_container, fragment).commit();
        } else {
            selectedNavigationItemId = savedInstanceState.getInt("selected_navigation_item_id", R
                    .id.tab_rate);
            bottomNavigationView.getMenu().findItem(selectedNavigationItemId).setChecked(true);
        }

    }


//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selected_navigation_item_id", selectedNavigationItemId);
    }

    @Override
    protected void onStart() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this, "db", 1);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        CurrencyList.reloadFromCache(sqLiteDatabase);
        RateList.reloadFromCache(sqLiteDatabase);
        CurrencyShowList.reloadFromCache(sqLiteDatabase);
        sqLiteDatabase.close();
        dbOpenHelper.close();
        super.onStart();
    }

    @Override
    protected void onStop() {
        DBOpenHelper dbOpenHelper = new DBOpenHelper(this, "db", 1);
        SQLiteDatabase sqLiteDatabase = dbOpenHelper.getReadableDatabase();
        CurrencyList.cache(sqLiteDatabase);
        RateList.cache(sqLiteDatabase);
        CurrencyShowList.cache(sqLiteDatabase);
        sqLiteDatabase.close();
        dbOpenHelper.close();
        super.onStop();
    }
}
