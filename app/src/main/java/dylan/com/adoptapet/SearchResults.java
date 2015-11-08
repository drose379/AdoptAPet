package dylan.com.adoptapet;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dylan Rose on 11/1/15.
 */
public class SearchResults extends AppCompatActivity implements APIHelper.Callback, View.OnClickListener {

    private AlertDialog loadingDialog;
    private ListView resultList;
    private ImageView noResultsImage;
    private TextView noResultText;

    public static String NO_RESULT = "NO_RESULT";

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.search_results);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView toolbarBack = (ImageView) toolbar.findViewById( R.id.toolbarBackButton );

        noResultsImage = (ImageView) findViewById( R.id.noResultImage );
        noResultText = (TextView) findViewById( R.id.noResultsText );

        toolbarTitle.setText( "Results" );
        toolbarBack.setOnClickListener( this );

        resultList = (ListView) findViewById( R.id.petResultsList );

        Intent i = getIntent();
        if ( i.getStringExtra("searchItems") != null ) {

            loadingDialog = new AlertDialog.Builder( this )
                    .setCustomTitle(LayoutInflater.from( this ).inflate(R.layout.loading_title, null, false))
                    .setMessage("Searching...")
                    .create();
            loadingDialog.show();

            try {

                JSONObject searchItems = new JSONObject( i.getStringExtra( "searchItems" ) );
                APIHelper.makeRequest( this, searchItems.getString( "location" ),  new Handler(), searchItems);

            } catch ( JSONException e ) {
                 throw new RuntimeException( e.getMessage() );
            }

        }

        /**TEST---TEST--GEOCODER TO GO FROM ZIP TO ADDRESS OBJECT-----TEST------TEST-----TEST----TEST---
        Geocoder geoCoder = new Geocoder( this );
        try {
            List<Address> addresses = geoCoder.getFromLocationName( "02356", 1 );
            float[] results = new float[3];
            Location.distanceBetween(  );
        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage() );
        }

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

    @Override
    public void getResults( ArrayList<PetResult> results ) {
        loadingDialog.dismiss();
        if ( results != null ) {
            if ( results.size() > 0 ) {
                PetResultAdapter adapter = new PetResultAdapter( this, results );
                resultList.setVisibility( View.VISIBLE );
                resultList.setAdapter( adapter );

                noResultsImage.setVisibility( View.GONE );
                noResultText.setVisibility( View.GONE );
            } else {
                resultList.setVisibility( View.GONE );
                noResultsImage.setVisibility( View.VISIBLE );
                noResultsImage.setVisibility( View.VISIBLE );
            }

        } else {
            /**
             * Send a broadcast that the DogFragment will listen for, the dog frag will show the "No Results" snackbar on receive of broadcast
             * Call finish after broadcast is called
             * -----> ------> THIS IS NOT WORKING RIGHT NOW!, apparently code will run after finish <----- <-----
             */


            finish();
            Intent noResult = new Intent( NO_RESULT );
            sendBroadcast( noResult );
            Log.i("BROADCAST","SENT BROADCAST");

        }

    }


}
