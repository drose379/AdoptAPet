package dylan.com.adoptapet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Random;

/**
 * Created by dylan on 11/24/15.
 */
public class ShelterResult {

    private String id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String latlng;
    private String phone;
    private String fax;
    private String email;
    private JSONArray photosJson;
    private boolean isBookmarked;

    public ShelterResult setId( String id ) {
        this.id = id;
        return this;
    }
    public ShelterResult setName( String name ) {
        this.name = name;
        return this;
    }
    public ShelterResult setAddress( String address ) {
        this.address = address;
        return this;
    }
    public ShelterResult setCity( String city ) {
        this.city = city;
        return this;
    }
    public ShelterResult setState( String state ) {
        this.state = state;
        return this;
    }
    public ShelterResult setCountry( String country ) {
        this.country = country;
        return this;
    }
    public ShelterResult setLatLng( String latLng ) {
        this.latlng = latLng;
        return this;
    }
    public ShelterResult setPhone( String phone ) {
        this.phone = phone;
        return this;
    }
    public ShelterResult setFax( String fax ) {
        this.fax = fax;
        return this;
    }
    public ShelterResult setEmail( String email ) {
        this.email = email;
        return this;
    }
    public ShelterResult setPhotos( String photosJSON ) {
        try {
            photosJson = new JSONArray( photosJSON );
        } catch ( JSONException e ) {
            photosJson = new JSONArray();
        }

        return this;
    }

    public void setBookmarked( boolean bookmarked ) {
        this.isBookmarked = bookmarked;
    }



    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getCountry() {
        return country;
    }
    public String getLatLng() {
        return latlng;
    }
    public String getPhone() {
        return phone;
    }
    public String getFax() {
        return fax;
    }
    public String getEmail() {
        return email;
    }
    public JSONArray getPhotos() {
        return photosJson;
    }

    public String getPhoto( int item ) {
        String url = "";
        try {
            url = photosJson.getString( item );
        } catch ( JSONException e ) {
            url = "";
        }

        return url;
    }

    public String getRandomPhoto() {

        String url = "";

        Random rand = new Random();

        try {
            url = photosJson.getString( rand.nextInt( photosJson.length() ) );
        } catch ( JSONException e ) {
            throw new RuntimeException( e );
        }

        return url;
    }

    public boolean isBookmarked() {return isBookmarked;}
    public String generateLocationText() {
        String location = "";

        String address = getAddress();
        String city = getCity();
        String state = getState();
        String country = getCountry();

        if ( address != null && !address.isEmpty() ) {

            if ( !city.isEmpty() || !state.isEmpty() || !country.isEmpty() ) {
                location += address + ", ";
            } else {
                location += address;
            }

        }

        if ( !city.isEmpty() ) {

            if ( !state.isEmpty() || !country.isEmpty() ) {
                location += city + ", ";
            } else {
                location += city;
            }

        }

        if ( !state.isEmpty() ) {

            if ( !country.isEmpty() ) {
                location += state + ", ";
            } else {
                location += state;
            }

        }

        location += !country.isEmpty() ? country : "";

        return location;
    }

}
