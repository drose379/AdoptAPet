package dylan.com.adoptapet;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dylan on 11/15/15.
 */
public class PetSearchDetails extends AppCompatActivity implements View.OnClickListener {

    private int animalType = 0;
    private String location = null;

    private ArrayList<String> selectedBreeds;

    private AlertDialog breedSelectDialog;

    private View root;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.pet_search_details);

        root = findViewById(R.id.root);

        animalType = getIntent().getIntExtra("type", 1);
        location = getIntent().getStringExtra( "location" );
        selectedBreeds = new ArrayList<String>();

        Button breedSelect = (Button) findViewById( R.id.breedSelectButton );
        FloatingActionButton searchFab = (FloatingActionButton) findViewById( R.id.searchFab );

        //set the breed select according to type

        breedSelect.setOnClickListener( this );
        searchFab.setOnClickListener( this );
    }

    @Override
    public void onClick( View v ) {

        switch( v.getId() ) {

            case R.id.breedSelectButton :

                View dialogView = LayoutInflater.from( this ).inflate( R.layout.breed_select_dialog, null );
                ListView breedList = (ListView) dialogView.findViewById(R.id.breedList);
                final AutoCompleteTextView breedSearch = (AutoCompleteTextView) dialogView.findViewById(R.id.breedSearch);
                final LinearLayout selectedBreeds = (LinearLayout) dialogView.findViewById(R.id.selectedBreeds);
                //selectedBreeds.setMovementMethod( new ScrollingMovementMethod() );


                final ArrayAdapter<String> adapter = new ArrayAdapter<String>( this,
                        R.layout.support_simple_spinner_dropdown_item );

                if ( animalType == 1 ) {
                    adapter.addAll( getResources().getStringArray( R.array.dog_breeds ) );
                } else {
                    adapter.addAll( getResources().getStringArray( R.array.cat_breeds ) );
                }


                breedSearch.setAdapter(adapter);

                breedSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View v, int item, long id) {
                        String selected = adapter.getItem(item);

                        breedSearch.setText("");

                        addSelectedBreed(selected, selectedBreeds);
                    }
                });

                breedSearch.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        breedSearch.setText("");
                    }
                });

                breedList.setAdapter(adapter);
                breedList.setFastScrollEnabled(true);

                breedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView parent, View view, int item, long id) {
                        String selected = getResources().getStringArray(R.array.dog_breeds)[item];
                        addSelectedBreed(selected, selectedBreeds);

                    }
                });

                breedSelectDialog = new AlertDialog.Builder( this )
                        .setView(dialogView)
                        .setPositiveButton("Save", null)
                        .setNegativeButton( "Cancel", null )
                        .setNeutralButton( "Clear", null )
                        .create();

                if ( animalType == 1 ) {
                    breedSelectDialog.setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.dog_breed_title, null ) );
                } else {
                    breedSelectDialog.setCustomTitle( LayoutInflater.from( this ).inflate( R.layout.cat_breed_title, null ) );
                }

                breedSelectDialog.show();

                breedSelectDialog.getButton( AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        breedSelectDialog.dismiss();
                        processSelectedBreeds();
                    }
                });

                breedSelectDialog.getButton( AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedBreeds.removeAllViews();
                        PetSearchDetails.this.selectedBreeds.clear();
                    }
                });

                breedSelectDialog.getButton( AlertDialog.BUTTON_NEGATIVE ).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        PetSearchDetails.this.selectedBreeds.clear();
                        breedSelectDialog.dismiss();
                    }
                });

                break;

            case R.id.searchFab :

                /**
                 * To grab:
                 *
                 * Location: postalBox.getText().toString()
                 * Breeds: this.selectedBreeds
                 * continue...
                 */

                CheckBox maleGender = (CheckBox) findViewById(R.id.male);
                CheckBox femaleGender = (CheckBox) findViewById(R.id.female);

                CheckBox sizeSmall = (CheckBox) findViewById(R.id.sm);
                CheckBox sizeMedium = (CheckBox) findViewById( R.id.md );
                CheckBox sizeLarge = (CheckBox) findViewById( R.id.lg );
                CheckBox sizeXL = (CheckBox) findViewById(R.id.xl);

                CheckBox ageBaby = (CheckBox) findViewById( R.id.baby );
                CheckBox ageYoung = (CheckBox) findViewById( R.id.young );
                CheckBox ageAdult = (CheckBox) findViewById(R.id.adult);
                CheckBox ageSenior = (CheckBox) findViewById(R.id.senior);

                CheckBox[] genderBoxes = { maleGender, femaleGender };
                CheckBox[] sizeBoxes = { sizeSmall, sizeMedium, sizeLarge, sizeXL };
                CheckBox[] ageBoxes = { ageBaby, ageYoung, ageAdult, ageSenior };

                JSONObject requestInfo = new JSONObject();

                try {

                    JSONArray genderSelected = new JSONArray();
                    JSONArray ageSelected = new JSONArray();
                    JSONArray sizeSelected = new JSONArray();

                    for ( CheckBox box : genderBoxes ) {
                        if ( box.isChecked() ) {
                            genderSelected.put( box.getText() );
                        }
                    }

                    for ( CheckBox box : sizeBoxes ) {
                        if ( box.isChecked() ) {
                            sizeSelected.put( box.getText() );
                        }
                    }

                    for ( CheckBox box : ageBoxes ) {
                        if ( box.isChecked() ) {
                            ageSelected.put(box.getText());
                        }
                    }

                    JSONArray breeds = new JSONArray( this.selectedBreeds.toArray() );

                    requestInfo.put( "location", location );
                    requestInfo.put( "type", animalType == 1 ? "dog" : "cat" );
                    requestInfo.put( "breeds", breeds );
                    requestInfo.put( "genders", genderSelected );
                    requestInfo.put( "sizes", sizeSelected );
                    requestInfo.put("ages", ageSelected);

                    if ( requestInfo.getString( "location" ).length() == 5 ) {

                        Intent i = new Intent( this, SearchResults.class );
                        i.putExtra( "searchItems", requestInfo.toString() );
                        startActivity( i );

                    } else {
                        Snackbar.make(root, "Please Specify a Location", Snackbar.LENGTH_SHORT).show();
                    }


                } catch ( JSONException e ) {
                    throw new RuntimeException( e.getMessage() );
                }

                break;

        }

    }


    private void addSelectedBreed( String selected, LinearLayout parent ) {
        //TODO:: Add to arraylist<String> of selected breeds for processing later, make sure to clear if user uses clear button

        if ( this.selectedBreeds.size() < 3 && !this.selectedBreeds.contains( selected ) ) {
            TextView newBreed = new TextView( this );
            newBreed.setText(selected);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins( 5, 5, 5, 5 );
            //TODO: Set padding to the text insdie the textview
            //TODO:: Instead of using a dialog for breed selection, create an entire activity, can offer the most options this way

            newBreed.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            newBreed.setTextColor(getResources().getColor(R.color.colorWhite));
            newBreed.setLayoutParams(layoutParams);

            parent.addView( newBreed, 0 );
            this.selectedBreeds.add( selected );
        } else {
            if ( this.selectedBreeds.size() == 3 ) {
                Toast.makeText( this, "Max Selection is 3", Toast.LENGTH_SHORT ).show();
            } else {
                Toast.makeText( this, "Already Selected This Breed", Toast.LENGTH_SHORT ).show();
            }

        }
    }

    private void processSelectedBreeds() {
        //TODO:: get selected breeds, add them to the listview below the SELECT BREEDS button, listview should have height wrap_content
        ArrayList<String> selected = new ArrayList<String>( this.selectedBreeds);
        ArrayAdapter<String> breedsAdapter = new ArrayAdapter<String>( this, R.layout.breed_list_item, R.id.title, selected );
        ListView selectedBreedsList = (ListView) findViewById(R.id.selectedBreedsList);


        selectedBreedsList.setAdapter(breedsAdapter);
        selectedBreedsList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView parent, View view, int item, long id  ) {
                breedSelectDialog.show();
            }
        });

        //TODO:: Fix weird grey box under the button if a list of breeds is cleared and button re-shows

        if ( selectedBreeds.size() > 0 ) {
            LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            selectedBreedsList.setLayoutParams(listParams);
            selectedBreedsList.setBackgroundColor(getResources().getColor(R.color.colorBackgroundDark));
            //breedSelectButton.setVisibility(View.GONE);
            selectedBreedsList.setVisibility(View.VISIBLE);
        } else {
            //breedSelectButton.setVisibility( View.VISIBLE );
            selectedBreedsList.setVisibility( View.GONE );
        }

        //if ( selectedBreeds.size() > 0 ) {breedSelectButton.setVisibility( View.GONE ); };

    }

}
