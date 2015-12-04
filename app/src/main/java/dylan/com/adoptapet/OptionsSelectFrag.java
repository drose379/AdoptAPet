package dylan.com.adoptapet;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dylan on 12/4/15.
 */
public class OptionsSelectFrag extends Fragment {

    private Context context;

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        this.context = context;
    }

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate( savedInstance );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup parent, Bundle savedInstance ) {

        View v = inflater.inflate( R.layout.main_layout_2, parent, false );


        return v;
    }

}
