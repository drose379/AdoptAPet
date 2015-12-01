package dylan.com.adoptapet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dylan on 11/29/15.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.about_activity_layout);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setTitle( "About" );

        //TODO:: Update the toolbar here, then update the toolbar in the MainActivity



        /**
         * Need to create layout, show details about Icons8 and PetFinder and how they were used
         * Also discuss future plans for updates
            * Pet analytics for shelters
            * Pet donation buttons on registered animals
         */

    }

    @Override
    public boolean onOptionsItemSelected( android.view.MenuItem item ) {

        switch ( item.getItemId() ) {
            case android.R.id.home :
                finish();
                return true;
        }

        return super.onOptionsItemSelected( item );
    }

}
