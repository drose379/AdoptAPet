package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
public class MainActivity extends AppCompatActivity implements View.OnClickListener, APIHelper.Callback {

    private ArrayList<String> selectedBreeds;

    private LocationManager locationManager;
    private EditText postalBox;
    private Button breedSelectButton;

    private DrawerLayout drawer;
    private ListView navItemsList;

    private ImageView dogSelect;
    private ImageView catSelect;
    private ImageView pigSelect;
    private ImageView rabbitSelect;
    private ImageView birdSelect;
    private ImageView horseSelect;
    private ImageView sheepSelect;
    private ImageView reptileSelect;
    private ImageView mouseSelect;

    private ArrayList<ImageView> selectables;

    private AlertDialog breedSelectDialog;

    private AlertDialog loadingDialog;

    private int selectedType = -1;

    private int currentFeatured = -1;

    private NavMenuAdapter navAdapter;
    private ArrayList<PetResult> featured;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView( R.layout.content_main );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView menuButton = (ImageView) toolbar.findViewById(R.id.toolbarMenuButton);

        selectables = new ArrayList<ImageView>();

        ImageView locationIcon = (ImageView) findViewById(R.id.locationIcon);
        postalBox = (EditText) findViewById(R.id.postalBox);

        LinearLayout searchButton = (LinearLayout) findViewById( R.id.searchButton );

        drawer = (DrawerLayout) findViewById( R.id.drawer );
        navItemsList = (ListView) findViewById( R.id.navItemsList );

        dogSelect = (ImageView) findViewById( R.id.dogSelect );
        catSelect = (ImageView) findViewById( R.id.catSelect );
        pigSelect = (ImageView) findViewById( R.id.pigSelect );
        rabbitSelect = (ImageView) findViewById( R.id.rabbitSelect );
        birdSelect = (ImageView) findViewById(  R.id.birdSelect );
        horseSelect = (ImageView) findViewById( R.id.horseSelect );
        sheepSelect = (ImageView) findViewById( R.id.sheepSelect );
        reptileSelect = (ImageView) findViewById( R.id.alligatorSelect );
        mouseSelect = (ImageView) findViewById( R.id.mouseSelect );

        selectables.add( dogSelect );
        selectables.add( catSelect );
        selectables.add( pigSelect );
        selectables.add( rabbitSelect );
        selectables.add( birdSelect );
        selectables.add( horseSelect );
        selectables.add( sheepSelect );
        selectables.add( reptileSelect );
        selectables.add(mouseSelect);

        searchButton.setOnClickListener(this);

        for ( ImageView item : selectables ) {
            item.setOnClickListener( this );
        }


        searchButton.setOnClickListener( this );
        locationIcon.setOnClickListener( this );
        menuButton.setOnClickListener(this);

        toolbarTitle.setText("AdoptAPet");

