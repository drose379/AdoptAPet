package dylan.com.adoptapet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by dylan on 12/4/15.
 */
public class OptionsSelectFrag extends Fragment implements View.OnClickListener {

    private Context context;
    private ArrayList<String> selectedBreeds;

    private AlertDialog breedSelectDialog;

    private View root;

    private Button showOptions;
    private LinearLayout moreOptions;
    private ScrollView scrollParent;

    private AlertDialog ageDialog;
    private AlertDialog sizeDialog;

    private int selectedType;

    @Override
    public void onAttach( Context context ) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);


        selectedBreeds = new ArrayList<String>();

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        View v = inflater.inflate( R.layout.main_layout_2, parent, false );

        LinearLayout ageButton = (LinearLayout) v.findViewById( R.id.ageButton );
        LinearLayout sizeButton = (LinearLayout) v.findViewById( R.id.sizeButton );
/**
        moreOptions = (LinearLayout) v.findViewById( R.id.moreOptions );
        showOptions = (Button) v.findViewById( R.id.showMoreOptions );
        scrollParent = (ScrollView) v.findViewById( R.id.scroller );

        root = v.findViewById(R.id.root);


        Button breedSelect = (Button) v.findViewById(R.id.breedSelectButton);


        breedSelect.setOnClickListener(this);
        showOptions.setOnClickListener(this);
*/

        ageButton.setOnClickListener( this );
        sizeButton.setOnClickListener(this);

        OptionsTagCallback viewParent = (OptionsTagCallback) getActivity();
        viewParent.getTag(getTag());



        return v;
    }

    @Override
    public void onClick( View v ) {
        /**
        switch( v.getId() ) {

            case R.id.breedSelectButton :

                View dialogView = LayoutInflater.from( context ).inflate( R.layout.breed_select_dialog, null );
                ListView breedList = (ListView) dialogView.findViewById(R.id.breedList);
                final AutoCompleteTextView breedSearch = (AutoCompleteTextView) dialogView.findViewById(R.id.breedSearch);
                final LinearLayout selectedBreeds = (LinearLayout) dialogView.findViewById(R.id.selectedBreeds);
                //selectedBreeds.setMovementMethod( new ScrollingMovementMethod() );


                final ArrayAdapter<String> adapter = new ArrayAdapter<String>( context,
                        R.layout.support_simple_spinner_dropdown_item );

                if ( ((MainActivity)getActivity()).selectedType == 1 ) {
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

                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

                breedSelectDialog = new AlertDialog.Builder( context )
                        .setView(dialogView)
                        .setPositiveButton("Save", null)
                        .setNegativeButton( "Cancel", null )
                        .setNeutralButton( "Clear", null )
                        .create();

                if ( ((MainActivity)getActivity()).selectedType == 1 ) {
                    breedSelectDialog.setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.dog_breed_title, null ) );
                } else {
                    breedSelectDialog.setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.cat_breed_title, null ) );
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
                        OptionsSelectFrag.this.selectedBreeds.clear();
                    }
                });

                breedSelectDialog.getButton( AlertDialog.BUTTON_NEGATIVE ).setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        OptionsSelectFrag.this.selectedBreeds.clear();
                        breedSelectDialog.dismiss();
                    }
                });

                break;

            case R.id.showMoreOptions :

                /**
                 * TODO: Align the Select Breeds button to the left, and the More Options button to the the right of it
                 * Keep the more options in the same spot


                switch( moreOptions.getVisibility() ) {
                    case View.VISIBLE :
                        moreOptions.animate().alpha(0);
                        moreOptions.setVisibility(View.GONE);
                        break;
                    case View.GONE :
                        moreOptions.setVisibility(View.VISIBLE);
                        moreOptions.animate().alpha(1);

                        moreOptions.post(new Runnable() {
                            @Override
                            public void run() {
                                scrollParent.fullScroll( View.FOCUS_DOWN );
                            }
                        });

                        break;
                }


                break;
        }

         */


        switch ( v.getId() ) {
            case R.id.ageButton :
                inflateAgeDialog();
                break;
            case R.id.sizeButton :
                inflateSizeDialog();
                break;
        }


    }


    private void inflateAgeDialog() {
        if ( ageDialog == null ) {
            ageDialog = new AlertDialog.Builder( context )
                    .setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.age_select_title, null ) )
                    .setView( LayoutInflater.from( context ).inflate( R.layout.age_select_layout, null ) )
                    .setPositiveButton( "Save", null )
                    .create();
        }

        ageDialog.show();

        //TODO:: FOR ALL DIALOGS: Make sure if anything else other then Any is clicked in the dialog, to un-check "any"d
        //TODO:: When items are selected from a certain button, change the button shadow to "selected"
        //TODO:: Show the "More" options right below the currnet buttons

        ageDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Save items
                 * Dismiss ageDialog
                 */
            }
        });


    }

    private void inflateSizeDialog() {

        if ( sizeDialog == null ) {
            sizeDialog = new AlertDialog.Builder( context )
                    .setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.size_title, null ) )
                    .setView( LayoutInflater.from( context ).inflate( R.layout.size_select_layout, null ) )
                    .setPositiveButton( "Save", null )
                    .create();

        }

        sizeDialog.show();

        sizeDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                /**
                 * save, dismiss
                 */
            }
        });

    }


    private void addSelectedBreed( String selected, LinearLayout parent ) {
        //TODO:: Add to arraylist<String> of selected breeds for processing later, make sure to clear if user uses clear button

        if ( this.selectedBreeds.size() < 3 && !this.selectedBreeds.contains( selected ) ) {
            TextView newBreed = new TextView( context );
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
                Toast.makeText( context, "Max Selection is 3", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText( context, "Already Selected This Breed", Toast.LENGTH_SHORT ).show();
            }

        }
    }

    private void processSelectedBreeds() {
        //TODO:: get selected breeds, add them to the listview below the SELECT BREEDS button, listview should have height wrap_content
        ArrayList<String> selected = new ArrayList<String>( this.selectedBreeds);
        ArrayAdapter<String> breedsAdapter = new ArrayAdapter<String>( context, R.layout.breed_list_item, R.id.title, selected );
        ListView selectedBreedsList = (ListView) getView().findViewById(R.id.selectedBreedsList);


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
            //breedSelectButton.setVisibility(View.GONE);
            selectedBreedsList.setVisibility(View.VISIBLE);
        } else {
            //breedSelectButton.setVisibility( View.VISIBLE );
            selectedBreedsList.setVisibility( View.GONE );
        }

        //if ( selectedBreeds.size() > 0 ) {breedSelectButton.setVisibility( View.GONE ); };

    }

    public void updateSelectedType( int type ) {
        /**
        selectedType = type;
        shouldShowNoClaws();
         */
    }

    private void shouldShowNoClaws() {
        /**
        if ( ((MainActivity) getActivity()).selectedType == 1 )
            moreOptions.findViewById( R.id.clawsParent ).setVisibility( View.GONE );
        else {
            moreOptions.findViewById( R.id.clawsParent ).setVisibility( View.VISIBLE );
        }
         */
    }

    public ArrayList<String> getSelectedBreeds() {
        return selectedBreeds;
    }

}
