package mushare.org.rate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mushare.org.rate.fragment.me.MeFragment;
import mushare.org.rate.fragment.news.NewsFragment;
import mushare.org.rate.fragment.rate.RateFragment;
import mushare.org.rate.fragment.subscribe.SubscribeFragment;

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