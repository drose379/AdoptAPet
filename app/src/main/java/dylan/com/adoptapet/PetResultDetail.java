package dylan.com.adoptapet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * Created by dylan on 11/8/15.
 */
public class PetResultDetail extends AppCompatActivity {

    private PetResult currentPet;

    @Override
    public void onCreate( Bundle savedInstance ) {
        super.onCreate(savedInstance);
        setContentView(R.layout.pet_detail);

        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView toolbarTitle = (TextView) findViewById( R.id.toolbarTitle );

        currentPet = (PetResult) getIntent().getSerializableExtra( "pet" );
        toolbarTitle.setText( currentPet.getName() + "\'s Details" );
    }

}