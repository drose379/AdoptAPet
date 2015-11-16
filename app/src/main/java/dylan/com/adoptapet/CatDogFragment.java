package dylan.com.adoptapet;

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
import android.support.v7.app.AlertDialog;
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
public class CatDogFragment extends Fragment implements View.OnClickListener {

    private ArrayList<String> selectedBreeds;

    private LocationManager locationManager;
    private EditText postalBox;
    private Button breedSelectButton;

    private ImageView dogSelect;
    private ImageView catSelect;

    private AlertDialog breedSelectDialog;

    private AlertDialog loadingDialog;

    private int selectedType = -1;

    @Override
    public void onAttach( Context context ) {
        super.onAttach(context);
        locationManager = ( LocationManager ) context.getSystemService( Context.LOCATION_SERVICE );
        selectedBreeds = new ArrayList<String>();
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);

    }

    @Override
    public void onResume() {
        super.onResume();

        if ( SearchResults.badLocation ) {
            Snackbar.make( getView(), "Please specify a valid location", Snackbar.LENGTH_SHORT ).show();
        }

    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup v, Bundle savedInstnace ) {
        View view = inflater.inflate( R.layout.cat_dog_frag, v, false );

        FloatingActionButton searchButton = (FloatingActionButton) view.findViewById(R.id.searchFab);

        //TODO:: Show a background around animal selector when it is clicked

        dogSelect = (ImageView) view.findViewById( R.id.dogSelect );
        catSelect = (ImageView) view.findViewById( R.id.catSelect );

        dogSelect.setOnClickListener( this );
        catSelect.setOnClickListener( this );
        searchButton.setOnClickListener( this );


        ImageView locationIcon = (ImageView) view.findViewById(R.id.locationIcon);
        postalBox = (EditText) view.findViewById(R.id.postalBox);

        searchButton.setOnClickListener( this );
        locationIcon.setOnClickListener( this );

        return view;
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
                    Snackbar.make( getView(), "Please Enable Location", Snackbar.LENGTH_SHORT ).show();
                }

                break;

            case R.id.searchFab :

                if ( selectedType != -1 && !postalBox.getText().toString().isEmpty() ) {
                    Intent details = new Intent( getContext(), PetSearchDetails.class );
                    details.putExtra( "type", selectedType );
                    details.putExtra( "location", postalBox.getText().toString() );
                    startActivity(details);
                } else {
                    if ( selectedType == -1 ) {
                        Snackbar.make( getView(), "Please select a type!", Snackbar.LENGTH_SHORT ).show();
                    } else {
                        Snackbar.make( getView(), "Please provide your location!", Snackbar.LENGTH_SHORT ).show();
                    }
                }

                break;

            case R.id.dogSelect :
                dogSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarkerer ) );
                catSelect.setBackgroundColor( getResources().getColor( R.color.colorBackground ) );
                selectedType = 1;
                break;

            case R.id.catSelect :
                catSelect.setBackgroundColor( getResources().getColor( R.color.colorBackgroundDarkerer ) );
                dogSelect.setBackgroundColor( getResources().getColor( R.color.colorBackground ) );
                selectedType = 2;
                break;
        }
    }




    private void grabLocationZip() {
        final AlertDialog locationWaiting = new AlertDialog.Builder( getContext() )
                .setCustomTitle( LayoutInflater.from( getContext()).inflate( R.layout.location_title, null, false ) )
                .setMessage("Waiting For Location")
                .create();
        locationWaiting.show();

        int permission = getContext().getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getContext().getPackageName());
        if ( permission == getContext().getPackageManager().PERMISSION_GRANTED ) {

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
        Geocoder geo = new Geocoder( getContext() );
        try {
            List<Address> adresses = geo.getFromLocation( location.getLatitude(), location.getLongitude(), 1 );

            if ( adresses.size() > 0 ) {
                postalBox.setText( adresses.get( 0 ).getPostalCode() );
            } else {
                Snackbar.make( getView(), "Could Not Find Location", Snackbar.LENGTH_SHORT ).show();
            }

        } catch ( IOException e) {
            throw new RuntimeException( e.getMessage() );
        }

    }



}
