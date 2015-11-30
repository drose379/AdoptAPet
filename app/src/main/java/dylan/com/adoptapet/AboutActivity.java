package dylan.com.adoptapet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dylan on 11/29/15.
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView( R.layout.about_activity_layout );

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView title = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        ImageView backButton = (ImageView) toolbar.findViewById( R.id.toolbarBackButton );

        title.setText( "About" );
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                switch ( v.getId() ) {
                    case R.id.toolbarBackButton :
                        finish();
                        break;
                }
            }
        });

        /**
         * Need to create layout, show details about Icons8 and PetFinder and how they were used
         * Also discuss future plans for updates
            * Pet analytics for shelters
            * Pet donation buttons on registered animals
         */

    }

}
