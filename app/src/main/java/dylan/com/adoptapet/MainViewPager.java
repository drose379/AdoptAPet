package dylan.com.adoptapet;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by dylan on 12/4/15.
 */
public class MainViewPager extends ViewPager {

    private boolean shouldSwipe = false;

    public MainViewPager( Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    public void setShouldSwipe( boolean shouldSwipe ) {
        this.shouldSwipe = shouldSwipe;
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev ) {
        if ( shouldSwipe )
            return super.onInterceptTouchEvent( ev );
        else
            return false;
    }

    @Override
    public boolean onTouchEvent( MotionEvent ev ) {
        if ( shouldSwipe )
            return super.onTouchEvent( ev );
        else
            return false;
    }

}
