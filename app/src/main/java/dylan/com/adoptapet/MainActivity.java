package dylan.com.adoptapet;

import android.content.DialogInterface;
import android.os.Bundle;
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
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> selectedBreeds;

    private LocationManager locationManager;
    private EditText postalBox;
    private Button breedSelectButton;

    private DrawerLayout drawer;

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
        selectables.add( mouseSelect );

        searchButton.setOnClickListener(this);

        for ( ImageView item : selectables ) {
            item.setOnClickListener( this );
        }


        searchButton.setOnClickListener( this );
        locationIcon.setOnClickListener( this );
        menuButton.setOnClickListener( this );

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
