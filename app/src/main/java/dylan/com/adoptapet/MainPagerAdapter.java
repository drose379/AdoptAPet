package dylan.com.adoptapet;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dylan on 12/4/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private View currentPrimary;


    public MainPagerAdapter( FragmentManager manager) {
        super( manager );
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem( int item ) {
        switch ( item ) {
            case 0:
                return new MainSelectFrag();
            case 1 :
                return new OptionsSelectFrag();
        }
        return null;
    }



}
