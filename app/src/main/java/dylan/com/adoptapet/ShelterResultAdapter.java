package dylan.com.adoptapet;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by dylan on 11/24/15.
 */
public class ShelterResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ShelterResult> results;

    public ShelterResultAdapter( Context context, ArrayList<ShelterResult> results ) {
        this.context = context;
        this.results = results;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public ShelterResult getItem( int item ) {
        return results.get(item);
    }

    @Override
    public long getItemId( int item ) {
        return 0;
    }

    public int getItemIndex( String name ) {
        int index = 0;

        for ( int i = 0; i < results.size(); i++ ) {
            if ( results.get( i ).getName().equals( name ) ) {
                index = i;
                break;
            }
        }

        return index;
    }

    private String generateLocationText( ShelterResult shelter ) {
        String location = "";

        String address = shelter.getAddress();
        String city = shelter.getCity();
        String state = shelter.getState();
        String country = shelter.getCountry();

        if ( !address.isEmpty() ) {

            if ( !city.isEmpty() || !state.isEmpty() || !country.isEmpty() ) {
                location += address + ", ";
            } else {
                location += address;
            }

        }

        if ( !city.isEmpty() ) {

            if ( !state.isEmpty() || !country.isEmpty() ) {
                location += city + ", ";
            } else {
                location += city;
            }

        }

        if ( !state.isEmpty() ) {

            if ( !country.isEmpty() ) {
                location += state + ", ";
            } else {
                location += state;
            }

        }

        location += !country.isEmpty() ? country : "";

        return location;
    }

    @Override
    public View getView( int which, View recycledView, ViewGroup parent ) {
        ShelterResult shelter = results.get( which );

        ShelterResultViewHolder viewHolder;

        if ( recycledView == null ) {
            recycledView = LayoutInflater.from( context ).inflate( R.layout.shelter_card, parent, false );

            viewHolder = new ShelterResultViewHolder();

            viewHolder.shelterName = (TextView) recycledView.findViewById( R.id.shelterName );
            viewHolder.location = (TextView) recycledView.findViewById( R.id.shelterLocation );
            viewHolder.phone = (TextView) recycledView.findViewById( R.id.shelterPhone );
            viewHolder.email = (TextView) recycledView.findViewById( R.id.shelterEmail );
            viewHolder.phoneLayout = (LinearLayout) recycledView.findViewById( R.id.phoneContainer );
            viewHolder.emailLayout = (LinearLayout) recycledView.findViewById( R.id.emailContainer );

            recycledView.setTag( viewHolder );

        }

        viewHolder = (ShelterResultViewHolder) recycledView.getTag();


        /**
         * TODO:: GET RID OF SHELTER NAME ICON, MOVE SHELTER NAME TO MIDDLE, GIVE IT A BACKGROUND OF DARKER THEN CARD REST
         */

        viewHolder.shelterName.setText( shelter.getName() );
        viewHolder.location.setText( generateLocationText(shelter) );

        if ( !shelter.getPhone().trim().isEmpty() ) {
            viewHolder.phone.setText(shelter.getPhone());
            viewHolder.phoneLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.phoneLayout.setVisibility( View.GONE );
        }

        if ( !shelter.getEmail().isEmpty() ) {
            viewHolder.email.setText( shelter.getEmail() );
            viewHolder.emailLayout.setVisibility( View.VISIBLE );
        } else {
            viewHolder.emailLayout.setVisibility( View.GONE );
        }


        return recycledView;
    }

    public class ShelterResultViewHolder {
        public TextView shelterName;
        public TextView location;
        public TextView phone;
        public TextView email;

        public LinearLayout phoneLayout;
        public LinearLayout emailLayout;
    }

}
