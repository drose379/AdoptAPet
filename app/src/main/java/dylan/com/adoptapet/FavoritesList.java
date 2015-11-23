package dylan.com.adoptapet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dylan.com.adoptapet.R;

/**
 * Created by Dylan Rose on 11/22/15.
 */
public class FavoritesList extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawer;

    private NavMenuAdapter navAdapter;
    private ListView navItemsList;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.favorites_list_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        ImageView menuButton = (ImageView) findViewById(R.id.toolbarMenuButton);

        drawer = (DrawerLayout) findViewById( R.id.drawer );
        navItemsList = (ListView) findViewById( R.id.navItemsList );

        toolbarTitle.setText("My Favorites");
        menuButton.setOnClickListener( this );

        initNavDrawer();

    }

    public void initNavDrawer() {

        ArrayList<MenuItem> items = new ArrayList<MenuItem>();


        FeaturedPetController fc = FeaturedPetController.getInstance( this );

        if ( fc.hasNext() ) {

            PetResult next = fc.next();

            items.add( new MenuItem()
                    .setType( 2 )
                    .setName( next.getName() )
                    .setSex( next.getSex() )
                    .setPhoto( next.getBestPhoto( 1 ) == null ? next.getBestPhoto( 2 ) : next.getBestPhoto( 1 ) )
            );
        } else {

            SQLiteDatabase readable = new ZipDBHelper( this ).getReadableDatabase();
            Cursor result = readable.rawQuery( "SELECT * FROM " + ZipDBHelper.table_name, null, null );

            if ( result.getCount() == 0 ) {

                items.add( new MenuItem()
                        .setType( 2 )
                        .setName( "Please Provide Location!" )
                        .setSex( "Male" )
                        .setPhoto( "https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png" )
                );

            } else {

                items.add( new MenuItem()
                        .setType( 2 )
                        .setName( "Grabbing Featured" )
                        .setSex( "Male" )
                        .setPhoto( "https://pixabay.com/static/uploads/photo/2012/04/10/23/44/question-27106_640.png" )
                );

            }

        }

        /** Add rest of items, make sure Favorites isCurrent( true ) */

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed( View drawer ) {
                updateFeatured();
            }
        });

        items.add(new MenuItem()
                        .setType(1)
                        .setIcon(getResources().getDrawable(R.drawable.ic_home_black_24dp))
                        .setLabel("Home")
        );

        items.add(new MenuItem()
                        .setType(1)
                        .setIcon(getResources().getDrawable(R.drawable.ic_favorite_black_24dp))
                        .setLabel("Favorites")
                        .setIsCurrent(true)
        );

        navItemsList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick( AdapterView parent, View view, int item, long id ) {
                switch( item ) {

                    case 0 :

                        break;

                    case 1 :

                        finish();

                        break;

                    case 2:

                        drawer.closeDrawer(Gravity.LEFT);

                        break;

                }
            }
        });


        navAdapter = new NavMenuAdapter( this, items );
        navItemsList.setAdapter(navAdapter);

    }


    public void updateFeatured() {
        FeaturedPetController fc = FeaturedPetController.getInstance( this );
        if ( fc.hasNext() ) {

            PetResult next = fc.next();

            MenuItem nextFeat = new MenuItem()
                    .setType( 2 )
                    .setName( next.getName() )
                    .setSex( next.getSex() )
                    .setPhoto( next.getBestPhoto( 1 )  == null ? next.getBestPhoto( 2 ) : next.getBestPhoto( 1 ) );


            if ( navAdapter != null )
                navAdapter.updateFeatured(nextFeat);
        }
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {

            case R.id.toolbarMenuButton :

                drawer.openDrawer( Gravity.LEFT );

                break;

        }
    }


}
