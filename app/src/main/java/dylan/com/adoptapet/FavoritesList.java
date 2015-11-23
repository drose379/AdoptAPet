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

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed( View drawer ) {

            }
        });

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
