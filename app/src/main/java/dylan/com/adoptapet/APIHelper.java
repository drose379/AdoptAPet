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

    public interface ShelterNameCallback {
        void getShelterName( String shelterName );
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
                parseResults(results, h, callback);
            }
        });

    }



    public static void getShelters( String location, final SheltersCallback callback, final Handler h ) {

        OkHttpClient httpClient = new OkHttpClient();

        JSONObject requestObject;

        try {
            requestObject = new JSONObject();
            requestObject.put( "location", location );
        } catch ( JSONException e ) {
            requestObject = new JSONObject();
        }

        RequestBody body = RequestBody.create( MediaType.parse("text/plain"), requestObject.toString() );
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
                /**
                 * Need to build a parseShelterResults to create an ArrayList of ShelterResults
                 */
                parseShelterResults(response.body().string(), h, callback);
            }
        });

    }

    public static void makeShelterAnimalsRequest( String shelterId, final Callback callback, final Handler h ) {

        OkHttpClient httpClient = new OkHttpClient();

        RequestBody body = RequestBody.create( MediaType.parse( "text/plain" ), shelterId );
        Request req = new Request.Builder()
                .post( body )
                .url("http://104.236.15.47/AdoptAPetAPI/shelterAnimalInit.php")
                .build();
        httpClient.newCall( req ).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String results = response.body().string();
                parseResults(results, h, callback);
            }
        });

    }

    public static void getShelterName( String id, final ShelterNameCallback callback, final Handler h ) {

        OkHttpClient client = new OkHttpClient();

        String url = "http://104.236.15.47/AdoptAPetAPI/getShelterName.php?id=" + id;

        Request req = new Request.Builder()
                .url( url )
                .build();
        client.newCall( req ).enqueue(new com.squareup.okhttp.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                final String name = response.body().string();

                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        callback.getShelterName( name );
                    }
                };

                h.post( r );

            }
        });

    }

    private static void parseResults( String result, final Handler h, final Callback callback ) {

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
                            .setContactInfo(pet.getJSONObject("contactInfo"))
                            .setShelterId( pet.getString("shelterId") );

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
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        callback.getResults( null );
                    }
                };

                h.post( r );
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


    private static void parseShelterResults( String result, final Handler h, final SheltersCallback callback ) {

        final ArrayList<ShelterResult> results = new ArrayList<ShelterResult>();

        try {

            JSONArray shelters = new JSONArray( result );

            for( int i = 0; i < shelters.length(); i++ ) {

                JSONObject shelter = shelters.getJSONObject( i );

                results.add( new ShelterResult()
                        .setId( shelter.getString( "id" ) )
                        .setName( shelter.getString( "name" ) )
                        .setAddress( shelter.getString( "address" ) )
                        .setCity( shelter.getString( "city" ) )
                        .setState( shelter.getString( "state" ) )
                        .setCountry( shelter.getString( "country" ) )
                        .setPhone( shelter.getString( "phone" ) )
                        .setFax( shelter.getString( "fax" ) )
                        .setEmail( shelter.getString( "email" ) )
                        .setPhotos( shelter.getString( "photos" ) )
                );

            }


        } catch ( JSONException e ) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    callback.getShelterResults( null );
                }
            };

            h.post( r );

            //h.post(callback.getShelterResults( null );
        }

        h.post(new Runnable() {
            @Override
            public void run() {
                callback.getShelterResults(results);
            }
        } );

    }

}
