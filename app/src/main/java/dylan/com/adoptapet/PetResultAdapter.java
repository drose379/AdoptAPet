package dylan.com.adoptapet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dylan on 11/4/15.
 */
public class PetResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PetResult> pets;

    public PetResultAdapter( Context context, ArrayList<PetResult> pets ) {
        this.context = context;
        this.pets = pets;
    }

    @Override
    public int getCount() {
        return pets.size();
    }

    @Override
    public long getItemId( int item ) {
        return 0;
    }

    @Override
    public PetResult getItem( int item ) {
        return pets.get( item );
    }

    @Override
    public View getView( int item, View recycledView, ViewGroup parent ) {

        recycledView = recycledView == null ? LayoutInflater.from( context ).inflate( R.layout.pet_card, parent, false ) : recycledView;

        //TODO:: If pets sex is a girl, give a girly image frame, if it is male, give a man image frame on head image
        ViewFlipper imageParent = (ViewFlipper) recycledView.findViewById( R.id.imageContainer );
        CircleImageView petHeadImage = (CircleImageView) recycledView.findViewById( R.id.petHeadImage );
        CircleImageView petHeadImageTwo = (CircleImageView) recycledView.findViewById( R.id.petHeadImageTwo );
        TextView petHeadName = (TextView) recycledView.findViewById( R.id.petHeadName );

        PetResult result = pets.get( item );

        switch ( result.getSex() ) {
            case "Male" :
                petHeadImage.setBorderColorResource( R.color.colorMale );
                petHeadImageTwo.setBorderColorResource( R.color.colorMale );
                petHeadImage.setBorderWidth(6);
                petHeadImageTwo.setBorderWidth( 6 );
                break;

            case "Female" :
                petHeadImage.setBorderColorResource( R.color.colorFemale );
                petHeadImageTwo.setBorderColorResource( R.color.colorFemale );
                petHeadImage.setBorderWidth(6);
                petHeadImageTwo.setBorderWidth( 6 );
                break;
        }


        Picasso.with( context ).load( result.getPhotos().get( 1 ) ).fit().into( petHeadImage );
        Picasso.with( context ).load( result.getPhotos().get( result.getPhotos().size() - 1 ) ).fit().into(petHeadImageTwo);
        petHeadName.setText(result.getName());

        imageParent.setInAnimation(context, android.R.anim.fade_in);
        imageParent.setOutAnimation( context, android.R.anim.fade_out );
        imageParent.startFlipping();


        return recycledView;
    }

}
