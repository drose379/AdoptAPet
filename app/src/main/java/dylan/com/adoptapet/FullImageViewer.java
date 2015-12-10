package dylan.com.adoptapet;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by dylan on 11/29/15.
 */
public class FullImageViewer extends AppCompatActivity implements View.OnClickListener, FullImageViewPager.CurrentItemCallback {

    private String name;

    private String[] images;
    private int imagesCount;

    private int currentPosition;


    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate( savedInstance );
        setContentView( R.layout.full_image_layout );

        /**
         * Get drawable and show it
         */

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.colorBlack));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FullImageViewPager pager = (FullImageViewPager) findViewById( R.id.pager );
        pager.setCallback( this );

        images = getIntent().getStringArrayExtra("images");
        name = getIntent().getStringExtra("name");

        imagesCount = images.length;

        FullImagesPagerAdapter adapter = new FullImagesPagerAdapter( this, images );
        pager.setAdapter( adapter );
        pager.setOffscreenPageLimit( 3 );

        getSupportActionBar().setTitle(name + " ( " + 1 + "/" + imagesCount + " )");

    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate( R.menu.photo_share_menu, menu );

        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected( android.view.MenuItem item ) {

        switch ( item.getItemId() ) {
            case android.R.id.home :
                finish();
            case R.id.photoShare:

                Picasso.with( FullImageViewer.this ).load( images[currentPosition] ).into(new Target() {
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        File f = new File( Environment.getExternalStorageDirectory() + "/" + generateRandomFilename() );

                        Log.i("FNAME", f.getAbsolutePath());

                        try {

                            OutputStream photoStream = new FileOutputStream(f);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, photoStream);
                            photoStream.close();

                            Uri uri = Uri.fromFile(f);

                            Intent imageChoser = new Intent(Intent.ACTION_SEND);
                            imageChoser.putExtra( Intent.EXTRA_STREAM, uri );
                            imageChoser.setType("image/*");
                            startActivity(Intent.createChooser(imageChoser, "Share Photo"));


                        } catch ( IOException e ) {
                            showErrorSnackbar();
                        }


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }
                });

        }


    return super.onOptionsItemSelected( item );
}

    @Override
    public void getCurrentPosition( int pos ) {
        getSupportActionBar().setTitle( generateToolbarTitle( pos ) );
        currentPosition = pos;
    }

    private String generateToolbarTitle( int pos ) {
        return name + " ( " + pos + "/" + imagesCount + " )";
    }

    private String generateRandomFilename() {
        String rand = String.valueOf( Calendar.getInstance().getTimeInMillis() + new Random().nextInt() );
        return rand;
    }

    private void showErrorSnackbar() {
        Snackbar.make( findViewById( R.id.root ), "Error Loading Image...", Snackbar.LENGTH_SHORT ).show();
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
