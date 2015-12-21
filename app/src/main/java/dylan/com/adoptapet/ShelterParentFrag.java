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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
 * Created by dylan on 12/20/15.
 */
public class ShelterParentFrag extends Fragment implements AdapterView.OnItemClickListener {

    public interface NewBookmarkCallback {
        void shelterBookmarkReload();
    }


    protected ListView shelterList;
    protected ProgressBar loader;
    protected TextView noBookmarkText;
    private AlertDialog shelterOptions;

    private NewBookmarkCallback bookmarkCallback;

    protected String[] shelterNames;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        bookmarkCallback = (NewBookmarkCallback) context;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        View v = LayoutInflater.from( getActivity() ).inflate( R.layout.shelter_bookmarks_layout, parent, false );

        shelterList = (ListView) v.findViewById( R.id.shelterList );
        loader = (ProgressBar) v.findViewById( R.id.loader );
        noBookmarkText = (TextView) v.findViewById( R.id.noBookmarks );

        return v;
    }

    public void initSearchDialog() {

        /**
         * Search for shelters
         * When user selects the name of shelter from the list, get the index of the shleter in the list, smootheScroll to it
         */

        if ( shelterNames != null && shelterNames.length > 0 ) {

            View dialogLayout = LayoutInflater.from(getActivity()).inflate(R.layout.shelter_search, null);
            final AutoCompleteTextView shelterBox = (AutoCompleteTextView) dialogLayout.findViewById(R.id.searchBox);

            ArrayAdapter<String> items = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, shelterNames);
            shelterBox.setAdapter(items);

            final AlertDialog shelterSearch = new AlertDialog.Builder(getActivity())
                    .setCustomTitle(LayoutInflater.from(getActivity()).inflate(R.layout.shelter_title, null))
                    .setView(dialogLayout)
                    .setPositiveButton("Search", null)
                    .create();

            shelterSearch.show();

            shelterSearch.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public void onItemClick(AdapterView parent, View item, int which, long id) {

        final ShelterResult selectedShelter = (ShelterResult) shelterList.getAdapter().getItem(which);
        final ArrayList<String> shelterMenuOptions = new ArrayList<String>();


        Log.i("SHELTER", String.valueOf( selectedShelter.getName() ) );


        if ( !selectedShelter.getPhone().trim().isEmpty() )
            shelterMenuOptions.add( "Call" );

        if ( !selectedShelter.getAddress().trim().isEmpty() || !selectedShelter.getState().trim().isEmpty() || !selectedShelter.getCity().trim().isEmpty() )
            shelterMenuOptions.add( "Map" );

        if ( !selectedShelter.getEmail().trim().isEmpty() )
            shelterMenuOptions.add( "Email" );

        shelterMenuOptions.add("Animals");

        shelterMenuOptions.add( selectedShelter.isBookmarked() ? "Remove From Bookmarks" : "Bookmark");

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

                                addShelterToBookmarks( selectedShelter );

                                break;

                            case "Remove From Bookmarks" :

                                removeShelterFromBookmarks( selectedShelter );

                                break;

                        }

                    }
                })
                .create();

        shelterOptions.show();

    }


    protected boolean isBookmarked( ShelterResult currentShelter ) {
        SQLiteDatabase readable = new ShelterBookmarkDb( getActivity() ).getReadableDatabase();
        Cursor results = readable.rawQuery( "SELECT shelterId FROM " + ShelterBookmarkDb.table_name, null, null ); //TODO:: Speed up by adding where clause for ID

        boolean bookmarked = false;

        if ( results.getCount() > 0 ) {

            results.moveToPosition(-1);

            while( results.moveToNext() ) {
                String shelterId = results.getString( results.getColumnIndex( "shelterId" ) );
                if ( shelterId.equals( currentShelter.getId() ) ) {
                    bookmarked = true;
                    break;
                }
            }

        }

        readable.close();
        results.close();

        return bookmarked;
    }

    private void addShelterToBookmarks( ShelterResult shelter ) {
        //TODO:: Insert local data, start background job to get remote data
        //TODO::Once that background job comes back, use the UPDATE clause to insert them into the matching shelterid row

        ContentValues vals = new ContentValues(  );
        vals.put( ShelterBookmarkDb.id_col, shelter.getId() );
        vals.put( ShelterBookmarkDb.name_col, shelter.getName() );
        vals.put( ShelterBookmarkDb.city_col, shelter.getCity() );
        vals.put( ShelterBookmarkDb.state_col, shelter.getState() );
        vals.put( ShelterBookmarkDb.country_col, shelter.getCountry() );
        vals.put( ShelterBookmarkDb.address_col, shelter.getAddress() );
        vals.put( ShelterBookmarkDb.phone_col, shelter.getPhone() );
        vals.put( ShelterBookmarkDb.email_col, shelter.getEmail() );
        vals.put( ShelterBookmarkDb.photos_col, shelter.getPhotos().toString() );

        SQLiteDatabase writeable = new ShelterBookmarkDb( getActivity() ).getWritableDatabase();
        writeable.insert( ShelterBookmarkDb.table_name, null, vals );

        writeable.close();

        Intent grabService = new Intent( getActivity(), GrabShelterAnimalsIds.class );
        grabService.putExtra( "shelterId", shelter.getId() );
        getActivity().startService(grabService);

        shelter.setBookmarked(true);
        bookmarkCallback.shelterBookmarkReload();

    }

    private void removeShelterFromBookmarks( ShelterResult shelter ) {

        SQLiteDatabase writeable = new ShelterBookmarkDb( getActivity() ).getReadableDatabase();

        writeable.delete(ShelterBookmarkDb.table_name, "shelterId = ?", new String[]{shelter.getId()});

        shelter.setBookmarked(false);

        writeable.close();

        bookmarkCallback.shelterBookmarkReload();

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

            ContentValues vals = new ContentValues();
            vals.put( ShelterBookmarkDb.pet_ids_col, petIds.toString() );

            writeable.update(ShelterBookmarkDb.table_name, vals, "shelterId = ?", new String[]{shelterId});
            writeable.close();
        }

    }

}
