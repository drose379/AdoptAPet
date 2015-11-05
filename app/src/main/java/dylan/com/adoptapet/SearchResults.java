package dylan.com.adoptapet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dylan on 11/1/15.
 */
public class SearchResults extends AppCompatActivity implements APIHelper.Callback {

    private AlertDialog loadingDialog;
    private ListView resultList;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.search_results);

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
    public void getResults( ArrayList<PetResult> results ) {
        loadingDialog.dismiss();
        PetResultAdapter adapter = new PetResultAdapter( this, results );

        Log.i("RESULT", String.valueOf( results.size() ) );
        Log.i("ADAPTERRESULT", String.valueOf( adapter.getCount() ) );
        resultList.setAdapter( adapter );
    }


}
