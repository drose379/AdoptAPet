package dylan.com.adoptapet;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan Rose on 10/27/15.
 */
public class DogFragment extends Fragment implements View.OnClickListener {

    private LocationManager locationManager;

    private EditText postalBox;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        locationManager = ( LocationManager ) context.getSystemService( Context.LOCATION_SERVICE );
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);



    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup v, Bundle savedInstnace ) {
        View view = inflater.inflate( R.layout.dog_frag, v, false );

        ImageView locationIcon = (ImageView) view.findViewById(R.id.locationIcon);
        Button selectBreed = (Button) view.findViewById(R.id.breedSelectButton);
        postalBox = (EditText) view.findViewById(R.id.postalBox);

        locationIcon.setOnClickListener( this );
        selectBreed.setOnClickListener(this);

        return view;
    }

    @Override
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
            case R.id.breedSelectButton :

                View dialogView = LayoutInflater.from( getContext() ).inflate( R.layout.breed_select_dialog, null );
                ListView breedList = (ListView) dialogView.findViewById(R.id.breedList);
                AutoCompleteTextView breedSearch = (AutoCompleteTextView) dialogView.findViewById(R.id.breedSearch);
                final LinearLayout selectedBreeds = (LinearLayout) dialogView.findViewById(R.id.selectedBreeds);
                //selectedBreeds.setMovementMethod( new ScrollingMovementMethod() );

                //TODO:: Add click to select on each item in the listview, keep list of selected items below the search, can select multiple
                    //TODO:: When grabbing multiple breeds, need to grab one by one and combine results

                ArrayAdapter<String> adapter = new ArrayAdapter<String>( getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.dog_breeds) );

                breedSearch.setAdapter( adapter );
                breedList.setAdapter(adapter);
                breedList.setFastScrollEnabled(true);

                breedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int item, long id) {
                        String selected = getResources().getStringArray(R.array.dog_breeds)[item];
                        /**
                        if (selectedBreeds.getText().length() == 0) {
                            selectedBreeds.setText(selected);
                        } else {
                            selectedBreeds.setText(selected + ", " + selectedBreeds.getText());
                        }
                         */

                        TextView newBreed = new TextView( getContext() );
                        newBreed.setText(selected);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins( 5, 5, 5, 5 );
                        //TODO: Set padding to the text insdie the textview
                        //TODO:: Instead of using a dialog for breed selection, create an entire activity, can offer the most options this way

                        newBreed.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        newBreed.setTextColor(getResources().getColor(R.color.colorWhite));
                        newBreed.setLayoutParams( layoutParams );

                        selectedBreeds.addView( newBreed, 0 );

                    }
                });

                AlertDialog breedSelect = new AlertDialog.Builder( getContext() )
                        .setCustomTitle( LayoutInflater.from( getContext() ).inflate( R.layout.dog_breed_title, null ) )
                        .setView(dialogView)
                        .setPositiveButton("Save", null)
                        .setNegativeButton( "Cancel", null )
                        .setNeutralButton( "Clear", null )
                        .create();

                breedSelect.show();

                breedSelect.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //save button
                    }
                });

                breedSelect.getButton( AlertDialog.BUTTON_NEUTRAL ).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        selectedBreeds.removeAllViews();
                    }
                });

                break;
        }
    }

    public void grabLocationZip() {
        final AlertDialog locationWaiting = new AlertDialog.Builder( getContext() )
                .setTitle("One Moment...")
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

    public void parseLocation( Location location ) {
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
