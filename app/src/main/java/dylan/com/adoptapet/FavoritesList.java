package dylan.com.adoptapet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Dylan Rose on 11/22/15.
 */
public class FavoritesList extends AppCompatActivity implements View.OnClickListener, APIHelper.Callback {

    private ProgressBar loader;
    private ListView favoritesList;

    private TextView noFavorites;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.favorites_list_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setTitle( "My Favorites" );

        loader = (ProgressBar) findViewById(R.id.loader);
        favoritesList = (ListView) findViewById(R.id.favoritesList);
        noFavorites = (TextView) findViewById(R.id.noFavorites);

    }

    @Override
    public boolean onOptionsItemSelected( android.view.MenuItem item ) {
        switch( item.getItemId() ) {
            case android.R.id.home :
                finish();
                return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onResume() {
        super.onResume();

        loadFavorites();
    }


    private void loadFavorites() {

        final ArrayList<PetResult> favorites = new ArrayList<>();
        /**
         * Loop over favorites table, add to the favorites ArrayList, pass to adapter, set Loader to GONE, set Listview to VISIBLE
         * Check if lastUpdated is more then a day, if yes, send to the API to get updated, when they come back, update the individual items in the ArrayList, notifyDataUpdate
         */

        SQLiteDatabase readable = new FavoritesDBHelper(this).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT * FROM " + FavoritesDBHelper.table_name, null, null);

        if (result.getCount() > 0) {
            /**
             * Get results, add each to favorites ArrayList
             */

            result.moveToPosition(-1);

            while (result.moveToNext()) {

                /**
                 * BUG: Not grabbing first value in Cursor
                 */

                String id = result.getString(result.getColumnIndex(FavoritesDBHelper.id_col));
                String type = result.getString(result.getColumnIndex(FavoritesDBHelper.type_col));
                String name = result.getString(result.getColumnIndex(FavoritesDBHelper.name_col));
                String age = result.getString(result.getColumnIndex(FavoritesDBHelper.age_col));
                String sex = result.getString(result.getColumnIndex(FavoritesDBHelper.sex_col));
                String size = result.getString(result.getColumnIndex(FavoritesDBHelper.size_col));
                String breedString = result.getString(result.getColumnIndex(FavoritesDBHelper.breed_col));
                String description = result.getString(result.getColumnIndex(FavoritesDBHelper.description_col));
                String photos = result.getString(result.getColumnIndex(FavoritesDBHelper.photo_col));
                JSONObject contactInfo;
                JSONArray photoArray;
                try {
                    contactInfo = new JSONObject(result.getString(result.getColumnIndex(FavoritesDBHelper.contactInfo_col)));
                    photoArray = new JSONArray( photos );
                    JSONArray breeds = new JSONArray(breedString);

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
                                    .setPhotos(photoArray)
                    );

                } catch (JSONException e) {
                    throw new RuntimeException(e.getMessage());
                }

            }

            initAnimalTypeSpinner(favorites);

            loader.setVisibility(View.GONE);
            favoritesList.setVisibility(View.VISIBLE);
            PetResultAdapter adapter = new PetResultAdapter(this, false, favorites);
            favoritesList.setAdapter(adapter);

            favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView parent, View view, int which, long id) {
                    Intent detail = new Intent(FavoritesList.this, PetResultDetail.class);
                    detail.putExtra("pet", favorites.get(which));
                    startActivity(detail);
                }
            });

        } else {
            loader.setVisibility(View.GONE);
            favoritesList.setVisibility(View.GONE);
            noFavorites.setVisibility(View.VISIBLE);
        }

    }


    private void initAnimalTypeSpinner(ArrayList<PetResult> favorites) {
        /**
         * Looks over types of animals in list, if more then 1, show spinner with types, if only 1, no need for spinner
         */

        final ArrayList<PetResult> favsPermanent = (ArrayList<PetResult>) favorites.clone();

        Spinner typeSelect = (Spinner) findViewById(R.id.typeSelect);


        ArrayList<String> types = new ArrayList<>();

        types.add("Any");

        for (PetResult item : favorites) {
            if (!types.contains(item.getType())) {
                types.add(item.getType());
            }
        }

        if (types.size() > 2) {

            final String[] items = new String[types.size()];

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, types.toArray(items));
            typeSelect.setAdapter(adapter);
            typeSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView parent, View v, int which, long id) {

                    /**
                     * Create temp array, pass to adapter, update the items in list
                     */
                    String type = items[which];
                    PetResultAdapter listAdapter = (PetResultAdapter) favoritesList.getAdapter();


                    ArrayList<PetResult> tempItems = new ArrayList<PetResult>();
                    tempItems.clear();

                    for (PetResult item : favsPermanent) {
                        if (item.getType().equals(type)) {
                            tempItems.add(item);
                        }
                        else if ( type.equals( "Any" ) ) {
                            tempItems.add( item );
                        }
                    }

                    listAdapter.updateAnimalType(tempItems);
                    favoritesList.smoothScrollToPosition(0);

                }

                @Override
                public void onNothingSelected (AdapterView parent){}

            });

            typeSelect.setVisibility(View.VISIBLE);

        }

        else

        {
            typeSelect.setVisibility(View.GONE);
        }

    }


    @Override
    public void getResults(ArrayList<PetResult> results) {
        /**
         * Only used to update favorites that were last updated more then 1 day ago, check for each one
         */
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbarBackButton:

                finish();

                break;

        }
    }


}
