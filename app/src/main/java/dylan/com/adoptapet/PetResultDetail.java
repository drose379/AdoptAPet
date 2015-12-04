package dylan.com.adoptapet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.*;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by dylan on 11/8/15.
 */
public class PetResultDetail extends AppCompatActivity implements View.OnClickListener {

    private PetResult currentPet;
    private View rootView;

    private ImageView favoriteButton;

    private ViewFlipper imageContainer;

    private MenuItem favoriteMenuItem;

    //TODO:: The menu button looks as if it has more padding then the rest of the buttons, look into this
    //TODO:: Implement the More from this shelter button functionality, descripbed in G.O.D file

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.pet_detail);

        if ( getIntent().getSerializableExtra( "pet" ) != null )
            currentPet = (PetResult) getIntent().getSerializableExtra("pet");
        else
            finish();

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        getSupportActionBar().setTitle(currentPet.getName() + "\'s details");

        rootView = findViewById( R.id.root );

        initDetailLayout();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater();

        SQLiteDatabase readable = new FavoritesDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery("SELECT * FROM favorites WHERE id = :id", new String[]{currentPet.getId()});

        if ( result.getCount() == 0 ) {
            inflater.inflate( R.menu.pet_detail_toolbar_items, menu );
        } else {
            inflater.inflate(R.menu.pet_detail_toolbar_favorited, menu);
        }

        favoriteMenuItem = menu.getItem( 0 );

        Log.i("MORE_BUTTON", menu.getItem( 2 ).getTitle().toString() );

        readable.close();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected( android.view.MenuItem item ) {
        switch ( item.getItemId() ) {
            case android.R.id.home :
                finish();
                return true;

            case R.id.favoriteItem :

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


                return true;

            case R.id.shareIcon :


                AlertDialog shareDialog = new AlertDialog.Builder( this )
                        .setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.share_title, null ) )
                        .setItems(new CharSequence[]{"Text Message", "E-Mail", "Copy URL To Clipboard"}, new DialogInterface.OnClickListener() {
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
                                    case 2:
                                        ClipboardManager clipboard = (ClipboardManager) getSystemService( CLIPBOARD_SERVICE );
                                        ClipData clipData = ClipData.newPlainText( "Pet", url );
                                        clipboard.setPrimaryClip( clipData );

                                        Snackbar.make( rootView, "Copied!", Snackbar.LENGTH_SHORT ).show();
                                        break;
                                }

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();

                shareDialog.show();


                return true;

            case R.id.moreButton :

                /**
                 * Get the current pets shelterID, pass that through intent to ShelterAnimalResults
                 */

                Intent shelterAnimals = new Intent( this, ShelterAnimalResults.class );

                if ( currentPet.getShelterId() != null && !currentPet.getShelterId().isEmpty() ) {
                    shelterAnimals.putExtra( "shelterId", currentPet.getShelterId() );
                    startActivity( shelterAnimals );
                } else {
                    Snackbar.make( findViewById( R.id.root ), "Cannot Find Shelter", Snackbar.LENGTH_SHORT ).show();
                }

                break;

        }
        return super.onOptionsItemSelected( item );
    }

    public void initDetailLayout() {

        //TODO:: Add a contact FAB to bottom right that inflates a AlertDialog with options: Email, Phone, Visit website, ( all if items supplied by API )

        LinearLayout topBackdrop = (LinearLayout) findViewById( R.id.topView );
        CircleImageView imageOne = (CircleImageView) findViewById( R.id.petHeadImageOne );
        CircleImageView imageTwo = (CircleImageView) findViewById( R.id.petHeadImageTwo );
        ImageView animalType = (ImageView) findViewById( R.id.animalTypeIcon );
        TextView noPhotoText = (TextView) findViewById( R.id.noPhotoText );
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

        imageContainer = (ViewFlipper) findViewById( R.id.imageContainer );

        RequestCreator imageOneLoad = Picasso.with( this ).load(currentPet.getBestPhoto(1));
        RequestCreator imageTwoLoad = Picasso.with( this ).load( currentPet.getBestPhoto( 2 ) );

        phoneButton.setOnClickListener(this);
        phoneNumber.setOnClickListener( this );
        navButton.setOnClickListener( this );
        location.setOnClickListener( this );
        mailIcon.setOnClickListener( this );
        email.setOnClickListener(this);
        imageContainer.setOnClickListener(this);

        switch ( currentPet.getSex() ) {
            case "Male" :
                topBackdrop.setBackgroundColor( getResources().getColor(R.color.colorMaleCard) );
                imageOne.setBorderColorResource(R.color.colorMaleCard);
                imageTwo.setBorderColorResource( R.color.colorMaleCard );
                noPhotoText.setTextColor(getResources().getColor(R.color.colorMale));

                imageOneLoad.placeholder(R.drawable.ic_load_2_male);
                imageTwoLoad.placeholder( R.drawable.ic_load_2_male );

                break;
            case "Female" :
                topBackdrop.setBackgroundColor( getResources().getColor(R.color.colorFemaleCard) );
                imageOne.setBorderColorResource( R.color.colorFemaleCard );
                imageTwo.setBorderColorResource(R.color.colorFemaleCard);
                noPhotoText.setTextColor(getResources().getColor(R.color.colorFemale));

                imageOneLoad.placeholder(R.drawable.ic_load_2_female);
                imageTwoLoad.placeholder(R.drawable.ic_load_2_female);

                break;
        }

        imageOneLoad.into( imageOne );
        imageTwoLoad.into( imageTwo );

        Log.i("Type", currentPet.getType());

        switch ( currentPet.getType() ) {
            case "Dog" :
                animalType.setImageDrawable( getResources().getDrawable(R.drawable.ic_dog_100) );
                break;
            case "Cat" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_cat_100 ) );
                break;
            case "Bird" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_bird_xs ) );
                break;
            case "Barnyard" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_barn_xs ) );
                break;
            case "Scales, Fins & Other" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_alligator_xs ) );
                break;
            case "Small & Furry" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_animation_xs ) );
                break;
            case "Pig" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_pig_xs ) );
                break;
            case "Rabbit" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_rabbit_xs ) );
                break;
            case "Horse" :
                animalType.setImageDrawable( getResources().getDrawable( R.drawable.ic_horse_xs ) );
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

        breedText.setText( breeds.trim() );
        ageText.setText( currentPet.getAge() );
        sizeText.setText( currentPet.getSize() );
        description.setText( currentPet.getDescription() );
        phoneNumber.setText( currentPet.getContactNumber().length() < 10 ? "N/A" : currentPet.getContactNumber() );
        location.setText( currentPet.getLocationInfo() );
        email.setText( currentPet.getEmail() );

        if ( currentPet.getBestPhoto( 2 ) != null ) {
            imageContainer.startFlipping();
        }
        else if ( currentPet.getBestPhoto( 1 ) == null && currentPet.getBestPhoto( 2 ) == null ) {
            imageContainer.setVisibility( View.GONE );
            noPhotoText.setVisibility( View.VISIBLE );
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



                break;

            case R.id.favoriteButton :



                break;

            case R.id.imageContainer :

                ImageView currentImage = (ImageView) imageContainer.getCurrentView();

                /**
                 * Show the image clicked in a larger view
                 */

                if ( currentImage != null ) {

                    /** Create string array of image(s), even if there is only 1 */

                    String[] imageUrls;
                    ArrayList<String> temp = new ArrayList<String>();

                    if ( currentPet.getBestPhoto( 1 ) != null ) {
                        temp.add( currentPet.getBestPhoto( 1 ) );
                    }
                    if ( currentPet.getBestPhoto( 2 ) != null ) {
                        temp.add( currentPet.getBestPhoto( 2 ) );
                    }

                    imageUrls = new String[temp.size()];
                    temp.toArray( imageUrls );


                    Intent fullImageView = new Intent( this, FullImageViewer.class );
                    fullImageView.putExtra( "images", imageUrls );
                    fullImageView.putExtra( "name", currentPet.getName() );
                    startActivity(fullImageView);

                }

                break;

        }

    }

    private void initFavoriteStatus() {
        SQLiteDatabase readable = new FavoritesDBHelper( this ).getReadableDatabase();
        Cursor result = readable.rawQuery( "SELECT * FROM favorites WHERE id = :id", new String[] { currentPet.getId() } );

        //invalidateOptionsMenu();

        if ( result.getCount() == 0 ) {
            favoriteMenuItem.setIcon( getResources().getDrawable( R.drawable.ic_favorite_border_white_24dp ) );
        } else {
            favoriteMenuItem.setIcon( getResources().getDrawable( R.drawable.ic_favorite_white_24dp ) );
        }



        readable.close();

    }

    public void addToFavorites() {
        ContentValues vals = new ContentValues();
        vals.put( FavoritesDBHelper.type_col, currentPet.getType() );
        vals.put( FavoritesDBHelper.id_col, currentPet.getId() );
        vals.put( FavoritesDBHelper.name_col, currentPet.getName() );
        vals.put( FavoritesDBHelper.photo_col, currentPet.getBestPhoto(1) );
        vals.put( FavoritesDBHelper.breed_col, currentPet.getBreedsRaw() );
        vals.put( FavoritesDBHelper.isMix_col, currentPet.isMix() );
        vals.put( FavoritesDBHelper.age_col, currentPet.getAge() );
        vals.put( FavoritesDBHelper.sex_col, currentPet.getSex() );
        vals.put( FavoritesDBHelper.size_col, currentPet.getSize() );
        vals.put( FavoritesDBHelper.description_col, currentPet.getDescription() );
        vals.put( FavoritesDBHelper.contactInfo_col, currentPet.getContactInfoRaw() );
        vals.put(FavoritesDBHelper.lastupdated_col, String.valueOf(System.currentTimeMillis()));



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