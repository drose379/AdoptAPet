package dylan.com.adoptapet.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;

/**
 * Created by dylan on 11/7/15.
 */
public class DistanceUtil {

    public static String zipDistance( Context context, String zip1, String zip2 ) {

        String distanceString = null;
        Geocoder geo = new Geocoder( context );;

        try {

            Address clientAddr = geo.getFromLocationName( zip1, 1 ).get( 0 );
            Address petAddr = geo.getFromLocationName( zip2, 1 ).get( 0 );

            float[] distMetersParent = new float[3];

            Location.distanceBetween(clientAddr.getLatitude(), clientAddr.getLongitude(), petAddr.getLatitude(),
                    petAddr.getLongitude(), distMetersParent);

            float distanceMeters = distMetersParent[0];

            distanceString = String.valueOf( Math.round( distanceMeters  * 0.00062137 ) );

        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage() );
        }

        return distanceString;

    }

}
