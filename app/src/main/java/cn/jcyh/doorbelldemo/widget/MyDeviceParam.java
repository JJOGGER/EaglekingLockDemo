package cn.jcyh.doorbelldemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jcyh.eaglekinglockdemo.R;


/**
 * Created by Jogger on 2018/2/24.
 */

public class MyDeviceParam extends RelativeLayout {

    private CheckBox cb_param;
    private TextView tv_text;

    public MyDeviceParam(Context context) {
        this(context, null);
    }

    public MyDeviceParam(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDeviceParam(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyDeviceParam);
        View view = LayoutInflater.from(context).inflate(R.layout.device_param_layout, this, true);
        tv_text = (TextView) view.findViewById(R.id.tv_text);
        cb_param = (CheckBox) view.findViewById(R.id.cb_param);
        String text = typedArray.getString(R.styleable.MyDeviceParam_text);
        Boolean isOpen = typedArray.getBoolean(R.styleable.MyDeviceParam_is_selected, false);
        if (!TextUtils.isEmpty(text)) {
            tv_text.setText(text);
        }
        float textSize = typedArray.getDimension(R.styleable.MyDeviceParam_text_size, 28);
        tv_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        int color = typedArray.getColor(R.styleable.MyDeviceParam_text_color, Color.BLACK);
        tv_text.setTextColor(color);
        cb_param.setChecked(isOpen);
        typedArray.recycle();
    }

    public void setCheck(boolean isCheck) {
        cb_param.setChecked(isCheck);
    }


    public boolean isChecked() {
        return cb_param.isChecked();
    }

    public void setCheckable(boolean isCheckable) {
        if (isCheckable) {
            tv_text.setTextColor(getResources().getColor(R.color.black_333333));
        } else {
            tv_text.setTextColor(getResources().getColor(R.color.gray_888888));
        }
    }

    public void setCheckBoxVisible(boolean isVisible) {
        cb_param.setVisibility(isVisible ? VISIBLE : GONE);
    }
}