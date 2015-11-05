package dylan.com.adoptapet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dylan on 11/4/15.
 */
public class PetResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PetResult> pets;

    public PetResultAdapter( Context context, ArrayList<PetResult> pets ) {
        this.context = context;
        this.pets = pets;
    }

    @Override
    public int getCount() {
        return pets.size();
    }

    @Override
    public long getItemId( int item ) {
        return 0;
    }

    @Override
    public PetResult getItem( int item ) {
        return pets.get( item );
    }

    @Override
    public View getView( int item, View recycledView, ViewGroup parent ) {

        recycledView = recycledView == null ? LayoutInflater.from( context ).inflate( R.layout.pet_card, parent, false ) : recycledView;

        TextView nameTest = (TextView) recycledView.findViewById( R.id.nameTest );

        PetResult result = pets.get( item );

        nameTest.setText( result.getName() );

        return recycledView;
    }

}
