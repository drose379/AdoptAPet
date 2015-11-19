package dylan.com.adoptapet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
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

    private ImageView favoriteButton;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.pet_detail);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) findViewById( R.id.toolbarTitle );
        ImageView toolbarBack = (ImageView) findViewById(R.id.toolbarBackButton);
        ImageView shareButton = (ImageView) findViewById( R.id.shareButton );
        favoriteButton = (ImageView) findViewById( R.id.favoriteButton );

        rootView = findViewById( R.id.root );

        currentPet = (PetResult) getIntent().getSerializableExtra("pet");
        toolbarTitle.setText( currentPet.getName() + "\'s Details" );
        toolbarBack.setOnClickListener(this);

        shareButton.setOnClickListener( this );
        favoriteButton.setOnClickListener( this );

        initDetailLayout();
        initFavoriteStatus();
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
        phoneNumber.setText( currentPet.getContactNumber().length() < 10 ? "N/A" : currentPet.getContactNumber() );
        location.setText( currentPet.getLocationInfo() );
        email.setText( currentPet.getEmail() );

        if ( currentPet.getBestPhoto( 2 ) != null ) {
            imageContainer.startFlipping();
        }

    }

    public void initFavoriteStatus() {
        SQLiteDatabase readable = new FavoritesDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT * FROM favorites WHERE id = :id", new String[] { currentPet.getId() } );

        if ( result.getCount() == 0 ) {
            favoriteButton.setImageDrawable( getResources().getDrawable( R.drawable.ic_favorite_border_white_24dp ) );
        } else {
            favoriteButton.setImageDrawable( getResources().getDrawable( R.drawable.ic_favorite_white_24dp ) );
        }

        readable.close();

    }

    @Override
    public void onClick( View view ) {

        switch( view.getId() ) {

            case R.id.toolbarBackButton :
                finish();
                break;

            case R.id.phoneButton :
            case R.id.phoneNumberText :

                String number = currentPet.getContactNumber().trim();

                boolean isDialable = number.length() >= 10 && number.length() <= 12;

                if ( isDialable ) {
                    Intent dial = new Intent( Intent.ACTION_DIAL );
                    dial.setData( Uri.parse("tel:" + number ) );
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

                                String gender = currentPet.getSex().equals( "Male" ) ? "him" : "her";
                                String url = "https://www.petfinder.com/petdetail/" + currentPet.getId();
                                String message = "I thought you may be interested in adopting " + currentPet.getName() +
                                        ", you can find " + gender + " here: " + url;

                                switch (which) {
                                    case 0:
                                        Intent sms = new Intent( Intent.ACTION_SENDTO );
                                        sms.setData( Uri.parse( "smsto:" ) );
                                        sms.putExtra( "sms_body", message ); //edit the message with the correct link with getId()
                                        sms.putExtra( "exit_on_sent", true ); //test
                                        startActivity( sms );
                                        break;
                                    case 1:
                                        Intent mail = new Intent( Intent.ACTION_SENDTO );
                                        mail.setData( Uri.parse( "mailto:" ) );
                                        mail.putExtra( Intent.EXTRA_TEXT, message);
                                        startActivity( mail );
                                        break;
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();

                shareDialog.show();

                break;

            case R.id.favoriteButton :

                SQLiteDatabase readable = new FavoritesDBHelper( this ).getReadableDatabase();
                Cursor result = readable.rawQuery( "SELECT * FROM favorites WHERE id = :id", new String[] { currentPet.getId() } );

                if ( result.getCount() == 0 ) {

                    AlertDialog confirmFavorite = new AlertDialog.Builder( this )
                            .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.favorite_title, null ) )
                            .setMessage( "Would you like to add " + currentPet.getName() + " to your favorites?" )
                            .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    addToFavorites();
                                    initFavoriteStatus();
                                }
                            })
                            .setNegativeButton( "Cancel", null )
                            .create();

                    confirmFavorite.show();

                } else {

                    AlertDialog confirmUnfavorite = new AlertDialog.Builder( this )
                            .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.unfavorite_title ,null ) )
                            .setMessage( "Would you like to remove " + currentPet.getName() + " from your favorites?" )
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    removeFromFavorites();
                                    initFavoriteStatus();
                                }
                            })
                            .setNegativeButton( "No", null )
                            .create();

                    confirmUnfavorite.show();

                }

                readable.close();

                break;
        }

    }

    public void addToFavorites() {
        ContentValues vals = new ContentValues();
        vals.put( FavoritesDBHelper.type_col, currentPet.getType() );
        vals.put( FavoritesDBHelper.id_col, currentPet.getId() );
        vals.put( FavoritesDBHelper.name_col, currentPet.getName() );
        vals.put( FavoritesDBHelper.photo_col, currentPet.getBestPhoto(1) );

        SQLiteDatabase writeable = new FavoritesDBHelper( this ).getWritableDatabase();
        writeable.insert( FavoritesDBHelper.table_name, null, vals );

        writeable.close();
    }

    public void removeFromFavorites() {
        SQLiteDatabase writeable = new FavoritesDBHelper( this ).getWritableDatabase();
        writeable.delete( FavoritesDBHelper.table_name, "id = ?", new String[] { currentPet.getId() } );
        writeable.close();
    }

}