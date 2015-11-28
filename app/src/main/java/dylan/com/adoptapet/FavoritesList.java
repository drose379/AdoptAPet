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

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.favorites_list_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView backButton = (ImageView) toolbar.findViewById(R.id.toolbarBackButton);

        loader = (ProgressBar) findViewById( R.id.loader );


        toolbarTitle.setText("My Favorites");
        backButton.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        loadFavorites();
    }




    public void loadFavorites() {

        ArrayList<PetResult> favorites = new ArrayList<>();
        /**
         * Loop over favorites table, add to the favorites ArrayList, pass to adapter, set Loader to GONE, set Listview to VISIBLE
         * Check if lastUpdated is more then a day, if yes, send to the API to get updated, when they come back, update the individual items in the ArrayList, notifyDataUpdate
         */

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
