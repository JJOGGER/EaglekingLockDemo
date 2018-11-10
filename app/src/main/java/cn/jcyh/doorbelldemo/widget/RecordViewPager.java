package cn.jcyh.doorbelldemo.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.jcyh.utils.L;


/**
 * Created by jogger on 2018/8/14.
 */
public class RecordViewPager extends ViewPager {
    public RecordViewPager(@NonNull Context context) {
        super(context);
    }

    public RecordViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            L.e("------------触摸异常抛出");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException e) {
            L.e("------------触摸异常抛出");
            e.printStackTrace();
        }
        return false;
    }

}
