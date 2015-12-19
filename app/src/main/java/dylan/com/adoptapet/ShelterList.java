package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dylan on 11/23/15.
 */
public class ShelterList extends AppCompatActivity implements SheltersNearUserFrag.NewBookmarkCallback {

    private ViewPager mainPager;

    private String[] shelterNames;
    private String location;

    private SheltersNearUserFrag shelterNearUserFrag;
    private ShelterBookmarkedFrag shelterBookmarkFrag;



    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.shelter_list_layout);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainPager = (ViewPager) findViewById( R.id.pager );

        ShelterPagerAdapter pagerAdapter = new ShelterPagerAdapter( getSupportFragmentManager(), this );
        mainPager.setAdapter(pagerAdapter);

        TabLayout tabs = (TabLayout) findViewById( R.id.tabs );
        tabs.setupWithViewPager( mainPager );

        //TODO:: In order to get instance of the bookmark frag, need to do fragmentManager.findFragmentByTag();
        //TODO:: ^^ In order to get tag, this must implement an interface from the fragment that will pass the tag back to this activity

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shelter_list_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( android.view.MenuItem item ) {

        switch ( item.getItemId() ) {
            case android.R.id.home :
                finish();
                break;
            case R.id.searchButton :

                switch ( mainPager.getCurrentItem() ) {
                    case 0 :
                        shelterNearUserFrag.initSearchDialog();
                        break;
                    case 1 :
                        shelterBookmarkFrag.initSearchDialog();
                        break;
                }

                break;
        }

        return super.onOptionsItemSelected( item );
    }



    @Override
    public void shelterBookmarkReload() {
        //TODO:: Make call to the bookmarks fragment to reload its data, new bookmark has been added

        if ( shelterBookmarkFrag != null ) {

            shelterBookmarkFrag.reloadBookmarks();

        } else {
            //Try again...
            shelterBookmarkReload();
        }

    }


    class ShelterPagerAdapter extends FragmentPagerAdapter {

        private Context context;

        public ShelterPagerAdapter( FragmentManager manager, Context context ) {
            super( manager );
            this.context = context;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem( int item ) {
            switch ( item ) {
                case 0 :
                    shelterNearUserFrag = new SheltersNearUserFrag();
                    return shelterNearUserFrag;
                case 1 :
                    shelterBookmarkFrag = new ShelterBookmarkedFrag();
                    return shelterBookmarkFrag;
                default :
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle( int item ) {
            switch ( item ) {
                case 0 :

                    return "Near Me";

                case 1 :

                    return "Bookmarked";

                default :

                    return null;
            }
        }


    }


}

