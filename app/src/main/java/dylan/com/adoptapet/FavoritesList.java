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

    private AlertDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.favorites_list_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView backButton = (ImageView) toolbar.findViewById(R.id.toolbarBackButton);


        toolbarTitle.setText("My Favorites");
        backButton.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        loadFavorites();
    }




    public void loadFavorites() {
        showLoadingDialog();

        JSONArray favoriteIds = new JSONArray();

        SQLiteDatabase readable = new FavoritesDBHelper( this ).getReadableDatabase();
        Cursor results = readable.rawQuery("SELECT id FROM " + FavoritesDBHelper.table_name, null, null);

        if ( results.getCount() > 0 ) {

            results.moveToPosition( -1 );

            while ( results.moveToNext() ) {
                favoriteIds.put( results.getString( results.getColumnIndex( "id" ) ) );
            }


            APIHelper.makeFavoritesRequest( this, grabLocation(), favoriteIds, new Handler( ) );


        } else {
            /**
             * Show a "No Favorites" notice and set ListView to GONE
             */
        }

        readable.close();

    }

    @Override
    public void getResults( final ArrayList<PetResult> results ) {

        ListView favoritesList = (ListView) findViewById( R.id.favoritesList );
        PetResultAdapter adapter = new PetResultAdapter( this, false, results );

        favoritesList.setAdapter( adapter );

        favoritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detail = new Intent( FavoritesList.this, PetResultDetail.class );
                detail.putExtra( "pet", results.get( position ) );
                startActivity( detail );
            }
        });

        loadingDialog.dismiss();
    }

    public String grabLocation() {
        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT zip FROM " + ZipDBHelper.table_name, null, null  );

        result.moveToFirst();

        String location = result.getString( result.getColumnIndex( "zip" ) );

        readable.close();

        return location;
    }

    public void showLoadingDialog() {
        loadingDialog = new AlertDialog.Builder( this )
                .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.loading_title, null ) )
                .setMessage( "Grabbing your favorites!" )
                .create();

        loadingDialog.show();
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
