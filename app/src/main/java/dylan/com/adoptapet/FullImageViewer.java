package dylan.com.adoptapet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

/**
 * Created by dylan on 11/29/15.
 */
public class FullImageViewer extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate( savedInstance );
        setContentView( R.layout.full_image_layout );

        /**
         * Get drawable and show it
         */

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar);
        ImageView backButton = (ImageView) findViewById( R.id.toolbarBackButton );
        TextView title = (TextView) findViewById( R.id.toolbarTitle );

        byte[] imageBytes = getIntent().getByteArrayExtra( "image" );
        String name = getIntent().getStringExtra( "name" );
        Bitmap imageBitmap = BitmapFactory.decodeByteArray( imageBytes, 0, imageBytes.length );

        ImageView fullImage = (ImageView) findViewById( R.id.fullImage );
        fullImage.setImageBitmap( imageBitmap );

        backButton.setOnClickListener( this );
        title.setText( name );

    }

    @Override
    public void onClick( View v ) {

        switch ( v.getId() ) {
            case R.id.toolbarBackButton :
                finish();
                break;
        }

    }

}
