package dylan.com.adoptapet;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan Rose on 10/27/15.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager locationManager;
    private EditText postalBox;
    private Button breedSelectButton;

    private DrawerLayout drawer;
    private ListView navItemsList;

    private ImageView dogSelect;
    private ImageView catSelect;
    private LinearLayout pigSelectParent;
    private LinearLayout rabbitSelect;
    private LinearLayout birdSelect;
    private LinearLayout horseSelect;
    private LinearLayout sheepSelect;
    private LinearLayout reptileSelect;
    private LinearLayout mouseSelect;

    private ArrayList<LinearLayout> selectables;

    private AlertDialog breedSelectDialog;

    private AlertDialog loadingDialog;

    private int selectedType = -1;

    private NavMenuAdapter navAdapter;

    private String location = null;

    private BroadcastReceiver featuredReceiver;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.content_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar( toolbar );

        selectables = new ArrayList<LinearLayout>();

        ImageView locationIcon = (ImageView) findViewById(R.id.locationIcon);
        postalBox = (EditText) findViewById(R.id.postalBox);

        LinearLayout searchButton = (LinearLayout) findViewById( R.id.searchButton );

        drawer = (DrawerLayout) findViewById( R.id.drawer );
        navItemsList = (ListView) findViewById( R.id.navItemsList );

        dogSelect = (ImageView) findViewById( R.id.dogSelect );
        catSelect = (ImageView) findViewById( R.id.catSelect );
        pigSelectParent = (LinearLayout) findViewById( R.id.pigSelectParent );
        rabbitSelect = (LinearLayout) findViewById( R.id.rabbitSelectParent );
        birdSelect = (LinearLayout) findViewById(  R.id.birdSelectParent );
        horseSelect = (LinearLayout) findViewById( R.id.horseSelectParent );
        sheepSelect = (LinearLayout) findViewById( R.id.sheepSelectParent );
        reptileSelect = (LinearLayout) findViewById( R.id.alligatorSelectParent );
        mouseSelect = (LinearLayout) findViewById( R.id.mouseSelectParent );


        selectables.add( pigSelectParent );
        selectables.add( rabbitSelect );
        selectables.add( birdSelect );
        selectables.add(horseSelect);
        selectables.add(sheepSelect);
        selectables.add(reptileSelect);
        selectables.add(mouseSelect);

        pigSelectParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pigSelectParent.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));
            }
        });



        for ( LinearLayout item : selectables ) {
            item.setOnClickListener( this );
        }

        dogSelect.setOnClickListener(this);
        catSelect.setOnClickListener(this);

        searchButton.setOnClickListener(this);
        locationIcon.setOnClickListener(this);

        locationManager = ( LocationManager ) getSystemService( Context.LOCATION_SERVICE );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.open_drawer, R.string.close_drawer  );
        drawer.setDrawerListener( drawerToggle );

    }

    @Override
    public void onPostCreate( Bundle savedInstance ) {
        super.onPostCreate( savedInstance );
        drawerToggle.syncState();
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.i("PARENT", "ONPAUSE CALLED");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("PARENT", "ONDESTROY CALLED");
    }

    @Override
    public void onResume() {
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
    }

    private void clearSelectedItems() {

        dogSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDark));
        catSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDark));

        for( LinearLayout item : selectables ) {
            item.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDark ) );
        }
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
            FeaturedPetController.getInstance( this ).getFeatured( location, "dog" );
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

                        if ( FeaturedPetController.getInstance( MainActivity.this ).hasNext() ) {
                            PetResult selected = FeaturedPetController.getInstance( MainActivity.this ).getCurrent();
                            Intent detail = new Intent( MainActivity.this, PetResultDetail.class );
                            detail.putExtra( "pet", selected );
                            startActivity( detail );
                        }

                        drawer.closeDrawer( Gravity.LEFT );

                        break;
                    case 1:
                        drawer.closeDrawer( Gravity.LEFT );
                        break;
                    case 2:

                        Intent favorites = new Intent( MainActivity.this, FavoritesList.class );
                        startActivity( favorites );

                        drawer.closeDrawer( Gravity.LEFT );

                        break;
                    case 3 :
                        Intent shelters = new Intent( MainActivity.this, ShelterList.class );
                        startActivity( shelters );

                        drawer.closeDrawer( Gravity.LEFT );
                        break;
                    case 4 :

                        /**
                         * About activity
                         */

                        Intent aboutApp = new Intent( MainActivity.this, AboutActivity.class );
                        startActivity( aboutApp );

                        drawer.closeDrawer( Gravity.LEFT );
                        break;
                }
            }
        });


        if ( location == null ) {
            items.add( new MenuItem()
                            .setType( 2 )
                            .setName( "Please Specify Location" )
                            .setPhoto( "https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png" ) //TODO:: Change this to the new Loading Drawable
                            .setSex( "Male" )

            );
        } else {
            items.add( new MenuItem()
                            .setType( 2 )
                            .setName( "Grabbing Featured!" )
                            .setPhoto( "https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png" ) //TODO:: Change this to the new loading drawable
                            .setSex( "Male" )
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

            case R.id.searchButton :

                String location = postalBox.getText().toString();
                if ( !location.isEmpty() && location.length() == 5 ) {

                    /**
                     *
                     * Check if location is already saved, if not, save it
                     */

                    SQLiteDatabase readableDb = new ZipDBHelper( this ).getReadableDatabase();

                    Cursor result = readableDb.rawQuery( "SELECT * FROM " + ZipDBHelper.table_name, null );

                    if ( result.getCount() == 0 ) {
                        SQLiteDatabase writeable = new ZipDBHelper( this ).getWritableDatabase();
                        ContentValues vals = new ContentValues();
                        vals.put( "zip", location );
                        writeable.insert(ZipDBHelper.table_name, null, vals);
                        writeable.close();

                    }
                    else if ( result.getCount() == 1 ) {

                        /**
                         * Read the current saved
                         * Check if it is equal to the newly entered
                         * If not, save the newly and remove the current.
                         */

                        result.moveToFirst();
                        String currentSaved = result.getString( result.getColumnIndex( "zip" ) );
                        readableDb.close();

                        if ( !currentSaved.equals( location ) ) {
                            /**
                             * Update the old with the new
                             */

                            SQLiteDatabase writeable = new ZipDBHelper( this ).getWritableDatabase();

                            ContentValues vals = new ContentValues();
                            vals.put( "zip", location );
                            writeable.update( ZipDBHelper.table_name, vals, "zip = ?", new String[] { currentSaved } );

                            writeable.close();
                        }



                    }

                    if ( selectedType != -1 && selectedType <= 2  ) {

                        Intent details = new Intent( this, PetSearchDetails.class );
                        details.putExtra( "type", selectedType );
                        details.putExtra("location", postalBox.getText().toString());
                        startActivity(details);

                    }
                    else if ( selectedType <= 9 && selectedType >= 3 ) {
                        /**
                         * Move directly to SearchResult and pass necessary items
                         */

                        String type = "";

                        switch ( selectedType ) {

                            case 3:
                                type = "pig";
                                break;
                            case 4 :
                                type = "rabbit";
                                break;
                            case 5 :
                                type = "bird";
                                break;
                            case 6 :
                                type = "horse";
                                break;
                            case 7 :
                                type = "barnyard";
                                break;
                            case 8 :
                                type = "reptile";
                                break;
                            case 9 :
                                type = "smallfurry";
                                break;

                        }

                        try {

                            JSONObject request = new JSONObject();
                            request.put( "location", location );
                            request.put( "type", type );

                            Intent i = new Intent( this, SearchResults.class );
                            i.putExtra( "searchItems", request.toString() );
                            startActivity( i );



                        } catch ( JSONException e ) {
                            Snackbar.make( findViewById( R.id.drawer ), getResources().getString( R.string.location_possible_issue ), Snackbar.LENGTH_SHORT ).show();
                        }

                    }
                    else {
                        Snackbar.make( drawer, "Please select a type!", Snackbar.LENGTH_SHORT ).show();
                    }

                } else {
                    Snackbar.make( drawer, "Please provide your location!", Snackbar.LENGTH_SHORT ).show();
                }

                break;

            case R.id.dogSelect :
                clearSelectedItems();
                dogSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 1;
                break;

            case R.id.catSelect :
                clearSelectedItems();
                catSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 2;
                break;

            case R.id.pigSelectParent :
                clearSelectedItems();
                pigSelectParent.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 3;
                break;
            case R.id.rabbitSelectParent :
                clearSelectedItems();
                rabbitSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 4;
                break;
            case R.id.birdSelectParent :
                clearSelectedItems();
                birdSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 5;
                break;
            case R.id.horseSelectParent :
                clearSelectedItems();
                horseSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 6;
                break;
            case R.id.sheepSelectParent :
                clearSelectedItems();
                sheepSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 7;
                break;
            case R.id.alligatorSelectParent :
                clearSelectedItems();
                reptileSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 8;
                break;
            case R.id.mouseSelectParent :
                clearSelectedItems();
                mouseSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 9;
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



}