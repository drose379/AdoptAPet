package dylan.com.adoptapet;

import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Created by dylan on 11/1/15.
 */
public class SearchResults extends AppCompatActivity implements APIHelper.Callback, View.OnClickListener {

    private AlertDialog loadingDialog;
    private ListView resultList;

    public static String NO_RESULT = "NO_RESULT";

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.search_results);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView toolbarBack = (ImageView) toolbar.findViewById( R.id.toolbarBackButton );

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
                APIHelper.makeRequest(searchItems.getString("type"), this, new Handler(), searchItems);

            } catch ( JSONException e ) {
                 throw new RuntimeException( e.getMessage() );
            }

        }

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
            PetResultAdapter adapter = new PetResultAdapter( this, results );
            resultList.setAdapter( adapter );
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
