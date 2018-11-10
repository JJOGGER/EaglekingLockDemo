package cn.jcyh.doorbelldemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.utils.ScreenUtil;


/**
 * Created by jogger on 2017/6/16.
 * 自定义开关
 */

public class MySwitchView extends RelativeLayout {
    private float startX = -1;
    private int transX = 0;
    private ImageView iv_switch;//滑动按钮
    private static final int OPEN = 1;
    private static final int CLOSE = 0;
    private int mCurrentState = CLOSE;//开关状态，0关1开
    private OnSwitchStateListener mListener;
    private boolean mIsTouch = true;

    public interface OnSwitchStateListener {
        void open();

        void close();
    }


    public boolean getIsSwitch() {
        return mCurrentState == OPEN;
    }

    public void setOnSwitchStateListener(OnSwitchStateListener listener) {
        mListener = listener;
    }

    public MySwitchView(Context context) {
        this(context, null);
    }

    public MySwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

//    @Override
//    public void setOnClickListener(@Nullable OnClickListener l) {
//        super.setOnClickListener(l);
//        L.e("-----------setOnClickListener:"+mCurrentState);
//        if (mCurrentState == OPEN) {
//            close();
//        } else {
//            open();
//        }
//    }
    private void init(Context context, AttributeSet attrs) {
        iv_switch = new ImageView(context);
        setBackgroundResource(R.mipmap.switch_close_b);//关闭状态
        iv_switch.setImageResource(R.mipmap.switch_close_a);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        iv_switch.setLayoutParams(params);
        addView(iv_switch);
    }

    public void setSwitch(int isSwitch) {
        if (isSwitch == OPEN) {
            open();
        } else {
            close();
        }
    }

    private void open() {
        //往右弹回
        setBackgroundResource(R.mipmap.switch_open_b);
        iv_switch.setImageResource(R.mipmap.switch_open_a);
        iv_switch.layout(getWidth() - iv_switch.getMeasuredWidth(), (getHeight() - iv_switch.getMeasuredHeight()) / 2, getWidth() - 1,
                (getHeight() + iv_switch.getMeasuredHeight()) / 2);
        if (mCurrentState != OPEN) {
            //原本就是打开状态
            mCurrentState = OPEN;
            if (mListener != null) {
                mListener.open();
            }
        }
    }

    private void close() {
        //往左弹回
        setBackgroundResource(R.mipmap.switch_close_b);
        iv_switch.setImageResource(R.mipmap.switch_close_a);
        iv_switch.layout(getWidth() - iv_switch.getMeasuredWidth(), (getHeight() - iv_switch.getMeasuredHeight()) / 2, getWidth(),
                (getHeight() + iv_switch.getMeasuredHeight()) / 2);
        iv_switch.layout(0, (getHeight() - iv_switch.getMeasuredHeight()) / 2, iv_switch.getMeasuredWidth(), (getHeight() + iv_switch.getMeasuredHeight()) / 2);
        mCurrentState = CLOSE;
        if (mListener != null) {
            mListener.close();
        }
    }

    public int getSwitchState() {
        return mCurrentState;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int top = (getHeight() - iv_switch.getMeasuredHeight()) / 2;
        int bottom = (getHeight() + iv_switch.getMeasuredHeight()) / 2;
        int left;
        int right;
        if (mCurrentState == OPEN) {
//            iv_switch.layout(getWidth() - iv_switch.getMeasuredWidth() - 1, 2, getWidth() - 1,
//                    iv_switch.getMeasuredHeight() + 2);
            left = getWidth() - iv_switch.getMeasuredWidth() - ScreenUtil.dip2px(getContext(), 2);
            right = getWidth() - ScreenUtil.dip2px(getContext(), 2);
            iv_switch.layout(left, top, right, bottom);
        } else {
            iv_switch.layout(ScreenUtil.dip2px(getContext(), 2), iv_switch.getTop
                    (), iv_switch.getMeasuredWidth(), iv_switch.getBottom());
//            iv_switch.layout(0, (getHeight() - iv_switch.getMeasuredHeight()) / 2, iv_switch.getMeasuredWidth(),
//                    (getHeight() + iv_switch.getMeasuredHeight()) / 2);
            left = ScreenUtil.dip2px(getContext(), 2);
            right = iv_switch.getMeasuredWidth() + left;
        }
        iv_switch.layout(left, top, right, bottom);
    }
}
