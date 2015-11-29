package dylan.com.adoptapet;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import dylan.com.adoptapet.util.DistanceUtil;

/**
 * Created by dylan on 11/4/15.
 */
public class PetResult implements Serializable {

    /**
     * POJO for the PetResult
     * Model after the PetResult class on the server
     */

    private String name;
    private String id;
    private String animalType;
    private ArrayList<String> breed;
    private String breedsRaw;
    private boolean isMix;
    private String age;
    private String sex;
    private String size;
    private ArrayList<String> extras;
    private String description;
    private ArrayList<String> photoUrls;
    private HashMap<String,String> contactInfo;
    private String contactInfoRaw;
    private String distanceFromClient;

    public PetResult setName( String name ) {
        this.name = name;
        return this;
    }
    public PetResult setId( String id ) {
        this.id = id;
        return this;
    }
    public PetResult setAnimalType( String animalType ) {

        try {
            JSONObject type = new JSONObject( animalType );
            this.animalType = type.getString( "0" );
        } catch ( JSONException e ) {
            if ( !animalType.isEmpty() ) {
                this.animalType = animalType;
            } else {
                throw new RuntimeException( e.getMessage() );
            }

        }

        return this;
    }
    public PetResult setBreed( JSONArray breeds ) throws JSONException {
        this.breed = new ArrayList<String>();
        this.breedsRaw = breeds.toString();

        for ( int i = 0; i < breeds.length(); i++ ) {
            this.breed.add( breeds.getString( i ) );
        }

        return this;
    }
    public PetResult setIsMix( boolean isMix ) {
        this.isMix = isMix;
        return this;
    }
    public PetResult setAge( String age ) {
        this.age = age;
        return this;
    }

    public PetResult setSex( String sex ) {
        this.sex = sex;
        return this;
    }
    public PetResult setSize( String size ) {
        this.size = size;
        return this;
    }
    public PetResult setExtras( JSONArray extras ) throws JSONException {
        this.extras = new ArrayList<String>();

        for ( int i = 0; i < extras.length(); i++ ) {
            this.extras.add( extras.getString( i ) );
        }

        return this;
    }
    public PetResult setDescription( String description ) {
        this.description = description;
        return this;
    }
    public PetResult setPhotos( JSONArray photos ) throws JSONException {
        this.photoUrls = new ArrayList<String>();

        for ( int i = 0; i < photos.length(); i++ ) {
            this.photoUrls.add( photos.getString( i ) );
        }

        return this;
    }
    public PetResult setContactInfo( JSONObject contactInfo ) throws JSONException {
        //TODO Convert json object to hashmap;

        this.contactInfoRaw = contactInfo.toString();

        this.contactInfo = new HashMap<String,String>();

        this.contactInfo.put( "name", contactInfo.getString( "name" ) );
        this.contactInfo.put( "address", contactInfo.getString( "address" ) );
        this.contactInfo.put( "city", contactInfo.getString( "city" ) );
        this.contactInfo.put( "state", contactInfo.getString( "state" ) );
        this.contactInfo.put( "zip", contactInfo.getString( "zip" ) );
        this.contactInfo.put( "phone", contactInfo.getString( "phone" ) );
        this.contactInfo.put( "fax", contactInfo.getString( "fax" ) );
        this.contactInfo.put( "email", contactInfo.getString( "email" ) );

        return this;
    }

    public void setDistanceFromClient( String distance) {
        distanceFromClient = distance;
    }

    public String getName() {
        String finalName = name;

        int dashIndex = name.indexOf( "-" );
        int parenIndex = name.indexOf("(");

        if ( dashIndex != -1 ) {
            finalName = finalName.substring( 0, dashIndex );
        } else if ( parenIndex != -1 ) {
            finalName = finalName.substring( 0, parenIndex );
        }

        //return dashIndex == -1 ? name : name.substring( 0, dashIndex );

        return finalName;
    }
    public String getBreed() {
        return breed.size() > 1 ? breed.get( 0 ) + " Mix" : breed.get( 0 );
    }

    public ArrayList<String> getBreeds() {
        return breed;
    }

    public String getBreedsRaw() { return breedsRaw; }

    public String getSex() {
        return sex;
    }

    public ArrayList<String> getPhotos() {
        return photoUrls;
    }

    public String getBestPhoto( int which ) {
        String photoId = String.valueOf( which );

        String url = null;
        int currentLargest = 0;

        for ( String photoUrl : getPhotos() ) {
            String id = photoUrl.substring( photoUrl.indexOf("?") - 2, photoUrl.indexOf("?") - 1 );
            if ( id.equals( photoId ) ) {
                //firstPhotos.add( photoUrl ); get the size, if bigger then current, assign to url;
                int widthStart = photoUrl.indexOf( "width=" ) + 6;
                int widthEnd = photoUrl.indexOf( "&", widthStart  );
                int width = Integer.valueOf( photoUrl.substring( widthStart, widthEnd ) );

                if ( width > currentLargest ) {
                    url = photoUrl;
                    currentLargest = width;
                }
            }
        }

        return url;
    }


    public String getDistance() {
        return distanceFromClient;
    }

    public String getZip() {
        return contactInfo.get( "zip" );
    }


    public String getAge() {
        return age;
    }

    public String getSize() {
        return size;
    }

    public String getDescription() {
        return description;
    }


    public String getContactNumber() {
        return contactInfo.get( "phone" );
    }

    public String getLocationInfo() {
        String contactString = "";

        String address = contactInfo.get( "address" );
        String city = contactInfo.get( "city" );
        String state = contactInfo.get( "state" );

        if ( !address.isEmpty() ) {
            if ( city.isEmpty() && state.isEmpty() ) {
                contactString += address;
            } else {
                contactString += address + ", ";
            }
        }

        if ( !city.isEmpty() ) {
            if ( state.isEmpty() ) {
                contactString += city;
            } else {
                contactString += city + ", ";
            }
        }

        contactString += state;

        return contactString;
        //return contactInfo.get( "address" ) + ", " + contactInfo.get( "city" ) + ", " + contactInfo.get( "state" );
    }

    public String getContactInfoRaw() {
        return contactInfoRaw;
    }

    public String getEmail( ) {
        return contactInfo.get( "email" );
    }

    public String getId() { return id; }

    public String getType() {
        return animalType;
    }

    public boolean isMix() {
        return isMix;
    }


}
