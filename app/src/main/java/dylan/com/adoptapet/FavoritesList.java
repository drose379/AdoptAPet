package dylan.com.adoptapet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dylan.com.adoptapet.R;

/**
 * Created by Dylan Rose on 11/22/15.
 */
public class FavoritesList extends AppCompatActivity implements View.OnClickListener, APIHelper.Callback {

    private ProgressBar loader;
    private ListView favoritesList;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.favorites_list_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView backButton = (ImageView) toolbar.findViewById(R.id.toolbarBackButton);

        loader = (ProgressBar) findViewById( R.id.loader );
        favoritesList = (ListView) findViewById( R.id.favoritesList );


        toolbarTitle.setText("My Favorites");
        backButton.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        loadFavorites();
    }




    public void loadFavorites() {

        final ArrayList<PetResult> favorites = new ArrayList<>();
        /**
         * Loop over favorites table, add to the favorites ArrayList, pass to adapter, set Loader to GONE, set Listview to VISIBLE
         * Check if lastUpdated is more then a day, if yes, send to the API to get updated, when they come back, update the individual items in the ArrayList, notifyDataUpdate
         */

        SQLiteDatabase readable = new FavoritesDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT * FROM " + FavoritesDBHelper.table_name, null, null );

        if ( result.getCount() > 0 ) {
            /**
             * Get results, add each to favorites ArrayList
             */

            result.moveToPosition(-1);

            while( result.moveToNext() ) {

                /**
                 * BUG: Not grabbing first value in Cursor
                 */

                String id = result.getString( result.getColumnIndex( FavoritesDBHelper.id_col ) );
                String type = result.getString( result.getColumnIndex( FavoritesDBHelper.type_col ) );
                String name = result.getString( result.getColumnIndex( FavoritesDBHelper.name_col ) );
                String age = result.getString( result.getColumnIndex( FavoritesDBHelper.age_col ) );
                String sex = result.getString( result.getColumnIndex( FavoritesDBHelper.sex_col ) );
                String size = result.getString( result.getColumnIndex( FavoritesDBHelper.size_col ) );
                String breedString = result.getString( result.getColumnIndex( FavoritesDBHelper.breed_col ) );
                String description = result.getString( result.getColumnIndex( FavoritesDBHelper.description_col ) );
                String photo = result.getString( result.getColumnIndex( FavoritesDBHelper.photo_col ) );
                JSONObject contactInfo;
                JSONArray photos;
                try {
                    contactInfo = new JSONObject( result.getString( result.getColumnIndex( FavoritesDBHelper.contactInfo_col ) ) );
                    photos = new JSONArray( );
                    JSONArray breeds = new JSONArray( breedString );

                    photos.put( photo );

                    favorites.add(new PetResult()
                                    .setId(id)
                                    .setAnimalType(type)
                                    .setName(name)
                                    .setAge(age)
                                    .setSize(size)
                                    .setSex(sex)
                                    .setBreed(breeds)
                                    .setDescription(description)
                                    .setContactInfo(contactInfo)
                                    .setPhotos(photos)
                    );

                } catch ( JSONException e ) {
                    throw new RuntimeException( e.getMessage() );
                }

            }


        } else {
            /**
             * Show message that user has not favorited any animals, if they have a location saved, give button to make a search
             */
        }


        loader.setVisibility( View.GONE );
        favoritesList.setVisibility( View.VISIBLE );
        PetResultAdapter adapter = new PetResultAdapter( this, false, favorites );
        favoritesList.setAdapter( adapter );

        favoritesList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView parent, View view, int which, long id ) {
                Intent detail = new Intent( FavoritesList.this ,PetResultDetail.class );
                detail.putExtra( "pet", favorites.get( which ) );
                startActivity( detail );
            }
        });


    }



    public String grabLocation() {
        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT zip FROM " + ZipDBHelper.table_name, null, null  );

        result.moveToFirst();

        String location = result.getString( result.getColumnIndex( "zip" ) );

        readable.close();

        return location;
    }

    @Override
    public void getResults( ArrayList<PetResult> results ) {
        /**
         * Only used to update favorites that were last updated more then 1 day ago, check for each one
         */
    }



    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.toolbarBackButton :

                finish();

                break;

        }
    }




}