        locationManager = ( LocationManager ) getSystemService( Context.LOCATION_SERVICE );
        selectedBreeds = new ArrayList<String>();

    }

    @Override
    public void onResume() {
        super.onResume();

        if ( SearchResults.badLocation ) {
            Snackbar.make( drawer, "Please specify a valid location", Snackbar.LENGTH_SHORT ).show();
        }

        initNavDrawer();

    }


    @Override
    public void onStop() {
        super.onStop();
    }

    public void clearSelectedItems() {
        for( ImageView item : selectables ) {
            item.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDark ) );
        }
    }

    public void initNavDrawer() {

        //TODO:: Add featured animal as head view in nav drawer
        /**
         * Have a List of PetResults for the featured and show a different one each time drawer opens
         * Must have photo
         * Must have name
         *
         * Make an API call here to get all featured items, use the API helper to be returned with a ArrayList<PetResult>
         * When the PetResult arraylist of featured animals comes back, populate the first featued item
         * When drawer closes, change to the next featured item (keep track with an int), go back to beginning of list once hit last item
         * Featured items MUST have a photo
         * When clicked, go to PetResultDetail
         */



        ArrayList<MenuItem> items = new ArrayList<MenuItem>();

        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT * FROM " + ZipDBHelper.table_name, null, null ); /** Difference between rawQuery() and query() etc */
        if ( result.getCount() == 0 ) {

            Log.i("ZIP", String.valueOf( result.getColumnIndex( "zip" ) ));

            items.add(new MenuItem()
                            .setType(2)
                            .setName("Please specify location!")
                            .setPhoto( "https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png" )
            );

        } else if ( featured == null ) {
            /**
             * Make the request with the location
             * When callback comes in, grab the adapter for the listview of nav items, add new MenuItem at pos 0 of type 2 with first PetResult's info
             */

            result.moveToFirst();
            String zip = result.getString( 0 );

            try {

                JSONObject requestInfo = new JSONObject();
                requestInfo.put( "location", zip );
                requestInfo.put( "age", new JSONArray().put( "senior" ) );
                requestInfo.put( "type", "dog" ); /** Start with dogs, but once system works, implement other types of animals here too */

                APIHelper.makeRequest( this, zip, new Handler() ,requestInfo );

            } catch ( JSONException e ) {
                throw new RuntimeException( e.getMessage() );
            }

            drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {

                @Override
                public void onDrawerClosed( View drawer ) {

                    if ( currentFeatured + 1 <= featured.size() - 1 )
                        currentFeatured = currentFeatured + 1;
                    else
                        currentFeatured = 0;



                    PetResult newFeat = featured.get( currentFeatured );

                    MenuItem item = new MenuItem( )
                            .setName( newFeat.getName() )
                            .setPhoto( newFeat.getBestPhoto( 1 ) != null ? newFeat.getBestPhoto( 1 ) : newFeat.getBestPhoto( 2 ) )
                            .setSex( newFeat.getSex() );

                    navAdapter.updateFeatured( item );

                }

            });

        }

        items.add(new MenuItem()
                        .setType(1)
                        .setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp))
                        .setLabel("Home")
        );
        items.add(new MenuItem()
                        .setType(1)
                        .setIcon(getResources().getDrawable(R.drawable.ic_favorite_black_24dp))
                        .setLabel("Favorites")
        );

        navAdapter = new NavMenuAdapter(this, items);
        navItemsList.setAdapter(navAdapter);
    }

    @Override
    public void getResults( ArrayList<PetResult> featured ) {
        this.featured = new ArrayList<PetResult>();
        if ( featured != null && !featured.isEmpty() ) {

            /**
             * TAKING CHANCES HERE ON FIRST AND SECOND, NEED TO REALLY MAKE SURE THE IMAGE IS PRESENT
             */
            for ( PetResult item : featured ) {

                if ( item.getBestPhoto( 1 ) != null || item.getBestPhoto( 2 ) != null ) {
                    this.featured.add( item );
                }

            }

            PetResult first = this.featured.get( 0 );
            MenuItem firstFeat = new MenuItem()
                    .setType(2)
                    .setName( first.getName() )
                    .setPhoto( first.getBestPhoto( 1 ) != null ? first.getBestPhoto( 1 ) : first.getBestPhoto( 2 ) )
                    .setSex( first.getSex() );


            //add to adapter, notify change, custom updateFeatured method
            currentFeatured = 0;
            navAdapter.updateFeatured( firstFeat );


        } else {
            /**
             * Handle the error, no featured
             */
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

                    ZipDBHelper zipHelper = new ZipDBHelper( this );
                    SQLiteDatabase db = zipHelper.getReadableDatabase();

                    Cursor result = db.rawQuery( "SELECT * FROM " + ZipDBHelper.table_name, null );

                    if ( result.getCount() == 0 ) {
                        SQLiteDatabase writeable = new ZipDBHelper( this ).getWritableDatabase();
                        ContentValues vals = new ContentValues();
                        vals.put( "zip", location );
                        writeable.insert(ZipDBHelper.table_name, null, vals);

                    }

                    if ( selectedType != -1 && selectedType <= 2  ) {

                        Intent details = new Intent( this, PetSearchDetails.class );
                        details.putExtra( "type", selectedType );
                        details.putExtra( "location", postalBox.getText().toString() );
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
                            throw new RuntimeException( e.getMessage() );
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

            case R.id.pigSelect :
                clearSelectedItems();
                pigSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 3;
                break;
            case R.id.rabbitSelect :
                clearSelectedItems();
                rabbitSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 4;
                break;
            case R.id.birdSelect :
                clearSelectedItems();
                birdSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 5;
                break;
            case R.id.horseSelect :
                clearSelectedItems();
                horseSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarker ) );

                selectedType = 6;
                break;
            case R.id.sheepSelect :
                clearSelectedItems();
                sheepSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 7;
                break;
            case R.id.alligatorSelect :
                clearSelectedItems();
                reptileSelect.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDarker));

                selectedType = 8;
                break;
            case R.id.mouseSelect :
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
            throw new RuntimeException( e.getMessage() );
        }

    }



}
