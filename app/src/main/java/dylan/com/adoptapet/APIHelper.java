package dylan.com.adoptapet;

import android.content.Context;
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

import dylan.com.adoptapet.util.DistanceUtil;

/**
 * Created by dylan on 11/1/15.
 */
public class APIHelper {

    public interface Callback {
        void getResults( ArrayList<PetResult> results );
    }

    public interface SheltersCallback {
        void getShelterResults( ArrayList<ShelterResult> results );
    }


    public static String lastOffset = null;

    public static void makeRequest( final Callback callback, final String clientZip, final Handler h, final JSONObject criteria ) {


        OkHttpClient client = new OkHttpClient();
        /**
         * Make request with object as string,
         */

        RequestBody body = RequestBody.create( MediaType.parse("text/plain"), criteria.toString() );

        Request request = new Request.Builder()
                .post( body )
                .url( "http://104.236.15.47/AdoptAPetAPI/testScript.php" )
                .build();
        client.newCall( request ).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String results = response.body().string();
                parseResults(results, clientZip, h, callback);
            }
        });

    }

    public static void makeFavoritesRequest( final Callback callback, final String location, JSONArray ids, final Handler h ) {

        JSONObject requestInfo = new JSONObject();

        try {

            requestInfo.put( "location", location );
            requestInfo.put( "ids", ids );

        } catch ( JSONException e ) {
            throw new RuntimeException( e.getMessage() );
        }


        OkHttpClient httpClient = new OkHttpClient();

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), requestInfo.toString());

        Request r  = new Request.Builder()
                .post( body )
                .url( "http://104.236.15.47/AdoptAPetAPI/favoriteInit.php" )
                .build();

        httpClient.newCall( r ).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String result = response.body().string();
                parseResults( result, location, h, callback );
            }
        });

    }

    public static void getShelters( String location, SheltersCallback callback, Handler h ) {

        OkHttpClient httpClient = new OkHttpClient();

        JSONObject requestObject;

        try {
            requestObject = new JSONObject();
            requestObject.put( "location", location );
        } catch ( JSONException e ) {
            throw new RuntimeException( e.getMessage() );
        }

        RequestBody body = RequestBody.create( MediaType.parse( "text/plain" ), requestObject.toString() );
        Request req = new Request.Builder()
                .post( body )
                .url( "http://104.236.15.47/AdoptAPetAPI/shelterInit.php" )
                .build();

        httpClient.newCall( req ).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });

    }

    private static void parseResults( String result, String clientLocation, final Handler h, final Callback callback ) {


        final ArrayList<PetResult> results = new ArrayList<PetResult>();

        if ( !result.isEmpty() ) {

            try {

                JSONObject resultObject = new JSONObject( result );

                JSONArray petResults = resultObject.getJSONArray( "masterItems" );

                if ( !resultObject.isNull( "lastOffset" )) {
                    lastOffset = resultObject.getJSONObject("lastOffset").getString("0");
                }

                for (int i = 0; i < petResults.length(); i++) {
                    JSONObject pet = petResults.getJSONObject(i);

                    PetResult petResult = new PetResult()
                            .setName(pet.getString("name"))
                            .setId(pet.getJSONObject("id").getString( "0" ))
                            .setAnimalType(pet.getString("animalType"))
                            .setBreed(pet.getJSONArray("breed"))
                            .setIsMix(pet.getString("isMix").equals("true"))
                            .setAge(pet.getString("age"))
                            .setSex(pet.getString("sex"))
                            .setSize(pet.getString("size"))
                            .setExtras(pet.getJSONArray("extras"))
                            .setDescription(pet.getString("description"))
                            .setPhotos(pet.getJSONArray("photos"))
                            .setContactInfo(pet.getJSONObject("contactInfo"));

                    //petResult.setDistanceFromClient( DistanceUtil.zipDistance( context, petResult.getZip(), clientLocation ) );
                    petResult.setDistanceFromClient( "5" ); //TODO:: INSTEAD OF DOING DISTANCE CALCULATION, JUST SHOW STATE EX: MA, NH, ETC

                    results.add( petResult );
                }

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        callback.getResults(results);
                    }
                };

                h.post(r);

            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }

        } else {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    callback.getResults( null );
                }
            };
            h.post( r );
        }
    }


}
