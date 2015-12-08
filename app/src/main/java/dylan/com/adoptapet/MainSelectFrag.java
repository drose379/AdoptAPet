package dylan.com.adoptapet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by dylan on 12/4/15.
 */
public class MainSelectFrag extends Fragment implements View.OnClickListener {

    //TODO:: Stretch the dog and cat backdrop to have same margin (2dp) of all smaller buttons

    private Context context;

    private ImageView dogSelect;
    private ImageView catSelect;
    private LinearLayout pigSelectParent;
    private LinearLayout rabbitSelect;
    private LinearLayout birdSelect;
    private LinearLayout horseSelect;
    private LinearLayout sheepSelect;
    private LinearLayout reptileSelect;
    private LinearLayout mouseSelect;

    private ArrayList<LinearLayout> selectables;

    private int selectedType = -1;

    private SelectedTypeCallback callback;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        this.context = context;
        this.callback = (MainActivity) context;
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate( savedInstance );

        /**
         * Move all logic and methods from activity to here
         */

    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        View v = inflater.inflate( R.layout.main_layout_1, parent, false );


        dogSelect = (ImageView) v.findViewById( R.id.dogSelect );
        catSelect = (ImageView) v.findViewById(R.id.catSelect);
        pigSelectParent = (LinearLayout) v.findViewById(R.id.pigSelectParent);
        rabbitSelect = (LinearLayout) v.findViewById(R.id.rabbitSelectParent);
        birdSelect = (LinearLayout) v.findViewById(R.id.birdSelectParent);
        horseSelect = (LinearLayout) v.findViewById(R.id.horseSelectParent);
        sheepSelect = (LinearLayout) v.findViewById(R.id.sheepSelectParent);
        reptileSelect = (LinearLayout) v.findViewById(R.id.alligatorSelectParent);
        mouseSelect = (LinearLayout) v.findViewById(R.id.mouseSelectParent);

        selectables = new ArrayList<LinearLayout>();

        selectables.add( pigSelectParent );
        selectables.add(rabbitSelect);
        selectables.add(birdSelect);
        selectables.add(horseSelect);
        selectables.add(sheepSelect);
        selectables.add(reptileSelect);
        selectables.add(mouseSelect);


        for ( LinearLayout item : selectables ) {
            item.setOnClickListener( this );
        }

        dogSelect.setOnClickListener( this );
        catSelect.setOnClickListener( this );

        /**
         * Grab views
         */

        return v;
    }

    private void clearSelectedItems() {

        dogSelect.setBackgroundResource(R.drawable.dog_background);
        catSelect.setBackgroundResource(R.drawable.dog_background);

        for( LinearLayout item : selectables ) {
            item.setBackgroundResource( R.drawable.other_background );
        }
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {

            case R.id.dogSelect :
                clearSelectedItems();
                dogSelect.setBackgroundResource(R.drawable.dog_background_selected);

                selectedType = 1;

                break;

            case R.id.catSelect :
                clearSelectedItems();
                catSelect.setBackgroundResource(R.drawable.dog_background_selected);

                selectedType = 2;
                break;

            case R.id.pigSelectParent :
                clearSelectedItems();
                pigSelectParent.setBackgroundResource( R.drawable.other_background_selected );

                selectedType = 3;
                break;
            case R.id.rabbitSelectParent :
                clearSelectedItems();
                rabbitSelect.setBackgroundResource(R.drawable.other_background_selected);

                selectedType = 4;
                break;
            case R.id.birdSelectParent :
                clearSelectedItems();
                birdSelect.setBackgroundResource(R.drawable.other_background_selected);

                selectedType = 5;
                break;
            case R.id.horseSelectParent :
                clearSelectedItems();
                horseSelect.setBackgroundResource(R.drawable.other_background_selected);

                selectedType = 6;
                break;
            case R.id.sheepSelectParent :
                clearSelectedItems();
                sheepSelect.setBackgroundResource(R.drawable.other_background_selected);

                selectedType = 7;
                break;
            case R.id.alligatorSelectParent :
                clearSelectedItems();
                reptileSelect.setBackgroundResource(R.drawable.other_background_selected);

                selectedType = 8;
                break;
            case R.id.mouseSelectParent :
                clearSelectedItems();
                mouseSelect.setBackgroundResource(R.drawable.other_background_selected);

                selectedType = 9;
                break;
        }

        callback.updateSelectedType( selectedType );

    }

}
