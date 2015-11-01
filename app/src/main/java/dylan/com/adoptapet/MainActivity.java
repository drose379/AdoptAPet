package dylan.com.adoptapet;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarText = (TextView) toolbar.findViewById(R.id.toolbarTitle);

        toolbarText.setText( "AdoptAPet" );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.searchFab);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabLayout);

        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorAccent);
            }
        });

        HomePagerAdapter adapter = new HomePagerAdapter( getSupportFragmentManager() );
        pager.setAdapter( adapter  );
        tabs.setViewPager(pager);
    }

}
