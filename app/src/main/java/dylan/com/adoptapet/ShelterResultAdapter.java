package dylan.com.adoptapet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by dylan on 11/24/15.
 */
public class ShelterResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ShelterResult> results;
    private String previousImage;

    public ShelterResultAdapter( Context context, ArrayList<ShelterResult> results ) {
        this.context = context;
        this.results = results;
        previousImage = "";
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public ShelterResult getItem( int item ) {
        return results.get(item);
    }

    @Override
    public long getItemId( int item ) {
        return 0;
    }

    public int getItemIndex( String name ) {
        int index = 0;

        for ( int i = 0; i < results.size(); i++ ) {
            if ( results.get( i ).getName().equals( name ) ) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void updateData() {

    }




    @Override
    public View getView( int which, View recycledView, ViewGroup parent ) {
        ShelterResult shelter = results.get( which );

        ShelterResultViewHolder viewHolder;

        if ( recycledView == null ) {
            recycledView = LayoutInflater.from( context ).inflate( R.layout.shelter_card, parent, false );

            viewHolder = new ShelterResultViewHolder();

            /**
            viewHolder.shelterName = (TextView) recycledView.findViewById( R.id.shelterName );
            viewHolder.location = (TextView) recycledView.findViewById( R.id.shelterLocation );
            viewHolder.phone = (TextView) recycledView.findViewById( R.id.shelterPhone );
            viewHolder.email = (TextView) recycledView.findViewById( R.id.shelterEmail );
            viewHolder.phoneLayout = (LinearLayout) recycledView.findViewById( R.id.phoneContainer );
            viewHolder.emailLayout = (LinearLayout) recycledView.findViewById( R.id.emailContainer );
             */

            viewHolder.headImage = (ImageView) recycledView.findViewById( R.id.headCardImage );
            viewHolder.headCardText = (TextView) recycledView.findViewById( R.id.headCardText );
            viewHolder.locationText = (TextView) recycledView.findViewById( R.id.locationText );
            viewHolder.phoneText = (TextView) recycledView.findViewById( R.id.shelterPhone );

            recycledView.setTag(viewHolder);

        }

        viewHolder = (ShelterResultViewHolder) recycledView.getTag();

        if ( shelter.getPhotos().length() > 0 ) {
            Picasso.with(context).load( shelter.getRandomPhoto() ).into(viewHolder.headImage);
        } else {
            Picasso.with( context ).load( R.color.colorAccentDark ).into( viewHolder.headImage );
        }

        viewHolder.headCardText.setText(shelter.getName());
        viewHolder.locationText.setText( shelter.generateLocationText() );

        /**
        viewHolder.shelterName.setText( shelter.getName() );
        viewHolder.location.setText( shelter.generateLocationText() );
         */


        if ( !shelter.getPhone().trim().isEmpty() ) {
            viewHolder.phoneText.setText(shelter.getPhone());
            viewHolder.phoneText.setVisibility(View.VISIBLE);
        } else {
            viewHolder.phoneText.setVisibility( View.GONE );
        }

        /**

        if ( !shelter.getEmail().isEmpty() ) {
            viewHolder.email.setText( shelter.getEmail() );
            viewHolder.emailLayout.setVisibility( View.VISIBLE );
        } else {
            viewHolder.emailLayout.setVisibility( View.GONE );
        }

         */


        return recycledView;
    }

    public class ShelterResultViewHolder {
        public TextView shelterName;
        public TextView location;
        public TextView phone;
        public TextView email;

        public LinearLayout phoneLayout;
        public LinearLayout emailLayout;

        /**NEW*/
        public ImageView headImage;
        public TextView headCardText;
        public TextView locationText;
        public TextView phoneText;
    }

}
