package dylan.com.adoptapet;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan Rose on 10/27/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SelectedTypeCallback, OptionsTagCallback {

    private LocationManager locationManager;
    private EditText postalBox;
    private Button breedSelectButton;

    private DrawerLayout drawer;
    private ListView navItemsList;


    private AlertDialog breedSelectDialog;

    private AlertDialog loadingDialog;

    private NavMenuAdapter navAdapter;

    private String location = null;

    private BroadcastReceiver featuredReceiver;

    private ActionBarDrawerToggle drawerToggle;

    private MainViewPager pager;

    private LinearLayout searchButton;
    private LinearLayout backButton;


    public int selectedType = -1;

    private String optionsFragTag;


    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainPagerAdapter adapter = new MainPagerAdapter( getSupportFragmentManager() );
        pager = (MainViewPager) findViewById( R.id.viewPager );
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected( int page ) {
                if ( page == 1 ) {
                    showBackButton();
                } else {
                    hideBackButton();
                }
            }
            @Override
            public void onPageScrolled( int position, float posOffset, int posOffPixels ) {}
            @Override
            public void onPageScrollStateChanged( int state ) {}

        });

        ImageView locationIcon = (ImageView) findViewById(R.id.locationIcon);
        postalBox = (EditText) findViewById(R.id.postalBox);


        searchButton = (LinearLayout) findViewById( R.id.searchButton );
        backButton = (LinearLayout) findViewById( R.id.backButton );

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        navItemsList = (ListView) findViewById( R.id.navItemsList );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer  );
        drawer.setDrawerListener(drawerToggle);


        searchButton.setOnClickListener(this);
        backButton.setOnClickListener( this );
        locationIcon.setOnClickListener(this);

        locationManager = ( LocationManager ) getSystemService( Context.LOCATION_SERVICE );


        Log.i("DPI", "DPI: " + getResources().getDisplayMetrics().densityDpi);

    }

    @Override
    public void onPostCreate( Bundle savedInstance ) {

        super.onPostCreate(savedInstance);

        drawerToggle.syncState();

    }


    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PARENT", "ONDESTROY CALLED");
    }

    @Override
    public void onResume() {

        AppEventsLogger.activateApp(this);

        super.onResume();

        if ( SearchResults.badLocation ) {
            Snackbar.make( drawer, "Please specify a valid location", Snackbar.LENGTH_SHORT ).show();
        }

        if ( featuredReceiver == null ) {
            featuredReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    nextFeaturedPet();
                }
            };

            registerReceiver( featuredReceiver, new IntentFilter( FeaturedPetController.GET_FEATURED ) );
        }

        checkSavedLocation();
        initNavDrawer();
        nextFeaturedPet();

    }


    @Override
    public void onStop() {
        super.onStop();

        unregisterReceiver(featuredReceiver);
        featuredReceiver = null;
    }

    @Override
    public void onBackPressed() {
        /**
         * If at tab 1 (2), bring back to 0, if on 1, just exit app
         */

        if ( pager.getCurrentItem() == 1 ) {
            pager.setCurrentItem( 0, true );
        } else {
            finish();
        }

    }

    @Override
    public void updateSelectedType( int type ) {
        this.selectedType = type;

        if ( type == 1 || type == 2 ) {
            pager.setShouldSwipe(true);
        } else {
            pager.setShouldSwipe(false);
        }

        ((OptionsSelectFrag)getSupportFragmentManager().findFragmentByTag( optionsFragTag )).updateSelectedType( type );

    }

    private void checkSavedLocation() {
        /**
         * If location is saved in local Db, populate the EditText automatically for user
         */

        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT * FROM " + ZipDBHelper.table_name, null, null );

        if ( result.getCount() == 1 ) {
            result.moveToFirst();
            location = result.getString( result.getColumnIndex( "zip" ) );
            postalBox.setText( location );
            postalBox.clearFocus();
            FeaturedPetController.getInstance( this ).getFeatured(location, "dog");
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

        readable.close();

    }


    private void initNavDrawer() {

        ArrayList<MenuItem> items = new ArrayList<MenuItem>();

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerClosed(View drawer) {
                nextFeaturedPet();
            }

        });

        navItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int item, long id) {
                switch (item) {
                    case 0:

                        drawer.closeDrawer( Gravity.LEFT );

                        if ( FeaturedPetController.getInstance( MainActivity.this ).hasNext() ) {
                            PetResult selected = FeaturedPetController.getInstance( MainActivity.this ).getCurrent();
                            Intent detail = new Intent( MainActivity.this, PetResultDetail.class );
                            detail.putExtra( "pet", selected );
                            startActivity( detail );
                        }


                        break;
                    case 1:
                        drawer.closeDrawer( Gravity.LEFT );
                        break;
                    case 2:

                        drawer.closeDrawer( Gravity.LEFT );

                        Intent favorites = new Intent( MainActivity.this, FavoritesList.class );
                        startActivity(favorites);

                        break;
                    case 3 :

                        drawer.closeDrawer( Gravity.LEFT );
                        Intent shelters = new Intent( MainActivity.this, ShelterList.class );
                        startActivity(shelters);

                        break;
                    case 4 :

                        /**
                         * About activity
                         */

                        drawer.closeDrawer( Gravity.LEFT );

                        Intent aboutApp = new Intent( MainActivity.this, AboutActivity.class );
                        startActivity(aboutApp);


                        break;
                }
            }
        });

        if ( location == null ) {
            items.add(new MenuItem()
                            .setType(2)
                            .setName("Please Specify Location")
                            .setPhoto("https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png") //TODO:: Change this to the new Loading Drawable
                            .setSex("Male")

            );
        } else {
            items.add(new MenuItem()
                            .setType(2)
                            .setName("Grabbing Featured!")
                            .setPhoto("https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png") //TODO:: Change this to the new loading drawable
                            .setSex("Male")
            );
        }

        items.add(new MenuItem()
                        .setType(1)
                        .setIsCurrent( true )
                        .setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp))
                        .setLabel("Home")
        );
        items.add(new MenuItem()
                        .setType(1)
                        .setIcon(getResources().getDrawable(R.drawable.ic_favorite_black_24dp))
                        .setLabel("Favorites")
        );

        items.add( new MenuItem()
                        .setType( 1 )
                        .setIcon( getResources().getDrawable( R.drawable.ic_pets_black_24dp ) )
                        .setLabel( "Shelters" )
        );

        items.add( new MenuItem()
                        .setType( 1 )
                        .setIcon( getResources().getDrawable( R.drawable.ic_info_black_24dp ) )
                        .setLabel( "About" )
        );

        navAdapter = new NavMenuAdapter(this, items);
        navItemsList.setAdapter(navAdapter);

    }


    public void nextFeaturedPet() {
        FeaturedPetController fc = FeaturedPetController.getInstance( MainActivity.this );
        if ( fc.hasNext() ) {

            PetResult next = fc.next();

            MenuItem nextFeat = new MenuItem()
                    .setType( 2 )
                    .setName( next.getName() )
                    .setSex( next.getSex() )
                    .setPhoto(next.getBestPhoto(1) == null ? next.getBestPhoto(2) : next.getBestPhoto(1));

            navAdapter.updateFeatured( nextFeat );
        }
    }


    @Override
    @SuppressWarnings("NewApi")
    public void onClick( View v ) {
        switch ( v.getId() ) {

            case R.id.locationIcon :

                /**
                 * If Location is enabled, pass to grabLocation method to get location
                 * Else, show snackbar saying to enable location
                 */

                if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    grabLocationZip();
                } else {
                    Snackbar.make( drawer, "Please Enable Location", Snackbar.LENGTH_SHORT ).show();
                }

                break;

            case R.id.backButton :
                pager.setCurrentItem( 0, true );
                hideBackButton();
                break;

            case R.id.searchButton :

                switch ( pager.getCurrentItem() ) {
                    case 0 :

                        /**
                         * Check if type selected, if not, show SB
                         * If type selected, check if type 1 or 2, if yes, swipe over to next screen
                         * If greater then type 2, make search now
                         */

                        if ( selectedType > 0 && selectedType <= 2 ) {
                            /**
                             * Slide over
                             */

                            pager.setCurrentItem(1, true);

                        }
                        else if ( selectedType <= 9 && selectedType >= 3 ) {

                            String type = "";

                            if ( !postalBox.getText().toString().isEmpty() ) {

                                checkUpdateLocation( postalBox.getText().toString() );

                                switch (selectedType) {

                                    case 3:
                                        type = "pig";
                                        break;
                                    case 4:
                                        type = "rabbit";
                                        break;
                                    case 5:
                                        type = "bird";
                                        break;
                                    case 6:
                                        type = "horse";
                                        break;
                                    case 7:
                                        type = "barnyard";
                                        break;
                                    case 8:
                                        type = "reptile";
                                        break;
                                    case 9:
                                        type = "smallfurry";
                                        break;

                                }

                                try {

                                    JSONObject request = new JSONObject();
                                    request.put("location", postalBox.getText().toString());
                                    request.put("type", type);

                                    Intent i = new Intent(this, SearchResults.class);
                                    i.putExtra("searchItems", request.toString());
                                    startActivity(i);

                                } catch (JSONException e) {
                                    Snackbar.make(findViewById(R.id.drawer), getResources().getString(R.string.location_possible_issue), Snackbar.LENGTH_SHORT).show();
                                }

                            } else {
                                /**
                                 * No location provided, show snackbar
                                 */

                                Log.i("NO_LOC", "NO_LOC_PROVIDED");

                                Snackbar.make( findViewById( R.id.root ), "Please Provide Location", Snackbar.LENGTH_SHORT ).show();
                            }
                        } else {
                            Snackbar.make( findViewById( R.id.root ), "Please Select a Type", Snackbar.LENGTH_SHORT ).show();
                        }

                        break;
                    case 1 :

                        /**
                         * TODO:: Show dialog confirming search criteria, if "GO" is clicked, call initSearch
                         */




                        RelativeLayout dialogView = (RelativeLayout) LayoutInflater.from( this ).inflate( R.layout.search_confirm_layout, null );
                        TextView ageText = (TextView) dialogView.findViewById(R.id.ageText);
                        TextView sizeText = (TextView) dialogView.findViewById(R.id.sizeText);
                        TextView genderText = (TextView) dialogView.findViewById( R.id.genderText );
                        TextView breedText = (TextView) dialogView.findViewById( R.id.breedText );

                        String ageString = "";
                        String sizeString = "";
                        String genderString = "";
                        String breedString = "";

                        OptionsSelectFrag optionsFragment = (OptionsSelectFrag) getSupportFragmentManager().findFragmentByTag(optionsFragTag);

                        JSONArray ageArray = optionsFragment.getAgeSelection();
                        JSONArray sizeArray = optionsFragment.getSizeSelection();
                        JSONArray genderArray = optionsFragment.getGenderSelection();
                        ArrayList<String> breedsArray = optionsFragment.getSelectedBreeds();

                        if ( ageArray.length() > 0 ) {

                            for ( int i = 0; i < ageArray.length(); i++ ) {
                                try {

                                    if ( i < ageArray.length() - 1 ) {
                                        ageString += ageArray.getString( i ) + ", ";
                                    } else {
                                        ageString += ageArray.getString( i );
                                    }

                                } catch ( JSONException e ) {

                                }
                            }

                        } else {
                            ageString += "Any";
                        }

                        if ( sizeArray.length() > 0 ) {

                            for ( int i = 0; i < sizeArray.length(); i++ ) {
                                try {

                                    if ( i < sizeArray.length() - 1 ) {
                                        sizeString += sizeArray.getString( i ) + ", ";
                                    } else {
                                        sizeString += sizeArray.getString( i );
                                    }

                                } catch ( JSONException e ) {

                                }
                            }

                        } else {
                            sizeString += "Any";
                        }

                        if ( genderArray.length() > 0 ) {

                            for ( int i = 0; i < genderArray.length(); i++ ) {
                                try {

                                    if ( i < genderArray.length() - 1 ) {
                                        genderString += genderArray.getString( i ) + ", ";
                                    } else {
                                        genderString += genderArray.getString( i );
                                    }

                                } catch ( JSONException e ) {

                                }
                            }

                        } else {
                            genderString += "Any";
                        }

                        if ( breedsArray.size() > 0 ) {

                            for ( int i = 0; i < breedsArray.size(); i++ ) {


                                if ( i < breedsArray.size() - 1 ) {
                                    breedString += breedsArray.get( i ) + ", ";
                                } else {
                                    breedString += breedsArray.get( i );
                                }


                            }

                        } else {
                            breedString += "Any";
                        }



                        ageText.setText( ageString );
                        sizeText.setText( sizeString );
                        genderText.setText( genderString );
                        breedText.setText( breedString );


                        final AlertDialog confirmDialog = new AlertDialog.Builder( this )
                                .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.search_title, null ) )
                                .setView( dialogView )
                                .setPositiveButton( "GO", null )
                                .create();

                        confirmDialog.show();


                        confirmDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick( View v ) {
                                initSearch();
                                confirmDialog.dismiss();
                            }
                        });





                        break;
                }


                break;



            case R.id.toolbarMenuButton :

                if ( drawer.isDrawerOpen( Gravity.LEFT ) ) {
                    drawer.closeDrawer( Gravity.LEFT );
                } else {
                    drawer.openDrawer( Gravity.LEFT );
                }

                break;
        }
    }

    @Override
    public void getTag( String tag ) {
        optionsFragTag = tag;
    }


    private void initSearch() {
        if ( !postalBox.getText().toString().isEmpty() ) {

            checkUpdateLocation( postalBox.getText().toString() );

            JSONObject requestObject = getRequestOptions();
            try {
                requestObject.put("location", postalBox.getText().toString());
            } catch ( JSONException e ) {
                /** Handle exception */
            }


            Intent i = new Intent( this, SearchResults.class );
            i.putExtra( "searchItems", requestObject.toString() );
            startActivity( i );


        } else {
            Snackbar.make( findViewById( R.id.root ), "Please Provide a Location", Snackbar.LENGTH_SHORT ).show();
        }
    }


    private void checkUpdateLocation( String location ) {

        SQLiteDatabase readableDb = new ZipDBHelper(this).getReadableDatabase();

        Cursor result = readableDb.rawQuery("SELECT * FROM " + ZipDBHelper.table_name, null);

        if (result.getCount() == 0) {
            SQLiteDatabase writeable = new ZipDBHelper(this).getWritableDatabase();
            ContentValues vals = new ContentValues();
            vals.put("zip", location);
            writeable.insert(ZipDBHelper.table_name, null, vals);
            writeable.close();
        } else if (result.getCount() == 1) {

            /**
             * Read the current saved
             * Check if it is equal to the newly entered
             * If not, save the newly and remove the current.
             */


            result.moveToFirst();
            String currentSaved = result.getString(result.getColumnIndex("zip"));
            readableDb.close();

            if (!currentSaved.equals(location)) {


                SQLiteDatabase writeable = new ZipDBHelper(this).getWritableDatabase();

                ContentValues vals = new ContentValues();
                vals.put("zip", location);
                writeable.update(ZipDBHelper.table_name, vals, "zip = ?", new String[]{currentSaved});

                writeable.close();
            }

        }
    }

    private JSONObject getRequestOptions() {

        JSONObject requestInfo = new JSONObject();

        OptionsSelectFrag optionsFragment = (OptionsSelectFrag) getSupportFragmentManager().findFragmentByTag( optionsFragTag );

        JSONArray ageSelected = optionsFragment.getAgeSelection();
        JSONArray sizeSelected = optionsFragment.getSizeSelection();
        JSONArray genderSelected = optionsFragment.getGenderSelection();
        JSONArray optionsSelected = optionsFragment.getOptionsSelection();

        ArrayList<String> selectedBreeds = optionsFragment.getSelectedBreeds();

        /**
         * Need to modify options, some dont make sense when off, doesnt mean what users think
         */

        try {

            JSONArray breeds = new JSONArray( selectedBreeds.toArray() );

            requestInfo.put( "type", selectedType == 1 ? "dog" : "cat" );
            requestInfo.put( "breeds", breeds );
            requestInfo.put( "options", optionsSelected );
            requestInfo.put( "genders", genderSelected );
            requestInfo.put( "sizes", sizeSelected );
            requestInfo.put("ages", ageSelected);

        } catch ( JSONException e ) {
            /** Handle the exception guy */
        }

        return requestInfo;
    }

    private void grabLocationZip() {
        final AlertDialog locationWaiting = new AlertDialog.Builder( this )
                .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.location_title, null, false ) )
                .setMessage("Waiting For Location")
                .create();
        locationWaiting.show();

        int permission = this.getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, this.getPackageName());
        if ( permission == this.getPackageManager().PERMISSION_GRANTED ) {

            Location currentLocation = locationManager.getLastKnownLocation( LocationManager.GPS_PROVIDER );

            if ( currentLocation == null ) {

                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        parseLocation( location );
                        locationWaiting.hide();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                }, Looper.myLooper());

            } else {
                parseLocation( currentLocation );
                locationWaiting.hide();
            }

        } else {
            //Location permission denied
        }


    }

    private void parseLocation( Location location ) {
        Geocoder geo = new Geocoder( this );
        try {

            List<Address> adresses = geo.getFromLocation( location.getLatitude(), location.getLongitude(), 1 );

            if ( adresses.size() > 0 ) {
                postalBox.setText( adresses.get( 0 ).getPostalCode() );
            } else {
                Snackbar.make( drawer, "Could Not Find Location", Snackbar.LENGTH_SHORT ).show();
            }

        } catch ( IOException e) {
            Snackbar.make( findViewById( R.id.drawer ), getResources().getString( R.string.network_issue), Snackbar.LENGTH_SHORT ).show();
        }

    }

    private void hideBackButton() {
        backButton.setVisibility( View.GONE );
    }
    private void showBackButton() {backButton.setVisibility( View.VISIBLE ); }



}