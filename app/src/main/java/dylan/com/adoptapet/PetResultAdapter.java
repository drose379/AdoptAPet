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

    private boolean shouldCallback;

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

    public PetResultAdapter( Context context, boolean shouldBeCallback, ArrayList<PetResult> pets ) {
        shouldCallback = shouldBeCallback;

        if ( shouldBeCallback ) {
            callback = (Callback) context;
        }

        this.context = context;
        this.pets = pets;

        rand = new Random();
    }

    @Override
    public int getCount() {
        int count = 0;

        if ( shouldCallback ) {
            count = pets.size() + 1;
        } else {
            count = pets.size();
        }

        return count;
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
        if ( shouldCallback ) {
            return item < pets.size() ? TYPE_CARD : TYPE_LOAD_BUTTON;
        } else {
            return TYPE_CARD;
        }

    }

    public void updateData( ArrayList<PetResult> newItems ) {
        for ( PetResult newItem : newItems ) {
            pets.add( newItem );
        }
    }

    public void updateAnimalType( ArrayList<PetResult> items ) {
        pets.clear();
        for ( PetResult item : items ) {
            pets.add( item );
        }

        notifyDataSetChanged();
    }

    public void stopLoadingMore() {
        /**
         * TODO:: Change boolean for loadMore to false, check for this before calling callback.loadMore in the getView()
         * TODO:: Also, remove the loading card from the last item in the ListView
         */

        shouldCallback = false;
        Log.i("STOP_LOAD", "END OF RESULTS!");

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

            /**
             * ******************************* Start view loading ***********************
             */

            PetCardViewHolder cardViewHolder;

            if ( recycledView == null ) {
                recycledView = LayoutInflater.from( context ).inflate( R.layout.pet_card, parent, false );

                cardViewHolder = new PetCardViewHolder();

                cardViewHolder.backdrop = recycledView.findViewById( R.id.backdrop );
                cardViewHolder.imageParent = (ViewFlipper) recycledView.findViewById( R.id.imageContainer );
                cardViewHolder.noPhotoText = (TextView) recycledView.findViewById( R.id.noPhotoText );
                cardViewHolder.petHeadImage = (CircleImageView) recycledView.findViewById( R.id.petHeadImage );
                cardViewHolder.petHeadImageTwo = (CircleImageView) recycledView.findViewById( R.id.petHeadImageTwo );
                cardViewHolder.genderIcon = (ImageView) recycledView.findViewById( R.id.genderIcon );
                cardViewHolder.locationIcon = (ImageView) recycledView.findViewById( R.id.locationIcon );
                cardViewHolder.petHeadGreeting = (TextView) recycledView.findViewById( R.id.petHeadGreeting );
                cardViewHolder.petHeadBasicInfo = (TextView) recycledView.findViewById( R.id.petHeadDescription );
                cardViewHolder.genderText = (TextView) recycledView.findViewById( R.id.genderText );

                recycledView.setTag( cardViewHolder );
            }


            cardViewHolder = (PetCardViewHolder) recycledView.getTag();

            if ( result.getBestPhoto( 1 ) == null && result.getBestPhoto( 2 ) == null ) {
                cardViewHolder.imageParent.setVisibility(View.GONE);
                cardViewHolder.noPhotoText.setVisibility( View.VISIBLE );
            }
            else {
                cardViewHolder.imageParent.setVisibility(View.VISIBLE);
                cardViewHolder.noPhotoText.setVisibility(View.GONE);
            }

            switch ( result.getSex() ) {
                case "Male" :

                    cardViewHolder.petHeadImage.setBorderColorResource(R.color.colorMaleCard);
                    cardViewHolder.petHeadImageTwo.setBorderColorResource(R.color.colorMaleCard);
                    cardViewHolder.petHeadImage.setBorderWidth( 10 );
                    cardViewHolder.petHeadImageTwo.setBorderWidth(10);

                    imageOne.placeholder( R.drawable.ic_load_2_male );
                    imageTwo.placeholder( R.drawable.ic_load_2_male );

                    cardViewHolder.backdrop.setBackgroundResource(R.drawable.round_card_male);

                    if ( result.getType().equals( "Dog" ) )
                        cardViewHolder.genderIcon.setImageResource( R.drawable.ic_dog_footprint_100_male );
                    else
                        cardViewHolder.genderIcon.setImageResource( R.drawable.ic_male_sign );

                    cardViewHolder.genderText.setText("M" );
                    cardViewHolder.genderText.setTextColor(context.getResources().getColor(R.color.colorMale));
                    cardViewHolder.noPhotoText.setTextColor( context.getResources().getColor( R.color.colorMale ) );
                    //cardViewHolder.locationIcon.setImageDrawable( getMaleIcon( result ) );


                    break;

                case "Female" :

                    cardViewHolder.petHeadImage.setBorderColorResource(R.color.colorFemaleCard);
                    cardViewHolder.petHeadImageTwo.setBorderColorResource(R.color.colorFemaleCard);
                    cardViewHolder.petHeadImage.setBorderWidth( 10 );
                    cardViewHolder.petHeadImageTwo.setBorderWidth(10);

                    imageOne.placeholder( R.drawable.ic_load_2_female );
                    imageTwo.placeholder( R.drawable.ic_load_2_female );

                    cardViewHolder.backdrop.setBackgroundResource(R.drawable.round_card_female);

                    if ( result.getType().equals( "Dog" ) ) {
                        cardViewHolder.genderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dog_footprint_female));

                    }
                    else {
                        cardViewHolder.genderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_female_sign));

                    }
                    cardViewHolder.genderText.setText("F");
                    cardViewHolder.genderText.setTextColor(context.getResources().getColor(R.color.colorFemale));
                    cardViewHolder.noPhotoText.setTextColor(context.getResources().getColor(R.color.colorFemale));
                    //cardViewHolder.locationIcon.setImageDrawable(getFemaleIcon());

                    break;
            }


            imageOne.into( cardViewHolder.petHeadImage );
            imageTwo.into( cardViewHolder.petHeadImageTwo );

            String basicInfo = getBasicInfoText() + " " + result.getName() + "\nI am a " + result.getBreed();

            cardViewHolder.petHeadGreeting.setText( getGreetingText() );
            cardViewHolder.petHeadBasicInfo.setText( basicInfo );

            cardViewHolder.imageParent.setInAnimation(context, android.R.anim.fade_in);
            cardViewHolder.imageParent.setOutAnimation( context, android.R.anim.fade_out );
            cardViewHolder.imageParent.startFlipping();

        } else {

            loadMoreView = loadMoreView == null ? LayoutInflater.from( context ).inflate( R.layout.load_more_list_item, parent, false ) : loadMoreView;
            recycledView = loadMoreView;

            callback.loadMore();

        }

        return recycledView;
    }





    class PetCardViewHolder {
        public ViewFlipper imageParent;
        public TextView noPhotoText;
        public CircleImageView petHeadImage;
        public CircleImageView petHeadImageTwo;
        public ImageView genderIcon;
        public ImageView locationIcon;
        public TextView petHeadGreeting;
        public TextView petHeadBasicInfo;
        public TextView genderText;
        public View backdrop;
    }

}
