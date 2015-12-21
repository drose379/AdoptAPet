package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by dylan on 12/15/15.
 */
public class SheltersNearUserFrag extends ShelterParentFrag implements APIHelper.SheltersCallback {


    private String location;

    private NewBookmarkCallback bookmarkCallback;

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        View v = super.onCreateView(inflater, parent, savedInstance);

        checkLocation();

        return v;
    }

    private void checkLocation() {

        SQLiteDatabase readable = new ZipDBHelper( getActivity() ).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT zip FROM " + ZipDBHelper.table_name, null, null);

        if ( result.getCount() == 1 ) {

            grabShelters();

        } else {

            final View layout = LayoutInflater.from( getActivity() ).inflate( R.layout.location_save_dialog,null );

            final AlertDialog getLocation = new AlertDialog.Builder( getActivity() )
                    .setCustomTitle(LayoutInflater.from( getActivity() ).inflate(R.layout.location_title, null))
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

                        SQLiteDatabase writeable = new ZipDBHelper( getActivity() ).getWritableDatabase();
                        ContentValues vals = new ContentValues();
                        vals.put( "zip", location );
                        writeable.insert(ZipDBHelper.table_name, null, vals);

                        writeable.close();

                        getLocation.dismiss();
                        grabShelters();

                    } else {
                        locationBox.setError("Invalid");
                    }

                }
            });

        }

        readable.close();

    }

    public void grabShelters() {
        /**
         * Get location from local DB, use APIHelper to make API call
         */

        SQLiteDatabase readable = new ZipDBHelper( getActivity() ).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT zip FROM " + ZipDBHelper.table_name, null, null);

        result.moveToFirst();

        location = result.getString(result.getColumnIndex("zip"));

        readable.close();

        APIHelper.getShelters(location, this, new Handler());

    }

    @Override
    public void getShelterResults( ArrayList<ShelterResult> results ) {

        if ( results != null ) {
            shelterNames = new String[results.size()];

            for (int i = 0; i < results.size(); i++) {
                shelterNames[i] = results.get(i).getName();
                ShelterResult shelter = results.get( i );
                shelter.setBookmarked( isBookmarked( shelter ) );
            }

            loader.setVisibility(View.GONE);
            shelterList.setVisibility(View.VISIBLE);

            ShelterResultAdapter adapter = new ShelterResultAdapter(getActivity(), results);
            shelterList.setAdapter(adapter);
            shelterList.setOnItemClickListener(this);
        } else {

            /**
             * Try again
             */
            APIHelper.getShelters(location, this, new Handler());

        }


    }








}