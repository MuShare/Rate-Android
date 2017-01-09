package org.mushare.rate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.mushare.rate.fragment.me.MeFragment;
import org.mushare.rate.fragment.news.NewsFragment;
import org.mushare.rate.fragment.rate.RateFragment;
import org.mushare.rate.fragment.subscribe.SubscribeFragment;

/**
 * Created by dklap on 12/16/2016.
 */

class ViewPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments = new Fragment[4];

    ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (fragments[position] == null)
            switch (position) {
                case 0:
                    fragments[position] = new RateFragment();
                    break;
                case 1:
                    fragments[position] = new SubscribeFragment();
                    break;
                case 2:
                    fragments[position] = new NewsFragment();
                    break;
                case 3:
                    fragments[position] = new MeFragment();
                    break;
            }
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 4;
    }

}