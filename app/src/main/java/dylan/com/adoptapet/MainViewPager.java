package dylan.com.adoptapet;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by dylan on 12/4/15.
 */
public class MainViewPager extends ViewPager {

    public MainViewPager( Context context, AttributeSet attrs ) {
        super( context, attrs );
    }

    @Override
    public boolean onInterceptTouchEvent( MotionEvent ev ) {
        return false;
    }

    @Override
    public boolean onTouchEvent( MotionEvent ev ) {
        setCurrentItem(0);
        return false;
    }

}
