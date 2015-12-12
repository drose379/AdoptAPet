package dylan.com.adoptapet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dylan on 11/16/15.
 */
public class FavoritesDBHelper extends SQLiteOpenHelper {

    private static String db_name = "pet_favorites";
    public static String table_name = "favorites";

    public static String id_col = "id";
    public static String type_col = "type";
    public static String name_col = "name";
    public static String photo_col = "photo";
    public static String breed_col = "breed";
    public static String isMix_col = "isMix";
    public static String age_col = "age";
    public static String sex_col = "sex";
    public static String size_col = "size";
    public static String extras_col = "extras";
    public static String description_col = "description";
    public static String contactInfo_col = "contactInfo";
    public static String lastupdated_col = "lastUpdated";

    //TODO:: NOTE, THE lastUpdated COLUMN IS ACTUALLY THE SHELTERiD

    public FavoritesDBHelper( Context context ) {
        super( context, db_name, null, 1 );
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        String query = "CREATE TABLE favorites ( " +
                "id TEXT," +
                "type TEXT, " +
                "name TEXT," +
                "photo TEXT," +
                "breed TEXT," +
                "isMix TEXT," +
                "age TEXT," +
                "sex TEXT," +
                "size TEXT," +
                "extras TEXT," +
                "description TEXT," +
                "contactInfo TEXT," +
                "lastUpdated TEXT )"; //TODO:: Save entire PetResult to localDB
        db.execSQL( query );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int old, int newVersion ) {

    }


}
