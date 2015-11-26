package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
public class ShelterList extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, APIHelper.SheltersCallback {

    private RelativeLayout root;
    private ProgressBar loader;
    private ListView shelterList;

    private AlertDialog shelterOptions;

    private String[] shelterNames;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.shelter_list_layout);

        root = (RelativeLayout) findViewById( R.id.root );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        ImageView backButton = (ImageView) toolbar.findViewById(R.id.toolbarBackButton);
        ImageView searchButton = (ImageView) toolbar.findViewById( R.id.searchButton );
        TextView title = (TextView) toolbar.findViewById( R.id.toolbarTitle );

        title.setText( "Shelters Near Me" );
        backButton.setOnClickListener( this );
        searchButton.setOnClickListener( this );

        loader = (ProgressBar) findViewById( R.id.loader );
        shelterList = (ListView) findViewById( R.id.shelterList );

        checkLocation();
    }

    public void checkLocation() {

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

        String location = result.getString(result.getColumnIndex("zip"));

        readable.close();

        APIHelper.getShelters(location, this, new Handler());

    }

    @Override
    public void getShelterResults( ArrayList<ShelterResult> results ) {


        shelterNames = new String[results.size()];

        for (int i = 0; i < results.size(); i++) {
            shelterNames[i] = results.get(i).getName();
        }

        loader.setVisibility(View.GONE);
        shelterList.setVisibility(View.VISIBLE);

        ShelterResultAdapter adapter = new ShelterResultAdapter(ShelterList.this, results);
        shelterList.setAdapter(adapter);
        shelterList.setOnItemClickListener( this );


    }

    /**
     * On item click for the master list
     */
    @Override
    public void onItemClick(AdapterView parent, View item, int which, long id) {

        final ShelterResult selectedShelter = (ShelterResult) shelterList.getAdapter().getItem(which);

        shelterOptions = new AlertDialog.Builder(ShelterList.this)
                .setCustomTitle(LayoutInflater.from(ShelterList.this).inflate(R.layout.shelter_title, null))
                .setItems(new String[]{"Call", "Map", "Email", "See Animals"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {

                            case 0:

                                break;
                            case 1:

                                break;
                            case 2:

                                break;
                            case 3:

                                Intent shelterAnimals = new Intent(ShelterList.this, ShelterAnimalResults.class);
                                shelterAnimals.putExtra("shelterId", selectedShelter.getId());
                                shelterAnimals.putExtra("shelterName", selectedShelter.getName());
                                startActivity(shelterAnimals);

                                shelterOptions.dismiss();

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


    @Override
    public void onClick( View v) {
        switch ( v.getId() ) {
            case R.id.toolbarBackButton :
                finish();
                break;

            case R.id.searchButton :

                initSearchDialog();

                break;

            }
        }

}
