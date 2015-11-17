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

    public static String type_col = "type";
    public static String name_col = "name";
    public static String id_col = "id";
    public static String photo_col = "photo";

    //TODO:: column names

    public FavoritesDBHelper( Context context ) {
        super( context, db_name, null, 1 );
    }

    @Override
    public void onCreate( SQLiteDatabase db ) {
        String query = "CREATE TABLE favorites ( id TEXT, type TEXT, name TEXT, photo TEXT )";
        db.execSQL( query );
    }

    @Override
    public void onUpgrade( SQLiteDatabase db, int old, int newVersion ) {

    }


}
