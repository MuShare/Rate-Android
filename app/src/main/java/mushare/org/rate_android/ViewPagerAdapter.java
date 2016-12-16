package mushare.org.rate_android;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dklap on 12/16/2016.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Fragment[] fragments = new Fragment[4];

    public ViewPagerAdapter(FragmentManager fm) {
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