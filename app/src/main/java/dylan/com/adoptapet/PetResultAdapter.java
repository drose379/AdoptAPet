package dylan.com.adoptapet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


import de.hdodenhof.circleimageview.CircleImageView;
import dylan.com.adoptapet.util.DistanceUtil;

/**
 * Created by dylan on 11/4/15.
 */
public class PetResultAdapter extends BaseAdapter {

    public interface Callback {
        void loadMore();
    }

    private static final int TYPE_CARD = 0;
    private static int TYPE_LOAD_BUTTON = 1;

    public static String LOAD_MORE_PETS = "LOAD_MORE_PETS";

    private String[] greetingStarts = { "Hello", "Hi", "Hey There", "Hey", "Howdy", "Good Day", "Greetings" };
    private String[] greetingMiddle = {  "My name is", "Call me", "They Call Me", "People call me" };

    private Random rand;

    private Context context;
    private ArrayList<PetResult> pets;
    private Callback callback;

    public PetResultAdapter( Context context, ArrayList<PetResult> pets ) {
        callback = (Callback) context;
        this.context = context;
        this.pets = pets;

        rand = new Random();
    }

    @Override
    public int getCount() {
        return pets.size() + 1;
    }

    @Override
    public long getItemId( int item ) {
        return 0;
    }

    @Override
    public PetResult getItem( int item ) {
        return pets.get( item );
    }

    public String getGreetingText() {
        int startRandom = rand.nextInt( 6 );


        return greetingStarts[startRandom] + "!";
    }

    public String getBasicInfoText() {
        int middleRandom = rand.nextInt( 3 );

        return greetingMiddle[middleRandom];
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType( int item ) {
        return item < pets.size() ? TYPE_CARD : TYPE_LOAD_BUTTON;
    }

    public void updateData( ArrayList<PetResult> newItems ) {
        for ( PetResult newItem : newItems ) {
            pets.add( newItem );
        }
    }

    @Override
    public View getView( int item, View recycledView, ViewGroup parent ) {

        if ( getItemViewType( item ) == TYPE_CARD ) {

            PetResult result = pets.get(item);

            RequestCreator imageOne = Picasso.with( context ).load( result.getBestPhoto( 1 ) );
            RequestCreator imageTwo = Picasso.with( context ).load( result.getBestPhoto( 2 ) );

            //TODO:: Check for a photo 3 below just in case 2 is NULL and 3 is not

            if ( result.getBestPhoto( 2 ) == null ) {
                imageTwo = Picasso.with( context ).load( result.getBestPhoto( 1 ) );
            }

            recycledView = recycledView == null ? LayoutInflater.from( context ).inflate( R.layout.pet_card, parent, false ) : recycledView;

            ViewFlipper imageParent = (ViewFlipper) recycledView.findViewById( R.id.imageContainer );
            CircleImageView petHeadImage = (CircleImageView) recycledView.findViewById( R.id.petHeadImage );
            CircleImageView petHeadImageTwo = (CircleImageView) recycledView.findViewById( R.id.petHeadImageTwo );
            ImageView genderIcon = (ImageView) recycledView.findViewById( R.id.genderIcon );
            ImageView locationIcon = (ImageView) recycledView.findViewById( R.id.locationIcon );
            TextView petHeadGreeting = (TextView) recycledView.findViewById( R.id.petHeadGreeting );
            TextView petHeadBasicInfo = (TextView) recycledView.findViewById( R.id.petHeadDescription );
            TextView genderText = (TextView) recycledView.findViewById( R.id.genderText );
            TextView distanceText = (TextView) recycledView.findViewById( R.id.distanceText );


            View backdrop =recycledView.findViewById(R.id.backdrop);


            switch ( result.getSex() ) {
                case "Male" :

                    petHeadImage.setBorderColorResource(R.color.colorMaleCard);
                    petHeadImageTwo.setBorderColorResource(R.color.colorMaleCard);
                    petHeadImage.setBorderWidth( 10 );
                    petHeadImageTwo.setBorderWidth( 10 );

                    backdrop.setBackgroundResource( R.drawable.round_card_male );

                    genderIcon.setImageResource( R.drawable.ic_dog_footprint_100_male );
                    genderText.setText("M" );
                    genderText.setTextColor( context.getResources().getColor( R.color.colorMale ) );

                    distanceText.setTextColor( context.getResources().getColor( R.color.colorMale ) );
                    locationIcon.setImageDrawable( context.getResources().getDrawable( R.drawable.ic_location_100_male ) );

                    break;

                case "Female" :

                    petHeadImage.setBorderColorResource(R.color.colorFemaleCard);
                    petHeadImageTwo.setBorderColorResource(R.color.colorFemaleCard);
                    petHeadImage.setBorderWidth( 10 );
                    petHeadImageTwo.setBorderWidth( 10 );

                    backdrop.setBackgroundResource( R.drawable.round_card_female );

                    genderIcon.setImageDrawable( context.getResources().getDrawable( R.drawable.ic_dog_footprint_female));
                    genderText.setText("F");
                    genderText.setTextColor( context.getResources().getColor( R.color.colorFemale ) );

                    distanceText.setTextColor( context.getResources().getColor( R.color.colorFemale ) );
                    locationIcon.setImageDrawable( context.getResources().getDrawable( R.drawable.ic_location_100_female ) );

                    break;
            }

            String distance = result.getDistance() + " Mi";
            distanceText.setText( distance );

            imageOne.fit().into( petHeadImage );
            imageTwo.fit().into( petHeadImageTwo );

            String basicInfo = getBasicInfoText() + " " + result.getName() + "\nI am a " + result.getBreed();

            petHeadGreeting.setText( getGreetingText() );
            petHeadBasicInfo.setText( basicInfo );

            imageParent.setInAnimation(context, android.R.anim.fade_in);
            imageParent.setOutAnimation( context, android.R.anim.fade_out );
            imageParent.startFlipping();

        } else {
            recycledView = LayoutInflater.from( context ).inflate( R.layout.load_more_list_item, parent, false );

            final Button loadButton = (Button) recycledView.findViewById( R.id.loadButton );
            final ProgressBar progress = (ProgressBar) recycledView.findViewById( R.id.progress );

            loadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadButton.setVisibility( View.GONE );
                    progress.setVisibility( View.VISIBLE );

                    callback.loadMore();
                }
            });

        }




        return recycledView;
    }

}
