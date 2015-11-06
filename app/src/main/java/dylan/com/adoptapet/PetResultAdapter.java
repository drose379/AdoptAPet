package dylan.com.adoptapet;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.Random;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dylan on 11/4/15.
 */
public class PetResultAdapter extends BaseAdapter {

    private String[] greetingStarts = { "Hello", "Hi", "Hey There", "Hey", "Howdy", "Good Day", "Greetings" };
    private String[] greetingMiddle = { "I go by", "My name is", "Call me", "They Call Me" };

    private Random rand;

    private Context context;
    private ArrayList<PetResult> pets;

    public PetResultAdapter( Context context, ArrayList<PetResult> pets ) {
        this.context = context;
        this.pets = pets;

        rand = new Random();
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

    public String getGreetingText() {
        int startRandom = rand.nextInt( 6 );


        return greetingStarts[startRandom] + "!";
    }

    public String getBasicInfoText() {
        int middleRandom = rand.nextInt( 3 );

        return greetingMiddle[middleRandom];
    }


    @Override
    public View getView( int item, View recycledView, ViewGroup parent ) {
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
        TextView petHeadGreeting = (TextView) recycledView.findViewById( R.id.petHeadGreeting );
        TextView petHeadBasicInfo = (TextView) recycledView.findViewById( R.id.petHeadDescription );
        TextView genderText = (TextView) recycledView.findViewById( R.id.genderText );


        View backdrop =recycledView.findViewById(R.id.backdrop);

        //TODO:: Make sure the backdrop view has rounded top corners on all devices



        switch ( result.getSex() ) {
            case "Male" :

                petHeadImage.setBorderColorResource(R.color.colorMale);
                petHeadImageTwo.setBorderColorResource(R.color.colorMale);
                petHeadImage.setBorderWidth(6);
                petHeadImageTwo.setBorderWidth(6);

                backdrop.setBackgroundColor( Color.parseColor("#C5CAE9") );

                genderIcon.setImageResource( R.drawable.ic_dog_footprint_100_male);
                genderText.setText("M" );
                genderText.setTextColor( context.getResources().getColor( R.color.colorMale ) );

                break;

            case "Female" :

                petHeadImage.setBorderColorResource(R.color.colorFemale);
                petHeadImageTwo.setBorderColorResource(R.color.colorFemale);
                petHeadImage.setBorderWidth(6);
                petHeadImageTwo.setBorderWidth(6);

                backdrop.setBackgroundColor( Color.parseColor( "#F8BBD0" ) );

                genderIcon.setImageDrawable( context.getResources().getDrawable( R.drawable.ic_dog_footprint_female));
                genderText.setText("F");
                genderText.setTextColor( context.getResources().getColor( R.color.colorFemale ) );

                break;
        }


        imageOne.fit().into( petHeadImage );
        imageTwo.fit().into( petHeadImageTwo );

        String basicInfo = getBasicInfoText() + " " + result.getName() + "\nI am a " + result.getBreed();

        petHeadGreeting.setText( getGreetingText() );
        petHeadBasicInfo.setText( basicInfo );

        imageParent.setInAnimation(context, android.R.anim.fade_in);
        imageParent.setOutAnimation( context, android.R.anim.fade_out );
        imageParent.startFlipping();


        return recycledView;
    }

}
