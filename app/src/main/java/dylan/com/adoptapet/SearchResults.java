package dylan.com.adoptapet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
public class SearchResults extends AppCompatActivity implements APIHelper.Callback, View.OnClickListener, AdapterView.OnItemClickListener{

    private AlertDialog loadingDialog;
    private ListView resultList;
    private ImageView noResultsImage;
    private TextView noResultText;

    private BroadcastReceiver loadMore;

    public static boolean badLocation = false;

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
        toolbarBack.setOnClickListener(this);

        resultList = (ListView) findViewById( R.id.petResultsList );
        resultList.setOnItemClickListener(this);

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

        initBroadcastReceiver();

    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance( this ).unregisterReceiver( loadMore );
    }

    public void initBroadcastReceiver() {

        if ( loadMore == null ) {

            loadMore = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i("LOAD_MORE", "LOAD_MORE_RECEIVED" );
                }
            };

            IntentFilter loadMoreFilter = new IntentFilter( PetResultAdapter.LOAD_MORE_PETS );
            LocalBroadcastManager.getInstance( this ).registerReceiver( loadMore, loadMoreFilter );

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
    public void onItemClick( AdapterView parent, View view, int item, long id ) {

        if ( item < parent.getAdapter().getCount() - 1 ) {
            PetResult itemClicked = (PetResult) resultList.getItemAtPosition( item );
            Intent petDetail = new Intent(this, PetResultDetail.class);
            petDetail.putExtra("pet", itemClicked);
            startActivity(petDetail);
        }
    }


    @Override
    public void getResults( ArrayList<PetResult> results ) {
        loadingDialog.dismiss();
        if ( results != null ) {
            if ( results.size() > 0 ) {
                badLocation = false;
                PetResultAdapter adapter = new PetResultAdapter( this, results );
                resultList.setVisibility( View.VISIBLE );
                resultList.setAdapter( adapter );

                noResultsImage.setVisibility( View.GONE );
                noResultText.setVisibility( View.GONE );
            } else {
                badLocation = false;
                resultList.setVisibility( View.GONE );
                noResultsImage.setVisibility( View.VISIBLE );
                noResultsImage.setVisibility( View.VISIBLE );
            }

        } else {

            finish();
            badLocation = true;

        }

    }


}
