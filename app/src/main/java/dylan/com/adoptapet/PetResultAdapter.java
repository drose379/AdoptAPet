package dylan.com.adoptapet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

    private int[] femaleIcons = { R.drawable.ic_flower_1,R.drawable.ic_flower_bouquet_3, R.drawable.ic_spring_4 };
    private int[] maleDogIcons  = { R.drawable.ic_dog_bone_1, R.drawable.ic_dog_bowl_100, R.drawable.ic_fire_hydrant_100 };
    private int[] maleOtherIcons = { R.drawable.ic_soccer_sm, R.drawable.ic_basketball_sm, R.drawable.ic_barbell_sm, R.drawable.ic_dumbbell_sm };
    private Random rand;

    private Context context;
    private ArrayList<PetResult> pets;
    private Callback callback;

    private View loadMoreView;

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

    public Drawable getMaleIcon( PetResult current ) {
        /**
         * TODO::
         * If dog, choose from dog icons
         * Else, choose from general
         */

        Drawable selectedIcon = null;

        if ( current.getType().equals( "dog" ) )
            selectedIcon = context.getResources().getDrawable( maleDogIcons[rand.nextInt( 3 )] );
        else
            selectedIcon =  context.getResources().getDrawable( maleOtherIcons[rand.nextInt( 4 )] );

        return selectedIcon;
    }

    public Drawable getFemaleIcon() {
        return context.getResources().getDrawable( femaleIcons[rand.nextInt( 3 )] );
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

            //TODO:: ADJUST IF NO PHOTOS AVAILABLE, SET VIS TO GONE, ADJUST CARD

            if ( result.getBestPhoto( 2 ) == null ) {
                imageTwo = Picasso.with( context ).load( result.getBestPhoto( 1 ) );
            }

            recycledView = recycledView == null ? LayoutInflater.from( context ).inflate( R.layout.pet_card, parent, false ) : recycledView;

            ViewFlipper imageParent = (ViewFlipper) recycledView.findViewById( R.id.imageContainer );
            TextView noPhotoText = (TextView) recycledView.findViewById( R.id.noPhotoText );
            CircleImageView petHeadImage = (CircleImageView) recycledView.findViewById( R.id.petHeadImage );
            CircleImageView petHeadImageTwo = (CircleImageView) recycledView.findViewById( R.id.petHeadImageTwo );
            ImageView genderIcon = (ImageView) recycledView.findViewById( R.id.genderIcon );
            ImageView locationIcon = (ImageView) recycledView.findViewById( R.id.locationIcon );
            TextView petHeadGreeting = (TextView) recycledView.findViewById( R.id.petHeadGreeting );
            TextView petHeadBasicInfo = (TextView) recycledView.findViewById( R.id.petHeadDescription );
            TextView genderText = (TextView) recycledView.findViewById( R.id.genderText );
            TextView distanceText = (TextView) recycledView.findViewById( R.id.distanceText );


            View backdrop = recycledView.findViewById(R.id.backdrop);

            if ( result.getBestPhoto( 1 ) == null && result.getBestPhoto( 2 ) == null ) {
                imageParent.setVisibility(View.GONE);
                noPhotoText.setVisibility( View.VISIBLE );
            }
            else {
                imageParent.setVisibility(View.VISIBLE);
                noPhotoText.setVisibility(View.GONE);
            }

            switch ( result.getSex() ) {
                case "Male" :

                    petHeadImage.setBorderColorResource(R.color.colorMaleCard);
                    petHeadImageTwo.setBorderColorResource(R.color.colorMaleCard);
                    petHeadImage.setBorderWidth( 10 );
                    petHeadImageTwo.setBorderWidth(10);

                    backdrop.setBackgroundResource(R.drawable.round_card_male);

                    if ( result.getType().equals( "dog" ) )
                        genderIcon.setImageResource( R.drawable.ic_dog_footprint_100_male );
                    else
                        genderIcon.setImageResource( R.drawable.ic_male_sign );

                    genderText.setText("M" );
                    genderText.setTextColor(context.getResources().getColor(R.color.colorMale));
                    noPhotoText.setTextColor( context.getResources().getColor( R.color.colorMale ) );

                    distanceText.setTextColor( context.getResources().getColor( R.color.colorMale ) );
                    locationIcon.setImageDrawable( getMaleIcon( result ) );

                    break;

                case "Female" :

                    petHeadImage.setBorderColorResource(R.color.colorFemaleCard);
                    petHeadImageTwo.setBorderColorResource(R.color.colorFemaleCard);
                    petHeadImage.setBorderWidth( 10 );
                    petHeadImageTwo.setBorderWidth(10);

                    backdrop.setBackgroundResource(R.drawable.round_card_female);

                    if ( result.getType().equals( "dog" ) )
                        genderIcon.setImageDrawable( context.getResources().getDrawable( R.drawable.ic_dog_footprint_female));
                    else
                        genderIcon.setImageDrawable( context.getResources().getDrawable( R.drawable.ic_female_sign ) );
                    genderText.setText("F");
                    genderText.setTextColor(context.getResources().getColor(R.color.colorFemale));
                    noPhotoText.setTextColor( context.getResources().getColor( R.color.colorFemale ) );

                    distanceText.setTextColor( context.getResources().getColor( R.color.colorFemale ) );
                    locationIcon.setImageDrawable( getFemaleIcon() );

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

            loadMoreView = loadMoreView == null ? LayoutInflater.from( context ).inflate( R.layout.load_more_list_item, parent, false ) : loadMoreView;
            recycledView = loadMoreView;

            callback.loadMore();

        }

        return recycledView;
    }

}
