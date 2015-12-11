package dylan.com.adoptapet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by dylan on 12/4/15.
 */
public class OptionsSelectFrag extends Fragment implements View.OnClickListener {

    //TODO:: Add an ON / OFF indicator to right of the switches in Options, when notChecked, OFF, when checked, change to ON, make bold and to right of switch

    private Context context;
    private ArrayList<String> selectedBreeds;

    private AlertDialog breedSelectDialog;

    private View root;

    private Button showOptions;
    private LinearLayout moreOptions;
    private ScrollView scrollParent;

    private ImageView breedIcon;

    private AlertDialog ageDialog;
    private AlertDialog sizeDialog;
    private AlertDialog genderDialog;
    private AlertDialog breedDialog;

    private int selectedType;

    private JSONArray ageSelection;
    private JSONArray sizeSelection;
    private JSONArray genderSelection;
    private JSONArray optionsSelection;

    @Override
    public void onAttach( Context context ) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);


        selectedBreeds = new ArrayList<String>();

        ageSelection = new JSONArray();
        sizeSelection = new JSONArray();
        genderSelection = new JSONArray();
        optionsSelection = new JSONArray();

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        View v = inflater.inflate( R.layout.main_layout_2, parent, false );

        LinearLayout ageButton = (LinearLayout) v.findViewById( R.id.ageButton );
        LinearLayout sizeButton = (LinearLayout) v.findViewById( R.id.sizeButton );
        LinearLayout genderButton = (LinearLayout) v.findViewById( R.id.genderSelect );
        LinearLayout breedButton = (LinearLayout) v.findViewById( R.id.breedSelect );

        breedIcon = (ImageView) v.findViewById( R.id.breedButtonIcon );


        ageButton.setOnClickListener(this);
        sizeButton.setOnClickListener(this);
        genderButton.setOnClickListener( this );
        breedButton.setOnClickListener( this );

        OptionsTagCallback viewParent = (OptionsTagCallback) getActivity();
        viewParent.getTag(getTag());

        initSwitchListener( v );

        return v;
    }


    @Override
    public void onClick( View v ) {

        switch ( v.getId() ) {
            case R.id.ageButton:
                inflateAgeDialog();
                break;
            case R.id.sizeButton:
                inflateSizeDialog();
                break;
            case R.id.genderSelect:
                inflateGenderDialog();
                break;
            case R.id.breedSelect:
                inflateBreedDialog();
                break;
        }
    }

    private void initSwitchListener( View rootView ) {

        final TextView alteredIndicator = (TextView) rootView.findViewById( R.id.alteredIndicator );
        final TextView clawsIndicator = (TextView) rootView.findViewById( R.id.clawsIndicator );
        final TextView shotsIndicator = (TextView) rootView.findViewById( R.id.shotsIndicator );
        final TextView houseTrainedIndicator = (TextView) rootView.findViewById( R.id.trainedIndicator );
        final TextView dogsIndicator = (TextView) rootView.findViewById( R.id.dogsIndicator );
        final TextView catsIndicator = (TextView) rootView.findViewById( R.id.catsIndicator );
        final TextView kidsIndicator = (TextView) rootView.findViewById( R.id.kidsIndicator );
        final TextView specialIndicator = (TextView) rootView.findViewById( R.id.specialIndicator );

        CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton button, boolean isChecked ) {

                String selected = (String) button.getTag();

                switch ( selected ) {

                    case "altered" :

                        if ( isChecked )
                            alteredIndicator.setText( "ON" );
                        else
                            alteredIndicator.setText( "OFF" );

                        break;
                    case "noClaws" :

                        if ( isChecked )
                            clawsIndicator.setText( "ON" );
                        else
                            clawsIndicator.setText( "OFF" );

                        break;
                    case "hasShots" :

                        if ( isChecked )
                            shotsIndicator.setText( "ON" );
                        else
                            shotsIndicator.setText( "OFF" );

                        break;
                    case "housebroken" :

                        if ( isChecked )
                            houseTrainedIndicator.setText( "ON" );
                        else
                            houseTrainedIndicator.setText( "OFF" );

                        break;
                    case "noDogs" :

                        if ( isChecked )
                            dogsIndicator.setText( "ON" );
                        else
                        dogsIndicator.setText( "OFF" );

                        break;
                    case "noCats" :

                        if ( isChecked )
                            catsIndicator.setText( "ON" );
                        else
                        catsIndicator.setText( "OFF" );

                        break;
                    case "noKids" :

                        if ( isChecked )
                         kidsIndicator.setText( "ON" );
                        else
                            kidsIndicator.setText( "OFF" );

                        break;
                    case "specialNeeds" :

                        if ( isChecked )
                            specialIndicator.setText( "ON" );
                        else
                            specialIndicator.setText( "OFF" );

                        break;
                }

                if ( isChecked ) {
                    optionsSelection.put( selected );
                } else {
                    removeItemFromJSON( optionsSelection, selected );
                }

            }
        };

        SwitchCompat altered = (SwitchCompat) rootView.findViewById( R.id.alteredSwitch );
        altered.setTag("altered");
        SwitchCompat noClaws = (SwitchCompat) rootView.findViewById( R.id.clawsSwitch );
        noClaws.setTag( "noClaws" );
        SwitchCompat hasShots = (SwitchCompat) rootView.findViewById( R.id.shotsSwitch );
        hasShots.setTag( "hasShots" );
        SwitchCompat houseTrained = (SwitchCompat) rootView.findViewById( R.id.houseSwitch );
        houseTrained.setTag( "housebroken" );
        SwitchCompat goodWithDogs = (SwitchCompat) rootView.findViewById( R.id.dogsSwitch );
        goodWithDogs.setTag( "noDogs" );
        SwitchCompat goodWithCats = (SwitchCompat) rootView.findViewById( R.id.catsSwitch );
        goodWithCats.setTag("noCats" );
        SwitchCompat goodWithKids = (SwitchCompat) rootView.findViewById( R.id.kidsSwitch );
        goodWithKids.setTag( "noKids" );
        SwitchCompat specialNeeds = (SwitchCompat) rootView.findViewById( R.id.specialSwitch );
        specialNeeds.setTag( "specialNeeds" );

        altered.setOnCheckedChangeListener( switchListener );
        noClaws.setOnCheckedChangeListener( switchListener );
        hasShots.setOnCheckedChangeListener( switchListener );
        houseTrained.setOnCheckedChangeListener( switchListener );
        goodWithDogs.setOnCheckedChangeListener( switchListener );
        goodWithCats.setOnCheckedChangeListener(switchListener);
        goodWithKids.setOnCheckedChangeListener( switchListener );
        specialNeeds.setOnCheckedChangeListener( switchListener );


    }


    private void inflateAgeDialog() {

        final View dialogView = LayoutInflater.from( context ).inflate( R.layout.age_select_layout, null );

        initAgeBoxListener(dialogView);

        if ( ageDialog == null ) {
            ageDialog = new AlertDialog.Builder( context )
                    .setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.age_select_title, null ) )
                    .setView( dialogView )
                    .setPositiveButton( "Close", null )
                    .create();
        }

        ageDialog.show();

        ageDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Save items
                 * Dismiss ageDialog
                 */

                ageDialog.dismiss();

            }
        });


    }

    private void clearAgeSelections() {
        //TODO:: Inflate the age dialog view, clear each checkbox, MUST LEAVE "Any" CHECKED

        //TODO:: Call this and same methods for other dialogs whenever the animal type changes
    }

    private void clearSizeSelections() {

    }

    private void clearGenderSelection() {

    }


    private void inflateSizeDialog() {

        View dialogView = LayoutInflater.from( context ).inflate( R.layout.size_select_layout, null );

        initSizeBoxListener(dialogView);

        if ( sizeDialog == null ) {
            sizeDialog = new AlertDialog.Builder( context )
                    .setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.size_title, null ) )
                    .setView( dialogView )
                    .setPositiveButton( "Close", null )
                    .create();

        }

        sizeDialog.show();

        sizeDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                sizeDialog.dismiss();
            }
        });

    }

    private void inflateGenderDialog() {

        View dialogView = LayoutInflater.from( context ).inflate( R.layout.gender_select_layout, null );

        initGenderBoxListener(dialogView);

        if ( genderDialog == null ) {
            genderDialog = new AlertDialog.Builder( context )
                    .setCustomTitle( LayoutInflater.from( context ).inflate( R.layout.gender_title, null )  )
                    .setView( dialogView )
                    .setPositiveButton( "Close", null )
                    .create();
        }

        genderDialog.show();

        genderDialog.getButton( AlertDialog.BUTTON_POSITIVE ).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick( View v ) {

                genderDialog.dismiss();

            }
        });

    }

    private void inflateBreedDialog() {

        View dialogView = LayoutInflater.from( context ).inflate( R.layout.breed_select_dialog, null );
        ListView breedList = (ListView) dialogView.findViewById(R.id.breedList);
        final AutoCompleteTextView breedSearch = (AutoCompleteTextView) dialogView.findViewById(R.id.breedSearch);
        final LinearLayout selectedBreeds = (LinearLayout) dialogView.findViewById(R.id.selectedBreeds);

        if ( this.selectedBreeds.size() > 0 ) {
            for ( String existing : this.selectedBreeds ) {
                showExistingBreedsInLayout(existing, selectedBreeds);
            }
        }


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

                String selected = "";

                if (selectedType == 1)
                    selected = getResources().getStringArray(R.array.dog_breeds)[item];
                else
                    selected = getResources().getStringArray(R.array.cat_breeds)[item];

                addSelectedBreed(selected, selectedBreeds);
            }
        });

        breedSelectDialog = new AlertDialog.Builder( context )
                .setView(dialogView)
                .setPositiveButton("Close", null)
                .setNegativeButton("Clear", null)
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


        breedSelectDialog.getButton( AlertDialog.BUTTON_NEGATIVE ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedBreeds.removeAllViews();
                OptionsSelectFrag.this.selectedBreeds.clear();
            }
        });

    }

    private void initAgeBoxListener( View dialogView  ) {

        final CheckBox any = (CheckBox) dialogView.findViewById( R.id.anyAge );
        final CheckBox baby = (CheckBox) dialogView.findViewById( R.id.babyAge );
        final CheckBox young = (CheckBox) dialogView.findViewById( R.id.youngAge );
        final CheckBox adult = (CheckBox) dialogView.findViewById( R.id.adultAge );
        final CheckBox senior = (CheckBox) dialogView.findViewById( R.id.seniorAge );

        CompoundButton.OnCheckedChangeListener boxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {

                switch( button.getText().toString() ) {
                    case "Any" :

                        /** Uncheck all others */
                        if ( isChecked ) {
                            baby.setChecked( false );
                            young.setChecked( false );
                            adult.setChecked( false );
                            senior.setChecked( false );
                            ageSelection.put( "Any" );
                        } else {
                            removeItemFromJSON( ageSelection, "Any" );
                        }

                        break;
                    case "Baby" :
                        if ( isChecked ) {
                            any.setChecked( false );
                            ageSelection.put( "Baby" );
                        } else {
                            removeItemFromJSON( ageSelection, "Baby" );
                        }
                        break;
                    case "Young" :
                        if ( isChecked ) {
                            any.setChecked(false);
                            ageSelection.put( "Young" );
                        } else {
                            removeItemFromJSON( ageSelection, "Young" );
                        }
                        break;
                    case "Adult" :
                        if ( isChecked ) {
                            any.setChecked(false);
                            ageSelection.put( "Adult" );
                        } else {
                            removeItemFromJSON( ageSelection, "Adult" );
                        }
                        break;
                    case "Senior" :
                        if ( isChecked ) {
                            any.setChecked( false );
                            ageSelection.put( "Senior" );
                        } else {
                            removeItemFromJSON( ageSelection, "Senior" );
                        }
                        break;
                }

            }
        };


        any.setOnCheckedChangeListener( boxListener );
        baby.setOnCheckedChangeListener(boxListener);
        young.setOnCheckedChangeListener(boxListener);
        adult.setOnCheckedChangeListener(boxListener);
        senior.setOnCheckedChangeListener(boxListener);

    }

    private void initSizeBoxListener( View dialogView ) {

        final CheckBox any = (CheckBox) dialogView.findViewById( R.id.anySize );
        final CheckBox small = (CheckBox) dialogView.findViewById( R.id.smallSize );
        final CheckBox medium = (CheckBox) dialogView.findViewById( R.id.mediumSize );
        final CheckBox large = (CheckBox) dialogView.findViewById( R.id.largeSize );
        final CheckBox extraLarge = (CheckBox) dialogView.findViewById( R.id.extraLargeSize );

        CompoundButton.OnCheckedChangeListener boxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {

                switch( button.getText().toString() ) {
                    case "Any" :

                        /** Uncheck all others */
                        if ( isChecked ) {
                            small.setChecked(false);
                            medium.setChecked(false);
                            large.setChecked(false);
                            extraLarge.setChecked(false);
                            sizeSelection.put( "Any" );
                        } else {
                            removeItemFromJSON( sizeSelection, "Any" );
                        }
                        break;
                    case "Small" :
                        if ( isChecked ) {
                            any.setChecked(false);
                            sizeSelection.put( "Small" );
                        } else {
                            removeItemFromJSON( sizeSelection, "Small" );
                        }
                        break;
                    case "Medium" :
                        if ( isChecked ) {
                            any.setChecked(false);
                            sizeSelection.put( "Medium" );
                        } else {
                            removeItemFromJSON( sizeSelection, "Medium" );
                        }
                        break;
                    case "Large" :
                        if ( isChecked ) {
                            any.setChecked(false);
                            sizeSelection.put( "Large" );
                        } else {
                            removeItemFromJSON( sizeSelection, "Large" );
                        }
                        break;
                    case "Extra Large" :
                        if ( isChecked ) {
                            any.setChecked( false );
                            sizeSelection.put("Extra Large");
                        } else {
                            removeItemFromJSON( sizeSelection, "Extra Large" );
                        }
                        break;
                }
            }
        };

        any.setOnCheckedChangeListener( boxListener );
        small.setOnCheckedChangeListener( boxListener );
        medium.setOnCheckedChangeListener( boxListener );
        large.setOnCheckedChangeListener(boxListener);
        extraLarge.setOnCheckedChangeListener(boxListener);


    }

    private void initGenderBoxListener( View dialogView ) {

        final CheckBox any = (CheckBox) dialogView.findViewById( R.id.anyGender );
        final CheckBox male = (CheckBox) dialogView.findViewById( R.id.maleGender );
        final CheckBox female = (CheckBox) dialogView.findViewById( R.id.femaleGender );

        CompoundButton.OnCheckedChangeListener boxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged( CompoundButton button, boolean isChecked ) {

                switch( button.getText().toString() ) {
                    case "Any" :

                        if ( isChecked ) {
                            male.setChecked( false );
                            female.setChecked( false );
                            genderSelection.put( "Any" );
                        } else {
                            removeItemFromJSON( genderSelection, "Any" );
                        }
                        break;
                    case "Male" :

                        if ( isChecked ) {
                            any.setChecked( false );
                            genderSelection.put( "Male" );
                            removeItemFromJSON( genderSelection, "Any" );
                        } else {
                            removeItemFromJSON( genderSelection, "Male" );
                        }
                        break;
                    case "Female" :

                        if ( isChecked ) {
                            any.setChecked( false );
                            genderSelection.put( "Female" );
                            removeItemFromJSON( genderSelection, "Any" );
                        } else {
                            removeItemFromJSON( genderSelection, "Female" );
                        }
                        break;
                }

            }
        };

        any.setOnCheckedChangeListener( boxListener );
        male.setOnCheckedChangeListener( boxListener );
        female.setOnCheckedChangeListener(boxListener);

    }

    public JSONArray getAgeSelection() {

        removeItemFromJSON(ageSelection, "Any");

        return ageSelection;
    }

    public JSONArray getSizeSelection() {

        removeItemFromJSON( sizeSelection, "Any" );

        return sizeSelection;
    }

    public JSONArray getGenderSelection() {

        removeItemFromJSON( genderSelection, "Any" );

        return genderSelection;
    }

    public ArrayList<String> getBreedSelection() {
        return selectedBreeds;
    }

    public JSONArray getOptionsSelection() {

        //TODO:: Main must work with the options

        return optionsSelection;
    }

    private void removeItemFromJSON( JSONArray array, String itemToRemove ) {
        try {
            for (int i = 0; i < array.length(); i++) {
                if (array.getString(i).equals(itemToRemove)) {
                    array.remove( i );
                }
            }
        } catch ( JSONException e ) {
            //TODO:: Handle
        }
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

    private void showExistingBreedsInLayout( String breed, LinearLayout parent ) {
        TextView newBreed = new TextView( context );
        newBreed.setText( breed );

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins( 5, 5, 5, 5 );
        //TODO: Set padding to the text insdie the textview
        //TODO:: Instead of using a dialog for breed selection, create an entire activity, can offer the most options this way

        newBreed.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        newBreed.setTextColor(getResources().getColor(R.color.colorWhite));
        newBreed.setLayoutParams(layoutParams);

        parent.addView( newBreed, 0 );
    }

    private void processSelectedBreeds() {

    }

    /**
     * Keep an updated selected type int here
     * Whenever animal type is changed, need to clear current ArrayList of selected breeds
     * @param type
     */
    public void updateSelectedType( int type ) {
        selectedType = type;
        this.selectedBreeds.clear();
        shouldShowNoClaws();

        switch ( selectedType ) {
            case 1 :
                breedIcon.setBackground( getResources().getDrawable( R.drawable.ic_dog_100 ) );
                break;
            case 2 :
                breedIcon.setBackground( getResources().getDrawable( R.drawable.ic_cat_large ) );
                break;
        }



    }

    private void shouldShowNoClaws() {
        if ( selectedType == 1 ) {
            getView().findViewById(R.id.clawsParent).setVisibility(View.GONE);
            getView().findViewById(R.id.clawsIndicatorParent ).setVisibility(View.GONE);
        }
        else {
            getView().findViewById( R.id.clawsParent ).setVisibility( View.VISIBLE );
            getView().findViewById( R.id.clawsIndicatorParent ).setVisibility( View.VISIBLE );
        }
    }

    public ArrayList<String> getSelectedBreeds() {
        return selectedBreeds;
    }

}
