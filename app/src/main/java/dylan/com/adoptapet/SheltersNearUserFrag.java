package dylan.com.adoptapet;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dylan on 12/15/15.
 */
public class SheltersNearUserFrag extends Fragment implements AdapterView.OnItemClickListener, APIHelper.SheltersCallback {

    private ListView shelterList;
    private ProgressBar loader;
    private AlertDialog shelterOptions;

    private String location;
    private String[] shelterNames;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {
        View v = LayoutInflater.from( getActivity() ).inflate( R.layout.shelter_near_user_layout, parent, false );

        shelterList = (ListView) v.findViewById( R.id.shelterList );
        loader = (ProgressBar) v.findViewById( R.id.loader );

        checkLocation();

        return v;
    }

    private void checkLocation() {

        SQLiteDatabase readable = new ZipDBHelper( getActivity() ).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT zip FROM " + ZipDBHelper.table_name, null, null);

        if ( result.getCount() == 1 ) {

            grabShelters();

        } else {

            final View layout = LayoutInflater.from( getActivity() ).inflate( R.layout.location_save_dialog,null );

            final AlertDialog getLocation = new AlertDialog.Builder( getActivity() )
                    .setCustomTitle(LayoutInflater.from( getActivity() ).inflate(R.layout.location_title, null))
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

                        SQLiteDatabase writeable = new ZipDBHelper( getActivity() ).getWritableDatabase();
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

        SQLiteDatabase readable = new ZipDBHelper( getActivity() ).getReadableDatabase();
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

            ShelterResultAdapter adapter = new ShelterResultAdapter(getActivity(), results);
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

        shelterMenuOptions.add("Animals");

        shelterMenuOptions.add( isBookmarked( selectedShelter ) ? "Remove From Bookmarks" : "Bookmark" );

        String[] optionsFinal = new String[shelterMenuOptions.size()];


        shelterOptions = new AlertDialog.Builder( getActivity() )
                .setCustomTitle(LayoutInflater.from( getActivity() ).inflate(R.layout.shelter_title, null))
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
                                startActivity(mail);

                                break;
                            case "Animals":

                                shelterOptions.dismiss();

                                Intent shelterAnimals = new Intent( getActivity() , ShelterAnimalResults.class);
                                shelterAnimals.putExtra("shelterId", selectedShelter.getId());
                                shelterAnimals.putExtra("shelterName", selectedShelter.getName());

                                if (selectedShelter.getId() != null && !selectedShelter.getId().isEmpty()) {
                                    startActivity(shelterAnimals);
                                } else {
                                    Snackbar.make( getView(), "Shelter Error", Snackbar.LENGTH_SHORT).show();
                                }


                                break;
                            case "Bookmark" :

                                //TODO:: Add shelter to bookmarks
                                addShelterToBookmarks( selectedShelter );

                                break;

                            case "Remove From Bookmarks" :

                                //TODO:: Remove shelter from bookmarks
                                removeShelterFromBookmarks( selectedShelter );

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

        View dialogLayout = LayoutInflater.from( getActivity() ).inflate( R.layout.shelter_search, null );
        final AutoCompleteTextView shelterBox = (AutoCompleteTextView) dialogLayout.findViewById( R.id.searchBox );

        ArrayAdapter<String> items = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_dropdown_item_1line, shelterNames );
        shelterBox.setAdapter(items);

        final AlertDialog shelterSearch = new AlertDialog.Builder( getActivity() )
                .setCustomTitle( LayoutInflater.from( getActivity() ).inflate( R.layout.shelter_title, null ) )
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
                    Snackbar.make(getView(), "Shelter Not Found", Snackbar.LENGTH_SHORT).show();
                } else {

                    /**
                     * Get index of item in list, get Y Location of item at that index, scroll to item Y location
                     */

                    ShelterResultAdapter masterAdapter = (ShelterResultAdapter) shelterList.getAdapter();
                    shelterList.smoothScrollToPosition(masterAdapter.getItemIndex(searched));

                    Log.i("INDEX", String.valueOf(masterAdapter.getItemIndex(searched)));

                }

            }
        });

    }

    private boolean isBookmarked( ShelterResult currentShelter ) {
        SQLiteDatabase readable = new ShelterBookmarkDb( getActivity() ).getReadableDatabase();
        Cursor results = readable.rawQuery( "SELECT shelterId FROM " + ShelterBookmarkDb.table_name, null, null ); //TODO:: Speed up by adding where clause for ID

        boolean bookmarked = false;

        if ( results.getCount() > 0 ) {

            results.moveToFirst();
            while( results.moveToNext() ) {
                String shelterId = results.getString( results.getColumnIndex( "shelterId" ) );
                if ( shelterId.equals( currentShelter.getId() ) ) {
                    bookmarked = true;
                    break;
                }
            }

        }

        readable.close();

        return bookmarked;
    }

    private void addShelterToBookmarks( ShelterResult shelter ) {
        //TODO:: Insert just the sheltersId into the table, START THE BACKGROUND JOB THAT WILL GRAB ALL PET IDs for this shelter
        //TODO::Once that background job comes back, use the UPDATE clause to insert them into the matching shelterid row
        //TODO:: Also need to add rest of shelter data to the table

        ContentValues vals = new ContentValues(  );
        vals.put( ShelterBookmarkDb.id_col, shelter.getId() );
        vals.put( ShelterBookmarkDb.location_col, shelter.generateLocationText() );
        vals.put( ShelterBookmarkDb.phone_col, shelter.getPhone() );
        vals.put( ShelterBookmarkDb.email_col, shelter.getEmail() );

        SQLiteDatabase writeable = new ShelterBookmarkDb( getActivity() ).getWritableDatabase();
        writeable.insert( ShelterBookmarkDb.table_name, null, vals );

        writeable.close();

        //TODO:: Call service that will grab the current animals for this
        Intent grabService = new Intent( getActivity(), GrabShelterAnimalsIds.class );
        grabService.putExtra( "shelterId", shelter.getId() );
        getActivity().startService(grabService);

        Log.i("BOOKMARK", "Called bookmark service");

    }

    private void removeShelterFromBookmarks( ShelterResult shelter ) {}

    private void updateShelterPetIds( String shelterId, JSONArray petIDs ) {
        //TODO:: Use a UPDATE clause to update where shelterID = :shelterID
    }


    /**
     * Class extends IntentService, used to grab pet Ids for shelter being bookmarked
     */
    public static class GrabShelterAnimalsIds extends IntentService {

        private String shelterId;

        public GrabShelterAnimalsIds() {
            super( "Name" );
        }

        public GrabShelterAnimalsIds( String name ) {
            super( name );
        }

        @Override
        public void onHandleIntent( Intent intent ) {
            //TODO:: Make request to grab all the pet Ids for the given shelter, once returned, save them with updateShelterPetIds

            this.shelterId = intent.getStringExtra( "shelterId" );

            Log.i("BOOKMARK", "MADE CALL");

            OkHttpClient httpClient = new OkHttpClient();
            RequestBody body = RequestBody.create( MediaType.parse("text/plain"), shelterId );
            Request req = new Request.Builder()
                    .post( body )
                    .url( "http://104.236.15.47/AdoptAPetAPI/getShelterPetIds.php" )
                    .build();
            httpClient.newCall( req ).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {

                    try {
                        updateShelterPetIds( new JSONArray( response.body().string() ) );
                    } catch ( JSONException e ) {
                        throw new RuntimeException( e );
                    }

                }
            });

        }


        private void updateShelterPetIds( JSONArray petIds ) {
            SQLiteDatabase writeable = new ShelterBookmarkDb( this ).getWritableDatabase();



            writeable.close();
        }

    }


}
