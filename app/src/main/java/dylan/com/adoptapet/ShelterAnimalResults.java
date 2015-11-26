package dylan.com.adoptapet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dylan on 11/25/15.
 */
public class ShelterAnimalResults extends AppCompatActivity implements APIHelper.Callback, View.OnClickListener {

    private String shelterId;
    private String shelterName;
    private ListView animalsList;
    private ProgressBar loader;


    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.shelter_animals);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) findViewById( R.id.toolbarTitle );
        ImageView backButton = (ImageView) findViewById( R.id.toolbarBackButton );

        backButton.setOnClickListener( this );

        animalsList = (ListView) findViewById( R.id.results );
        loader = (ProgressBar) findViewById( R.id.loader );

        shelterId = getIntent().getStringExtra( "shelterId" );
        shelterName = getIntent().getStringExtra( "shelterName" );

        toolbarTitle.setText(shelterName);

        APIHelper.makeShelterAnimalsRequest( shelterId, this, new Handler() );

    }

    @Override
    public void getResults( final ArrayList<PetResult> results ) {
        PetResultAdapter adapter = new PetResultAdapter( this, false, results );
        animalsList.setAdapter( adapter );

        animalsList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView parent, View view, int which, long id ) {

                PetResult selectedAnimal = results.get( which );

                Intent details = new Intent( ShelterAnimalResults.this, PetResultDetail.class );
                details.putExtra( "pet", selectedAnimal );
                startActivity( details );

            }
        });

        loader.setVisibility(View.GONE);
        animalsList.setVisibility( View.VISIBLE );

    }

    @Override
    public void onClick( View v ) {

        switch ( v.getId() ) {

            case R.id.toolbarBackButton :
                finish();
                break;

            case R.id.searchButton :


                break;
        }

    }

}
