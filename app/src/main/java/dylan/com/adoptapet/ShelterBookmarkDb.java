package dylan.com.adoptapet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dylan on 12/15/15.
 */
public class ShelterBookmarkDb extends SQLiteOpenHelper {

    public static final String db_name = "shelter_bookmarks";
    public static final String table_name = "shelters";
    public static final String id_col = "shelterId";
    public static final String name_col = "shelterName";
    public static final String address_col = "address";
    public static final String city_col = "city";
    public static final String state_col = "state";
    public static final String country_col = "country";
    public static final String phone_col = "phone";
    public static final String email_col = "email";
    public static final String pet_ids_col = "currentPetIds";

    public ShelterBookmarkDb( Context context ) {
        super( context, db_name, null, 1  );
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        String createTable = "CREATE TABLE " + table_name + " (" +
                "shelterId TEXT," +
                "shelterName TEXT," +
                "address TEXT," +
                "city TEXT," +
                "state TEXT," +
                "country TEXT," +
                "phone TEXT," +
                "email TEXT," +
                "currentPetIds TEXT" +
                ")";

        db.execSQL( createTable );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {

    }

    @Override
    public void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {

    }

}
