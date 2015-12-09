package dylan.com.adoptapet;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dylan on 11/22/15.
 */
public class FeaturedPetController implements APIHelper.Callback {

    private static FeaturedPetController featuredPetController;

    public static final String GET_FEATURED = "GET_FEATURED";

    private Context context;
    private int currentItem = -1;
    private ArrayList<PetResult> featuredItems;

    private String location;
    private String type;


    public static FeaturedPetController getInstance( Context context ) {
        featuredPetController = featuredPetController == null ? new FeaturedPetController( context ) : featuredPetController;
        return featuredPetController;
    }

    private FeaturedPetController( Context context ) {
        this.context = context;
        /**
         * Init featured items grab
         * Have a internal private counter of the items, keep track of which item is showing
         * Have a next() method to get the next item in the que
         */

        featuredItems = new ArrayList<PetResult>();

    }

    public void getFeatured( String location, String type ) {

        this.location = location;
        this.type = type;

        try {

            JSONObject requestInfo = new JSONObject();
            requestInfo.put( "location", location );
            requestInfo.put( "age", new JSONArray().put( "senior" ) );
            requestInfo.put( "type", "dog" );

            APIHelper.makeRequest( this, location, new Handler(), requestInfo );

        } catch ( JSONException e ) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    @Override
    public void getResults( ArrayList<PetResult> results ) {
        /**
         * Filter results to only use items with a photo
         * Populate an Arraylist of official feautred items
         * Start the int count at -1
         * Will call next() to move the counter to 0 and get the first item
         */
        if ( results != null ) {
            for (PetResult result : results) {
                if (result.getBestPhoto(1) != null || result.getBestPhoto(2) != null) {
                    featuredItems.add(result);
                }
            }

            Intent featuredBroadcast = new Intent(GET_FEATURED);
            context.sendBroadcast(featuredBroadcast);
        } else {
            /**
             * Make the request again
             */
            getFeatured( location, type );
        }

    }

    public boolean hasNext() {
        return featuredItems.size() > 0;
    }

    public PetResult next() {
        /**
         * Returns the next PetResult from the featured ArrayList
         * Makes sure to set count back to 0 if it is == size of list
         */

        ++currentItem;

        currentItem = currentItem <= featuredItems.size() -1 ? currentItem : 0;

        return featuredItems.get( currentItem );
    }

    public PetResult getCurrent() {
        return featuredItems.get( currentItem );
    }

}
