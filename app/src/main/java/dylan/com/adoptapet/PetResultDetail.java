package dylan.com.adoptapet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dylan on 11/8/15.
 */
public class PetResultDetail extends AppCompatActivity implements View.OnClickListener {

    private PetResult currentPet;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.pet_detail);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) findViewById( R.id.toolbarTitle );
        ImageView toolbarBack = (ImageView) findViewById( R.id.toolbarBackButton );

        currentPet = (PetResult) getIntent().getSerializableExtra("pet");
        toolbarTitle.setText( currentPet.getName() + "\'s Details" );
        toolbarBack.setOnClickListener( this );

        initDetailLayout();
    }

    public void initDetailLayout() {

        //TODO:: Add a contact FAB to bottom right that inflates a AlertDialog with options: Email, Phone, Visit website, ( all if items supplied by API )

        LinearLayout topBackdrop = (LinearLayout) findViewById( R.id.topView );
        CircleImageView imageOne = (CircleImageView) findViewById( R.id.petHeadImageOne );
        CircleImageView imageTwo = (CircleImageView) findViewById( R.id.petHeadImageTwo );
        TextView nameText = (TextView) findViewById( R.id.nameText );
        TextView breedText = (TextView) findViewById( R.id.breedText );
        TextView ageText = (TextView) findViewById( R.id.ageText );
        TextView sizeText = (TextView) findViewById( R.id.sizeText );
        TextView description = (TextView) findViewById( R.id.description );
        TextView phoneNumber = (TextView) findViewById( R.id.phoneNumberText );
        TextView location = (TextView) findViewById( R.id.locationText );
        TextView email = (TextView) findViewById( R.id.emailText );

        ImageView phoneButton = (ImageView) findViewById( R.id.phoneButton );

        ViewFlipper imageContainer = (ViewFlipper) findViewById( R.id.imageContainer );


        Picasso.with( this ).load( currentPet.getBestPhoto( 1 ) ).fit().into(imageOne);
        Picasso.with( this ).load( currentPet.getBestPhoto( 2 ) ).fit().into(imageTwo);

        phoneButton.setOnClickListener( this );
        phoneNumber.setOnClickListener( this );

        switch ( currentPet.getSex() ) {
            case "Male" :
                topBackdrop.setBackgroundColor(getResources().getColor(R.color.colorMaleCard));
                imageOne.setBorderColorResource(R.color.colorMaleCard);
                imageTwo.setBorderColorResource(R.color.colorMaleCard);
                break;
            case "Female" :
                topBackdrop.setBackgroundColor(getResources().getColor(R.color.colorFemaleCard));
                imageOne.setBorderColorResource( R.color.colorFemaleCard );
                imageTwo.setBorderColorResource(R.color.colorFemaleCard);
                break;
        }


        imageContainer.setInAnimation(this, android.R.anim.fade_in);
        imageContainer.setOutAnimation(this, android.R.anim.fade_out);

        imageOne.setBorderWidth( 15 );
        imageTwo.setBorderWidth(15);
        nameText.setText( currentPet.getName() );

        String breeds = "";
        for( String breed : currentPet.getBreeds() ) {
            if ( !breed.equals( "null" ) ) {
                breeds += breed + "\n";
            }
        }

        breedText.setText( breeds );
        ageText.setText( currentPet.getAge() );
        sizeText.setText( currentPet.getSize() );
        description.setText( currentPet.getDescription() );
        phoneNumber.setText( currentPet.getContactNumber().isEmpty() ? "N/A" : currentPet.getContactNumber() );
        location.setText( currentPet.getLocationInfo() );
        email.setText( currentPet.getEmail() );

    }

    @Override
    public void onClick( View view ) {

        switch( view.getId() ) {

            case R.id.toolbarBackButton :
                finish();
                break;

            case R.id.phoneButton :
            case R.id.phoneNumberText :

                Intent dial = new Intent( Intent.ACTION_DIAL );
                dial.setData( Uri.parse("tel:" + currentPet.getContactNumber() ) );
                startActivity(dial);

                break;

        }

    }

}