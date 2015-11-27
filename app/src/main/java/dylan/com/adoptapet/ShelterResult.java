package dylan.com.adoptapet;

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

}