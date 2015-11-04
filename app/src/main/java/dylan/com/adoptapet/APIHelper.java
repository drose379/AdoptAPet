package dylan.com.adoptapet;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dylan on 11/1/15.
 */
public class APIHelper {

    public interface Callback {
        void getResults( ArrayList<PetResult> results );
    }

    public static void makeRequest( String animalType, final Callback callback , JSONObject criteria ) {
        OkHttpClient client = new OkHttpClient();
        /**
         * Make request with object as string,
         */

        RequestBody body = RequestBody.create( MediaType.parse("text/plain"), criteria.toString() );

        Request request = new Request.Builder()
                .post( body )
                .url( "http://104.236.15.47/AdoptAPetAPI/getPets.php" )
                .build();
        client.newCall( request ).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                parseResults( response.body().string(), callback );
            }
        });

    }

    private static void parseResults( String resultArray, Callback callback ) {

        try {
            JSONArray petResults = new JSONArray( resultArray );
            Log.i("COUNT", String.valueOf( petResults.length() ));
        } catch ( JSONException e) {
            throw new RuntimeException( e.getMessage() );
        }

    }

}
