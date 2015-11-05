package dylan.com.adoptapet;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dylan Rose on 10/27/15.
 */
public class DogFragment extends Fragment implements View.OnClickListener {

    private ArrayList<String> selectedBreeds;

    private LocationManager locationManager;
    private EditText postalBox;
    private Button breedSelectButton;

    private AlertDialog breedSelectDialog;

    private AlertDialog loadingDialog;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        locationManager = ( LocationManager ) context.getSystemService( Context.LOCATION_SERVICE );
        selectedBreeds = new ArrayList<String>();
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);



    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup v, Bundle savedInstnace ) {
        View view = inflater.inflate( R.layout.dog_frag, v, false );

        FloatingActionButton searchButton = (FloatingActionButton) view.findViewById(R.id.searchFab);
        ImageView locationIcon = (ImageView) view.findViewById(R.id.locationIcon);
        breedSelectButton = (Button) view.findViewById(R.id.breedSelectButton);
        postalBox = (EditText) view.findViewById(R.id.postalBox);

        searchButton.setOnClickListener( this );
        locationIcon.setOnClickListener( this );
        breedSelectButton.setOnClickListener(this);

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
            case R.id.breedSelectButton :

                View dialogView = LayoutInflater.from( getContext() ).inflate( R.layout.breed_select_dialog, null );
                ListView breedList = (ListView) dialogView.findViewById(R.id.breedList);
                final AutoCompleteTextView breedSearch = (AutoCompleteTextView) dialogView.findViewById(R.id.breedSearch);
                final LinearLayout selectedBreeds = (LinearLayout) dialogView.findViewById(R.id.selectedBreeds);
                //selectedBreeds.setMovementMethod( new ScrollingMovementMethod() );

                //TODO:: Add click to select on each item in the listview, keep list of selected items below the search, can select multiple
                    //TODO:: When grabbing multiple breeds, need to grab one by one and combine results

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>( getContext(),
                        R.layout.support_simple_spinner_dropdown_item,
                        getResources().getStringArray(R.array.dog_breeds) );

                breedSearch.setAdapter( adapter );

                breedSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View v, int item, long id) {
                        String selected = adapter.getItem(item);

                        breedSearch.setText("");

                        addSelectedBreed(selected, selectedBreeds);
                    }
                });

                breedSearch.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        breedSearch.setText("");
                    }
                });

                breedList.setAdapter(adapter);
                breedList.setFastScrollEnabled(true);

                breedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int item, long id) {
                        String selected = getResources().getStringArray(R.array.dog_breeds)[item];
                        addSelectedBreed(selected, selectedBreeds);

                    }
                });

                breedSelectDialog = new AlertDialog.Builder( getContext() )
                        .setCustomTitle( LayoutInflater.from( getContext() ).inflate( R.layout.dog_breed_title, null ) )
                        .setView(dialogView)
                        .setPositiveButton("Save", null)
                        .setNegativeButton( "Cancel", null )
                        .setNeutralButton( "Clear", null )
                        .create();

                breedSelectDialog.show();

                breedSelectDialog.getButton( AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        breedSelectDialog.dismiss();
                        processSelectedBreeds();
                    }
                });

                breedSelectDialog.getButton( AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedBreeds.removeAllViews();
                        DogFragment.this.selectedBreeds.clear();
                    }
                });

                breedSelectDialog.getButton( AlertDialog.BUTTON_NEGATIVE ).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        DogFragment.this.selectedBreeds.clear();
                        breedSelectDialog.dismiss();
                   }
                });

                break;

            case R.id.searchFab :

                /**
                 * To grab:
                 *
                 * Location: postalBox.getText().toString()
                 * Breeds: this.selectedBreeds
                 * continue...
                 */

                CheckBox maleGender = (CheckBox) getView().findViewById( R.id.male );
                CheckBox femaleGender = (CheckBox) getView().findViewById( R.id.female );

                CheckBox sizeSmall = (CheckBox) getView().findViewById( R.id.sm );
                CheckBox sizeMedium = (CheckBox) getView().findViewById( R.id.md );
                CheckBox sizeLarge = (CheckBox) getView().findViewById( R.id.lg );
                CheckBox sizeXL = (CheckBox) getView().findViewById( R.id.xl );

                CheckBox ageBaby = (CheckBox) getView().findViewById( R.id.baby );
                CheckBox ageYoung = (CheckBox) getView().findViewById( R.id.young );
                CheckBox ageAdult = (CheckBox) getView().findViewById(  R.id.adult );
                CheckBox ageSenior = (CheckBox) getView().findViewById( R.id.senior );

                CheckBox[] genderBoxes = { maleGender, femaleGender };
                CheckBox[] sizeBoxes = { sizeSmall, sizeMedium, sizeLarge, sizeXL };
                CheckBox[] ageBoxes = { ageBaby, ageYoung, ageAdult, ageSenior };

                JSONObject requestInfo = new JSONObject();

                try {

                    JSONArray genderSelected = new JSONArray();
                    JSONArray ageSelected = new JSONArray();
                    JSONArray sizeSelected = new JSONArray();

                    for ( CheckBox box : genderBoxes ) {
                        if ( box.isChecked() ) {
                            genderSelected.put( box.getText() );
                        }
                    }

                    for ( CheckBox box : sizeBoxes ) {
                        if ( box.isChecked() ) {
                            sizeSelected.put( box.getText() );
                        }
                    }

                    for ( CheckBox box : ageBoxes ) {
                        if ( box.isChecked() ) {
                            ageSelected.put( box.getText() );
                        }
                    }

                    String location = postalBox.getText().toString();
                    JSONArray breeds = new JSONArray( this.selectedBreeds.toArray() );

                    requestInfo.put( "location", location );
                    requestInfo.put( "type", "Dog" );
                    requestInfo.put( "breeds", breeds );
                    requestInfo.put( "genders", genderSelected );
                    requestInfo.put( "sizes", sizeSelected );
                    requestInfo.put("ages", ageSelected);

                    if ( requestInfo.getString( "location" ).length() == 5 ) {

                        Intent i = new Intent( getContext(), SearchResults.class );
                        i.putExtra( "searchItems", requestInfo.toString() );
                        startActivity( i );

                    } else {
                        Snackbar.make( getView(), "Please Specify a Location", Snackbar.LENGTH_SHORT ).show();
                    }


                } catch ( JSONException e ) {
                    throw new RuntimeException( e.getMessage() );
                }



                break;
        }
    }

    public void addSelectedBreed( String selected, LinearLayout parent ) {
        //TODO:: Add to arraylist<String> of selected breeds for processing later, make sure to clear if user uses clear button

        if ( this.selectedBreeds.size() < 3 && !this.selectedBreeds.contains( selected ) ) {
            TextView newBreed = new TextView( getContext() );
            newBreed.setText(selected);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins( 5, 5, 5, 5 );
            //TODO: Set padding to the text insdie the textview
            //TODO:: Instead of using a dialog for breed selection, create an entire activity, can offer the most options this way

            newBreed.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            newBreed.setTextColor(getResources().getColor(R.color.colorWhite));
            newBreed.setLayoutParams(layoutParams);

            parent.addView( newBreed, 0 );
            this.selectedBreeds.add( selected );
        } else {
            if ( this.selectedBreeds.size() == 3 ) {
                Toast.makeText( getContext(), "Max Selection is 3", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText( getContext(), "Already Selected This Breed", Toast.LENGTH_SHORT).show();
            }

        }


    }

    public void processSelectedBreeds() {
        //TODO:: get selected breeds, add them to the listview below the SELECT BREEDS button, listview should have height wrap_content
        ArrayList<String> selected = new ArrayList<String>( this.selectedBreeds);
        ArrayAdapter<String> breedsAdapter = new ArrayAdapter<String>( getContext(), R.layout.breed_list_item, R.id.title, selected );
        ListView selectedBreedsList = (ListView) getView().findViewById(R.id.selectedBreedsList);


        selectedBreedsList.setAdapter(breedsAdapter);
        selectedBreedsList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView parent, View view, int item, long id  ) {
                breedSelectDialog.show();
            }
        });

        //TODO:: Fix weird grey box under the button if a list of breeds is cleared and button re-shows

        if ( selectedBreeds.size() > 0 ) {
            LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            selectedBreedsList.setLayoutParams(listParams);
            selectedBreedsList.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDark));
            breedSelectButton.setVisibility(View.GONE);
            selectedBreedsList.setVisibility(View.VISIBLE);
        } else {
            breedSelectButton.setVisibility( View.VISIBLE );
            selectedBreedsList.setVisibility( View.GONE );
        }

        //if ( selectedBreeds.size() > 0 ) {breedSelectButton.setVisibility( View.GONE ); };

    }


    public void grabLocationZip() {
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
