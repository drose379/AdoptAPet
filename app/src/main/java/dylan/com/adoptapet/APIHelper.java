package dylan.com.adoptapet;

import android.os.Handler;
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

    public static void makeRequest( String animalType, final Callback callback , final Handler h, JSONObject criteria ) {
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
                parseResults( response.body().string(), h, callback );
            }
        });

    }

    private static void parseResults( String resultArray, final Handler h, final Callback callback ) {


        final ArrayList<PetResult> results = new ArrayList<PetResult>();

        try {
            JSONArray petResults = new JSONArray( resultArray );

            for ( int i = 0; i < petResults.length(); i++ ) {
                JSONObject pet = petResults.getJSONObject( i );

                PetResult petResult = new PetResult()
                        .setName( pet.getString( "name" ) )
                        .setId( pet.getString( "id" ) )
                        .setAnimalType( pet.getString( "animalType" ) )
                        .setBreed( pet.getJSONArray( "breed" ) )
                        .setIsMix( pet.getString( "isMix" ).equals( "true" ) )
                        .setAge( pet.getString( "age" ) )
                        .setSex( pet.getString( "sex" ) )
                        .setSize( pet.getString( "size" ) )
                        .setExtras( pet.getJSONArray( "extras" ) )
                        .setDescription( pet.getString( "description" ) )
                        .setPhotos( pet.getJSONArray( "photos" ) )
                        .setContactInfo( pet.getJSONObject( "contactInfo" ) );

                results.add( petResult );
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    callback.getResults( results );
                }
            };

            h.post( r );

        } catch ( JSONException e) {
            throw new RuntimeException( e.getMessage() );
        }

    }

}
