package dylan.com.adoptapet;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
    private View rootView;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.pet_detail);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) findViewById( R.id.toolbarTitle );
        ImageView toolbarBack = (ImageView) findViewById(R.id.toolbarBackButton);
        ImageView shareButton = (ImageView) findViewById( R.id.shareButton );

        rootView = findViewById( R.id.root );

        currentPet = (PetResult) getIntent().getSerializableExtra("pet");
        toolbarTitle.setText( currentPet.getName() + "\'s Details" );
        toolbarBack.setOnClickListener(this);

        shareButton.setOnClickListener( this );

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
        ImageView navButton = (ImageView) findViewById( R.id.navButton );
        ImageView mailIcon = (ImageView) findViewById( R.id.emailIcon );

        ViewFlipper imageContainer = (ViewFlipper) findViewById( R.id.imageContainer );


        Picasso.with( this ).load( currentPet.getBestPhoto( 1 ) ).fit().into(imageOne);
        Picasso.with( this ).load( currentPet.getBestPhoto( 2 ) ).fit().into(imageTwo);

        phoneButton.setOnClickListener( this );
        phoneNumber.setOnClickListener( this );
        navButton.setOnClickListener( this );
        location.setOnClickListener( this );
        mailIcon.setOnClickListener( this );
        email.setOnClickListener( this );

        switch ( currentPet.getSex() ) {
            case "Male" :
                topBackdrop.setBackgroundColor(getResources().getColor(R.color.colorMaleCard));
                imageOne.setBorderColorResource(R.color.colorMaleCard);
                imageTwo.setBorderColorResource(R.color.colorMaleCard);
                break;
            case "Female" :
                topBackdrop.setBackgroundColor(getResources().getColor(R.color.colorFemaleCard));
                imageOne.setBorderColorResource(R.color.colorFemaleCard);
                imageTwo.setBorderColorResource(R.color.colorFemaleCard);
                break;
        }


        imageContainer.setInAnimation( this, android.R.anim.fade_in );
        imageContainer.setOutAnimation( this, android.R.anim.fade_out );

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

        if ( currentPet.getBestPhoto( 2 ) != null ) {
            imageContainer.startFlipping();
        }

    }

    @Override
    public void onClick( View view ) {

        switch( view.getId() ) {

            case R.id.toolbarBackButton :
                finish();
                break;

            case R.id.phoneButton :
            case R.id.phoneNumberText :

                if ( !currentPet.getContactNumber().isEmpty() ) {
                    Intent dial = new Intent( Intent.ACTION_DIAL );
                    dial.setData( Uri.parse("tel:" + currentPet.getContactNumber() ) );
                    startActivity(dial);
                } else {
                    Snackbar.make( rootView, "No phone number supplied by this organization", Snackbar.LENGTH_SHORT ).show();
                }

                break;

            case R.id.navButton :
            case R.id.locationText :

                Uri mapUri = Uri.parse( "geo:0,0?q=" + currentPet.getLocationInfo()  );
                Intent map = new Intent( Intent.ACTION_VIEW, mapUri );
                map.setPackage( "com.google.android.apps.maps" );
                startActivity( map );

                break;

            case R.id.emailIcon :
            case R.id.emailText :

                if ( !currentPet.getEmail().isEmpty() ) {

                    Intent mail = new Intent( Intent.ACTION_SENDTO );
                    mail.setData( Uri.parse( "mailto:" + currentPet.getEmail() ) );
                    mail.putExtra( Intent.EXTRA_SUBJECT, "Interested in - " + currentPet.getName() );

                    startActivity( mail );

                } else {
                    Snackbar.make( rootView, "No email provided by this organization", Snackbar.LENGTH_SHORT ).show();
                }

                break;

            case R.id.shareButton :

                AlertDialog shareDialog = new AlertDialog.Builder( this )
                        .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.share_title, null ) )
                        .setItems(new CharSequence[]{"Text Message", "E-Mail"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick( DialogInterface dialog, int which ) {

                                switch ( which ) {
                                    case 0 :

                                        break;
                                    case 1 :

                                        break;
                                }

                            }
                        })
                        .setNegativeButton( "Cancel", null )
                        .create();

                shareDialog.show();

                break;

        }

    }

}