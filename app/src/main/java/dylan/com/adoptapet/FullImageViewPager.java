package dylan.com.adoptapet;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by dylan on 12/5/15.
 */
public class FullImageViewPager extends ViewPager {

    public interface CurrentItemCallback {
        void getCurrentPosition( int pos );
    }

    private CurrentItemCallback callback;

    public FullImageViewPager( Context context, AttributeSet attrs) {
        super( context, attrs );

        addOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected( int position ) {
                callback.getCurrentPosition( position + 1 );
            }
        });



    }

    public void setCallback( CurrentItemCallback callback ) {
        this.callback = callback;
    }



}
