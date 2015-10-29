package dylan.com.adoptapet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dylan on 10/26/15.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitles = {"Dogs","Cats","Other"};
    private int[] tabIcons = { R.drawable.ic_action_year_of_dog_50, R.drawable.ic_action_black_cat_50, R.drawable.ic_action_pig_50 };
    //also have array of icon for each item (paw print for "Other"          )

    public HomePagerAdapter(FragmentManager manager) {
        super( manager );
    }

    public Fragment getItem( int item ) {
        Fragment frag;
        switch ( item ) {
            case 0 :
                frag = new DogFragment();
                break;
            case 1 :
                frag = new DogFragment();
                break;
            case 2 :
                frag = new DogFragment();
                break;
            default :
                throw new RuntimeException();
        }

        return frag;
    }

    public int getCount() {
        return tabTitles.length;
    }

    public String getPageTitle( int position ) {
        return tabTitles[position];
    }

    public int getTabDrawable( int position ) {
        return tabIcons[position];
    }

}
