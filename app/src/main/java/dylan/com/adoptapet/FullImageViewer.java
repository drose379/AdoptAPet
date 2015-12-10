package dylan.com.adoptapet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
public class FullImageViewer extends AppCompatActivity implements View.OnClickListener, FullImageViewPager.CurrentItemCallback {

    private String name;

    private TextView toolbarTitle;
    private int imagesCount;

    private int currentPosition;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate( savedInstance );
        setContentView( R.layout.full_image_layout );

        /**
         * Get drawable and show it
         */

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar);
        ImageView backButton = (ImageView) findViewById( R.id.toolbarBackButton );
        toolbarTitle = (TextView) findViewById( R.id.toolbarTitle );

        FullImageViewPager pager = (FullImageViewPager) findViewById( R.id.pager );
        pager.setCallback( this );

        String[] images = getIntent().getStringArrayExtra("images");
        name = getIntent().getStringExtra("name");

        imagesCount = images.length;

        FullImagesPagerAdapter adapter = new FullImagesPagerAdapter( this, images );
        pager.setAdapter( adapter );
        pager.setOffscreenPageLimit( 3 );

        /**
         * TODO:: Implement image slider with ViewPager, no need to use fragments, only use views to slide, PagerAdapter will do
         */



        backButton.setOnClickListener( this );
        toolbarTitle.setText( name + " ( " + 1 + "/" + imagesCount + " )" );

    }

    @Override
    public void getCurrentPosition( int pos ) {
        toolbarTitle.setText( generateToolbarTitle( pos ) );
        currentPosition = pos;
    }

    private String generateToolbarTitle( int pos ) {
        return name + " ( " + pos + "/" + imagesCount + " )";
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
