package dylan.com.adoptapet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dylan on 11/20/15.
 */
public class ZipDBHelper extends SQLiteOpenHelper {

    private static String db_name = "zipcode";
    public static String table_name = "zip";

    public ZipDBHelper( Context context ) {
        super( context, db_name, null, 1 );
    }

    public void onCreate( SQLiteDatabase db ) {
        String query = "CREATE TABLE zip ( zip TEXT )";
        db.execSQL(query);
    }
    @Override
    public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {}
    @Override
    public void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {}

}
