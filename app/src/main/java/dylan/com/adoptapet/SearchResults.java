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
public class SearchResults extends AppCompatActivity implements APIHelper.Callback, View.OnClickListener, AdapterView.OnItemClickListener,
                                                                PetResultAdapter.Callback{

    private AlertDialog loadingDialog;
    private ListView resultList;
    private ImageView noResultsImage;
    private TextView noResultText;

    private JSONObject searchItems;

    private BroadcastReceiver loadMore;
    private PetResultAdapter resultAdapter;

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

                searchItems = new JSONObject( i.getStringExtra( "searchItems" ) );
                APIHelper.makeRequest( this, searchItems.getString( "location" ),  new Handler(), searchItems);

            } catch ( JSONException e ) {
                 throw new RuntimeException( e.getMessage() );
            }

        }
    }

    @Override
    public void loadMore() {

        try {
            searchItems.put( "offset", APIHelper.lastOffset );
            APIHelper.makeRequest( SearchResults.this, searchItems.getString( "location" ),new Handler(), searchItems );
        } catch ( JSONException e ) {
            throw new RuntimeException( e.getMessage() );
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
                if ( resultAdapter == null ) {
                    resultAdapter = new PetResultAdapter( this, true, results );
                    resultList.setAdapter( resultAdapter );
                } else {

                    int index = resultList.getFirstVisiblePosition() + 1;
                    int top = resultList.getTop();

                    Log.i("INDEX", String.valueOf( index ));

                    resultAdapter.updateData( results );
                    resultAdapter.notifyDataSetChanged();

                    resultList.setSelection( index );

                }

                resultList.setVisibility( View.VISIBLE );

                noResultsImage.setVisibility( View.GONE );
                noResultText.setVisibility( View.GONE );
            } else if ( APIHelper.lastOffset.equals( "50" ) ) {

                Log.i("OFFSET", APIHelper.lastOffset);

                badLocation = false;
                resultList.setVisibility( View.GONE );
                noResultsImage.setVisibility( View.VISIBLE );
                noResultText.setVisibility( View.VISIBLE );

            } else {
                /**
                 * TODO::Tried to load more, but no more results, need to stop trying to load more
                 * TODO:: Implement functionality for method below
                 *
                 * If no results are found, offer options to expand results based on what the user searched, If searched for Large, offer button to search for XL and M
                 */

                resultAdapter.stopLoadingMore();
            }

        } else {

            finish();
            badLocation = true;

        }

    }


}
