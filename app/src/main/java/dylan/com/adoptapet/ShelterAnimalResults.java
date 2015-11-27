package dylan.com.adoptapet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

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

        animalsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int which, long id) {

                PetResult selectedAnimal = results.get(which);

                Intent details = new Intent(ShelterAnimalResults.this, PetResultDetail.class);
                details.putExtra("pet", selectedAnimal);
                startActivity(details);

            }
        });

        loader.setVisibility(View.GONE);
        animalsList.setVisibility(View.VISIBLE);

        initAnimalTypeSelect( results );

    }

    public void initAnimalTypeSelect( final ArrayList<PetResult> results ) {
        /**
         * Looks over each type of animal in list, populates the dropdown to filter animal types
         */

        final ArrayList<PetResult> master = new ArrayList<PetResult>( results ); // Why is this necessary, if using arraylist passed in, it loses items

        Spinner typeOptions = (Spinner) findViewById( R.id.typeSelect );

        ArrayList<String> types = new ArrayList<String>();
        types.add("Any");

        for ( PetResult item : master ) {
            if ( !types.contains( item.getType() ) )
                types.add( item.getType() );
        }



        //typeOptions.setSelection( types.size() - 1 );

        final String[] finalTypes = new String[types.size()];
        ArrayAdapter<String> adapter = new ArrayAdapter<>( this, android.R.layout.simple_dropdown_item_1line, types.toArray( finalTypes ) );

        typeOptions.setAdapter( adapter );

        typeOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String type = finalTypes[position];

                ArrayList<PetResult> tempItems = new ArrayList<PetResult>();


                for ( PetResult item : master ) {
                    if ( item.getType().equals( type ) ) {
                        tempItems.add( item );
                    }
                    else if ( type.equals( "Any" ) )
                        tempItems.add( item );
                }

                Log.i("MASTERSIZE", String.valueOf( master.size() ));

                PetResultAdapter adapter = (PetResultAdapter) animalsList.getAdapter();
                adapter.updateAnimalType( tempItems );

                animalsList.smoothScrollToPosition( 0 );

            }

            @Override
            public void onNothingSelected(AdapterView parent) {

            }

        });

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
