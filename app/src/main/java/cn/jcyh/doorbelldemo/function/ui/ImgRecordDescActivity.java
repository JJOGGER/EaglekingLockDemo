package cn.jcyh.doorbelldemo.function.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.List;

import butterknife.BindView;
import cn.jcyh.doorbelldemo.adapter.ImgRecordDescPageAdapter;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.bean.DoorbellImgRecord;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.eaglekinglockdemo.R;

public class ImgRecordDescActivity extends BaseDoorbellActivity {

    @BindView(R.id.vp_content)
    ViewPager vpContent;

    @Override
    public int getLayoutId() {
        return R.layout.activity_img_record_desc;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void init() {
        Bundle extras = getIntent().getExtras();
        if (extras == null)
            return;
        List<DoorbellImgRecord> records = extras.getParcelableArrayList(Constant.DOORBELL_IMG_RECORDS);
        int index = extras.getInt(Constant.INDEX);
        assert records != null;
        vpContent.setAdapter(new ImgRecordDescPageAdapter(this, records));
        vpContent.setOffscreenPageLimit(3);
        vpContent.setCurrentItem(index);
    }
}
