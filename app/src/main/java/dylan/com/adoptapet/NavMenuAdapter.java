package dylan.com.adoptapet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dylan on 11/20/15.
 */
public class NavMenuAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MenuItem> items;

    public NavMenuAdapter( Context context, ArrayList<MenuItem> items  ) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public long getItemId( int item ) {
        return 0;
    }

    @Override
    public MenuItem getItem( int item ) {
        return items.get( item );
    }

    @Override
    public int getViewTypeCount() {
        return 1; //TODO:: Implement the 2 view types the right way, change this to 2
    }

    @Override
    public View getView( int item, View recycledView, ViewGroup parent ) {
        MenuItem currentItem = items.get( item );

        if ( currentItem.getType() == 1 ) {
            recycledView = recycledView == null ? LayoutInflater.from( context ).inflate( R.layout.menuitem_layout, parent, false ) : recycledView;

            ImageView icon = (ImageView) recycledView.findViewById( R.id.itemIcon );
            TextView label = (TextView) recycledView.findViewById( R.id.itemLabel );

            icon.setImageDrawable( currentItem.getIcon() );
            label.setText( currentItem.getLabel() );
        } else {
            recycledView = LayoutInflater.from( context ).inflate( R.layout.featured_animal_layout, parent,false );

            CircleImageView headImage = (CircleImageView) recycledView.findViewById( R.id.petImage );
            TextView name = (TextView) recycledView.findViewById( R.id.name );

            headImage.setBorderColorResource( R.color.colorMaleCard );

            headImage.setBorderWidth( 10 );

            Picasso.with( context ).load( currentItem.getImageUrl() ).fit().into( headImage );
            name.setText( currentItem.getName() );
        }


        return recycledView;
    }


}
