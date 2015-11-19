package dylan.com.adoptapet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private AlertDialog breedSelectDialog;

    private AlertDialog loadingDialog;

    private int selectedType = -1;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.content_main);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) toolbar.findViewById( R.id.toolbarTitle );
        ImageView menuButton = (ImageView) toolbar.findViewById( R.id.toolbarMenuButton );

        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.searchFab);

        //TODO:: Show a background around animal selector when it is clicked

        drawer = (DrawerLayout) findViewById( R.id.drawer );
        dogSelect = (ImageView) findViewById( R.id.dogSelect );
        catSelect = (ImageView) findViewById( R.id.catSelect );

        ImageView locationIcon = (ImageView) findViewById(R.id.locationIcon);
        postalBox = (EditText) findViewById(R.id.postalBox);

        dogSelect.setOnClickListener( this );
        catSelect.setOnClickListener( this );
        searchButton.setOnClickListener( this );


        searchButton.setOnClickListener( this );
        locationIcon.setOnClickListener( this );
        menuButton.setOnClickListener( this );

        toolbarTitle.setText( "AdoptAPet" );

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

            case R.id.searchFab :

                if ( selectedType != -1 && !postalBox.getText().toString().isEmpty() ) {
                    Intent details = new Intent( this, PetSearchDetails.class );
                    details.putExtra( "type", selectedType );
                    details.putExtra( "location", postalBox.getText().toString() );
                    startActivity(details);
                } else {
                    if ( selectedType == -1 ) {
                        Snackbar.make( drawer, "Please select a type!", Snackbar.LENGTH_SHORT ).show();
                    } else {
                        Snackbar.make( drawer, "Please provide your location!", Snackbar.LENGTH_SHORT ).show();
                    }
                }

                break;

            case R.id.dogSelect :
                dogSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarkerer ) );
                catSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDark ) );
                selectedType = 1;
                break;

            case R.id.catSelect :
                catSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarkerer ) );
                dogSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDark ) );
                selectedType = 2;
                break;
            case R.id.otherSelect :
                //open dialog to select type of "other" animal
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
