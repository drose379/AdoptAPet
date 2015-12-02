package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
public class ShelterList extends AppCompatActivity implements AdapterView.OnItemClickListener, APIHelper.SheltersCallback {

    private RelativeLayout root;
    private ProgressBar loader;
    private ListView shelterList;

    private AlertDialog shelterOptions;

    private String[] shelterNames;

    private String location;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.shelter_list_layout);

        root = (RelativeLayout) findViewById( R.id.root );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        loader = (ProgressBar) findViewById( R.id.loader );
        shelterList = (ListView) findViewById( R.id.shelterList );

        checkLocation();
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
                initSearchDialog();
                break;
        }

        return super.onOptionsItemSelected( item );
    }

    private void checkLocation() {

        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT zip FROM " + ZipDBHelper.table_name, null, null);

        if ( result.getCount() == 1 ) {

            grabShelters();

        } else {

            final View layout = LayoutInflater.from( this ).inflate( R.layout.location_save_dialog,null );

            final AlertDialog getLocation = new AlertDialog.Builder( this )
                    .setCustomTitle(LayoutInflater.from(this).inflate(R.layout.location_title, null))
                    .setView(layout)
                    .setMessage(getResources().getString(R.string.provide_location))
                    .setPositiveButton("Save", null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            /**
                             * Check if location saveed, if not, show dialog again
                             */
                        }
                    })
                    .create();

            getLocation.show();


            getLocation.getButton( DialogInterface.BUTTON_POSITIVE ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText locationBox = (EditText) layout.findViewById(R.id.locationBox);
                    String location = locationBox.getText().toString();

                    if (location.length() == 5) {

                        SQLiteDatabase writeable = new ZipDBHelper( ShelterList.this ).getWritableDatabase();
                        ContentValues vals = new ContentValues();
                        vals.put( "zip", location );
                        writeable.insert(ZipDBHelper.table_name, null, vals);

                        writeable.close();

                        getLocation.dismiss();
                        grabShelters();

                    } else {
                        locationBox.setError("Invalid");
                    }

                }
            });

        }

        readable.close();

    }

    public void grabShelters() {
        /**
         * Get location from local DB, use APIHelper to make API call
         */

        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT zip FROM " + ZipDBHelper.table_name, null, null);

        result.moveToFirst();

        location = result.getString(result.getColumnIndex("zip"));

        readable.close();

        APIHelper.getShelters(location, this, new Handler());

    }

    @Override
    public void getShelterResults( ArrayList<ShelterResult> results ) {

        if ( results != null ) {
            shelterNames = new String[results.size()];

            for (int i = 0; i < results.size(); i++) {
                shelterNames[i] = results.get(i).getName();
            }

            loader.setVisibility(View.GONE);
            shelterList.setVisibility(View.VISIBLE);

            ShelterResultAdapter adapter = new ShelterResultAdapter(ShelterList.this, results);
            shelterList.setAdapter(adapter);
            shelterList.setOnItemClickListener(this);
        } else {

            /**
             * Try again
             */
            APIHelper.getShelters(location, this, new Handler());

        }


    }

    /**
     * On item click for the master list
     */
    @Override
    public void onItemClick(AdapterView parent, View item, int which, long id) {

        final ShelterResult selectedShelter = (ShelterResult) shelterList.getAdapter().getItem(which);
        final ArrayList<String> shelterMenuOptions = new ArrayList<String>();



        if ( !selectedShelter.getPhone().trim().isEmpty() )
            shelterMenuOptions.add( "Call" );

        if ( !selectedShelter.getAddress().trim().isEmpty() || !selectedShelter.getState().trim().isEmpty() || !selectedShelter.getCity().trim().isEmpty() )
            shelterMenuOptions.add( "Map" );

        if ( !selectedShelter.getEmail().trim().isEmpty() )
            shelterMenuOptions.add( "Email" );

        shelterMenuOptions.add( "Animals" );

        String[] optionsFinal = new String[shelterMenuOptions.size()];


        shelterOptions = new AlertDialog.Builder(ShelterList.this)
                .setCustomTitle(LayoutInflater.from(ShelterList.this).inflate(R.layout.shelter_title, null))
                .setItems(shelterMenuOptions.toArray(optionsFinal), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        switch (shelterMenuOptions.get(which)) {

                            case "Call":

                                String phoneNumber = selectedShelter.getPhone().trim();

                                Intent phone = new Intent(Intent.ACTION_DIAL);
                                phone.setData(Uri.parse("tel:" + phoneNumber));
                                startActivity(phone);

                                break;
                            case "Map":

                                Uri mapUri = Uri.parse("geo:0,0?q=" + selectedShelter.getAddress() + "," + selectedShelter.getState() + "," + selectedShelter.getCity());
                                Intent map = new Intent(Intent.ACTION_VIEW, mapUri);
                                map.setPackage("com.google.android.apps.maps");
                                startActivity(map);

                                break;
                            case "Email":

                                Intent mail = new Intent(Intent.ACTION_SENDTO);
                                mail.setData(Uri.parse("mailto:" + selectedShelter.getEmail()));
                                mail.putExtra(Intent.EXTRA_SUBJECT, "Interested in - " + selectedShelter.getName());

                                startActivity(mail);

                                break;
                            case "Animals":

                                shelterOptions.dismiss();

                                Intent shelterAnimals = new Intent(ShelterList.this, ShelterAnimalResults.class);
                                shelterAnimals.putExtra("shelterId", selectedShelter.getId());
                                shelterAnimals.putExtra("shelterName", selectedShelter.getName());

                                if (selectedShelter.getId() != null && !selectedShelter.getId().isEmpty()) {
                                    startActivity(shelterAnimals);
                                } else {
                                    Snackbar.make( findViewById( R.id.root ), "Shelter Error", Snackbar.LENGTH_SHORT ).show();
                                }


                                break;

                        }

                    }
                })
                .create();

        shelterOptions.show();

    }


    public void initSearchDialog() {

        /**
         * Search for shelters
         * When user selects the name of shelter from the list, get the index of the shleter in the list, smootheScroll to it
         */

        View dialogLayout = LayoutInflater.from( this ).inflate( R.layout.shelter_search, null );
        final AutoCompleteTextView shelterBox = (AutoCompleteTextView) dialogLayout.findViewById( R.id.searchBox );

        ArrayAdapter<String> items = new ArrayAdapter<String>( this, android.R.layout.simple_dropdown_item_1line, shelterNames );
        shelterBox.setAdapter(items);

        final AlertDialog shelterSearch = new AlertDialog.Builder( this )
                .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.shelter_title, null ) )
                .setView( dialogLayout )
                .setPositiveButton( "Search", null )
                .create();

        shelterSearch.show();

        shelterSearch.getButton( DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Check if shelterNames contains the searched item, scroll to item
                 */

                shelterSearch.dismiss();

                String searched = shelterBox.getText().toString();

                if (searched.isEmpty() || !Arrays.asList(shelterNames).contains(searched)) {
                    Snackbar.make(root, "Shelter Not Found", Snackbar.LENGTH_SHORT).show();
                } else {

                    /**
                     * Get index of item in list, get Y Location of item at that index, scroll to item Y location
                     */

                    ShelterResultAdapter masterAdapter = (ShelterResultAdapter) shelterList.getAdapter();
                    shelterList.smoothScrollToPosition( masterAdapter.getItemIndex( searched ) );

                    Log.i("INDEX", String.valueOf( masterAdapter.getItemIndex( searched ) ));

                }

            }
        });

    }




}
