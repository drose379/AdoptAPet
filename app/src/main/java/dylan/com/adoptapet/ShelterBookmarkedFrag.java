package dylan.com.adoptapet;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by dylan on 12/16/15.
 */
public class ShelterBookmarkedFrag extends ShelterParentFrag {

    private ArrayList<ShelterResult> bookmarkedShelters;
    private ShelterResultAdapter shelterListAdapter;



    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        //TODO:: If there are no bookmarks, show simple black text in middle of screen, (Just like the "No Favorites" text)
        //TODO:: In this list, the search dialog can still work, just need to update its adapter to use only the names of these shelters, do this in loadBookmarkedShelters()
        View v = super.onCreateView( inflater, parent, savedInstance );

        loader.setVisibility( View.GONE );

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadBookmarkedShelters();
    }

    public void reloadBookmarks() {
        //TODO:: Call loadBookmarkedShelters but make sure to update the adapter instead of create a new one, call the adapters update method not new
        loadBookmarkedShelters();
    }

    private void loadBookmarkedShelters() {

        bookmarkedShelters = new ArrayList<>();

        SQLiteDatabase readable = new ShelterBookmarkDb( getActivity() ).getReadableDatabase();
        Cursor results = readable.rawQuery( "SELECT * FROM " + ShelterBookmarkDb.table_name, null );

        if ( results.getCount() > 0 ) {

            noBookmarkText.setVisibility( View.GONE );
            shelterList.setVisibility( View.VISIBLE );

            results.moveToPosition(-1);
            while( results.moveToNext() ) {

                String id = results.getString( results.getColumnIndex( ShelterBookmarkDb.id_col ) );
                String name = results.getString( results.getColumnIndex( ShelterBookmarkDb.name_col ) );
                String city = results.getString( results.getColumnIndex( ShelterBookmarkDb.city_col ) );
                String state = results.getString( results.getColumnIndex( ShelterBookmarkDb.state_col ) );
                String country = results.getString( results.getColumnIndex( ShelterBookmarkDb.country_col ) );
                String address = results.getString( results.getColumnIndex( ShelterBookmarkDb.address_col ) );
                String phone = results.getString( results.getColumnIndex( ShelterBookmarkDb.phone_col ) );
                String email = results.getString( results.getColumnIndex( ShelterBookmarkDb.email_col ) );
                String photos = results.getString( results.getColumnIndex( ShelterBookmarkDb.photos_col ) );



                ShelterResult currentShelter = new ShelterResult()
                        .setId( id )
                        .setName( name )
                        .setCity( city )
                        .setState( state )
                        .setCountry( country )
                        .setAddress( address )
                        .setPhone( phone )
                        .setEmail( email )
                        .setPhotos( photos );

                currentShelter.setBookmarked( isBookmarked( currentShelter ) );

                bookmarkedShelters.add( currentShelter );

            }

            shelterListAdapter = new ShelterResultAdapter( getActivity(), bookmarkedShelters );
            shelterList.setAdapter(shelterListAdapter);
            shelterList.setOnItemClickListener( this );

            shelterNames = new String[bookmarkedShelters.size()];

            for ( int i = 0; i < bookmarkedShelters.size(); i++ ) {
                shelterNames[i] = bookmarkedShelters.get( i ).getName();
            }


        } else {
            shelterList.setAdapter( null );
            initNoBookmarkView();
        }

        results.close();
        readable.close();

    }

    private void initNoBookmarkView() {
        shelterList.setVisibility(View.GONE);
        noBookmarkText.setVisibility(View.VISIBLE);
    }


}
