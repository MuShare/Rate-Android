package org.mushare.rate;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import org.mushare.rate.data.CurrencyList;
import org.mushare.rate.data.CurrencyShowList;
import org.mushare.rate.data.DBOpenHelper;
import org.mushare.rate.data.RateList;

//import com.roughike.bottombar.BottomBar;
//import com.roughike.bottombar.OnTabSelectListener;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MyFloatingSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        viewPager = (NoSwipeViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

//        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
//        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
//            @Override
//            public void onTabSelected(@IdRes int tabId) {
//                switch (tabId) {
//                    case R.id.tab_rate:
//                        viewPager.setCurrentItem(0, false);
//                        break;
//                    case R.id.tab_subscribe:
//                        viewPager.setCurrentItem(1, false);
//                        break;
//                    case R.id.tab_news:
//                        viewPager.setCurrentItem(2, false);
//                        break;
//                    case R.id.tab_me:
//                        viewPager.setCurrentItem(3, false);
//                        break;
//                }
//            }
//        });
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

        searchView = (MyFloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setOnFocusChangeListener(new MyFloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

            }

            @Override
            public void onFocusCleared() {
                searchView.setVisibility(View.GONE);
                searchView.setSearchText("");
            }
        });
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

    public void showSearch() {
        if (searchView != null) {
            searchView.setVisibility(View.VISIBLE);
            searchView.setSearchFocused(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (searchView != null && searchView.setSearchFocused(false)) {
            return;
        }
        super.onBackPressed();
    }
}
