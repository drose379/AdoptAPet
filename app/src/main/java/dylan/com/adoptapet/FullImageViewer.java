package dylan.com.adoptapet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

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




        String[] images = getIntent().getStringArrayExtra("images");
        String name = getIntent().getStringExtra("name");



        /**
         * TODO:: Implement image slider with ViewPager, no need to use fragments, only use views to slide, PagerAdapter will do
         */



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
