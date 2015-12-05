package dylan.com.adoptapet;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by dylan on 12/5/15.
 */
public class FullImagesPagerAdapter extends PagerAdapter {

    private Context context;
    private String[] images;

    public FullImagesPagerAdapter( Context context, String[] imageUrls ) {
        this.context = context;
        this.images = imageUrls;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject( View v, Object o ) {
        return v == o;
    }

    @Override
    public Object instantiateItem( ViewGroup parent, int position ) {
        View v = LayoutInflater.from(context).inflate( R.layout.full_image, parent, false );
        ImageView image = (ImageView) v.findViewById( R.id.image );


        Picasso.with( context ).load( images[position] ).placeholder( R.drawable.ic_load_2_male ).into(image);

        parent.addView( v );

        return v;
    }

    @Override
    public void destroyItem( ViewGroup parent, int position, Object object ) {
        parent.removeViewAt(position);
    }

}
