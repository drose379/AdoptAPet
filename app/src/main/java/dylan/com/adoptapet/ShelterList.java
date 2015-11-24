package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by dylan on 11/23/15.
 */
public class ShelterList extends AppCompatActivity {

    private ProgressBar loader;
    private ListView shelterList;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.shelter_list_layout);

        loader = (ProgressBar) findViewById( R.id.loader );
        shelterList = (ListView) findViewById( R.id.shelterList );

        checkLocation();
    }

    public void checkLocation() {

        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT zip FROM " + ZipDBHelper.table_name, null, null );

        if ( result.getCount() == 1 ) {

            grabShelters();

        } else {

            final View layout = LayoutInflater.from( this ).inflate( R.layout.location_save_dialog,null );

            final AlertDialog getLocation = new AlertDialog.Builder( this )
                    .setCustomTitle(LayoutInflater.from(this).inflate(R.layout.location_title, null))
                    .setView(layout)
                    .setMessage(getResources().getString(R.string.provide_location))
                    .setPositiveButton("Save", null)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            /**
                             * Check if location saveed, if not, show dialog again
                             */
                        }
                    })
                    .create();

            getLocation.show();


             getLocation.getButton( DialogInterface.BUTTON_POSITIVE ).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     EditText locationBox = (EditText) layout.findViewById(R.id.locationBox);
                     String location = locationBox.getText().toString();

                     if (location.length() == 5) {

                         SQLiteDatabase writeable = new ZipDBHelper( ShelterList.this ).getWritableDatabase();
                         ContentValues vals = new ContentValues();
                         vals.put( "zip", location );
                         writeable.insert(ZipDBHelper.table_name, null, vals);

                         getLocation.dismiss();
                         grabShelters();

                     } else {
                         locationBox.setError("Invalid");
                     }

                 }
             });

        }

    }

    public void grabShelters() {
        /**
         * Get location from local DB, use APIHelper to make API call
         */

        SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT zip FROM " + ZipDBHelper.table_name, null, null );

        result.moveToFirst();

        String location = result.getString( result.getColumnIndex( "zip" ) );

        APIHelper.getShelters( location, new APIHelper.SheltersCallback() {
            @Override
            public void getShelterResults( ArrayList<ShelterResult> results ) {
                /**
                 * Populate the list with results
                 */
            }
        }, new Handler());

    }

}
