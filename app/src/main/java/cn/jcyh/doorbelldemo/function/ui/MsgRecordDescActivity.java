package cn.jcyh.doorbelldemo.function.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.widget.RecordViewPager;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.DoorbellMsgRecord;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MsgRecordDescActivity extends BaseDoorbellActivity {
    @BindView(R.id.vp_content)
    RecordViewPager vpContent;
    private List<DoorbellMsgRecord> mDoorbellMsgRecords;

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
        mDoorbellMsgRecords = extras.getParcelableArrayList(Constant.DOORBELL_MSG_RECORDS);
        int index = extras.getInt(Constant.INDEX);
        vpContent.setAdapter(new MyPagerAdapter());
        vpContent.setOffscreenPageLimit(3);
        vpContent.setCurrentItem(index);
    }

    private class MyPagerAdapter extends PagerAdapter {
        private List<FrameLayout> mImageViews;

        MyPagerAdapter() {
            mImageViews = new ArrayList<>();
            for (int i = 0; i < mDoorbellMsgRecords.size(); i++) {
                mImageViews.add(new FrameLayout(getApplicationContext()));
            }
        }

        @Override
        public int getCount() {
            return mDoorbellMsgRecords.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            FrameLayout frameLayout = mImageViews.get(position);
            frameLayout.setBackgroundColor(Color.BLACK);
            final DoorbellMsgRecord record = mDoorbellMsgRecords.get(position);
            PhotoView photoView = new PhotoView(container.getContext());
            PhotoViewAttacher viewAttacher = new PhotoViewAttacher(photoView);
            Glide.with(getApplicationContext())
                    .load(record.getImageUrl())
                    .error(R.mipmap.peephole_push_photo_ph)
                    .into(photoView);
            viewAttacher.update();
            frameLayout.addView(photoView);
            container.addView(frameLayout);
            mImageViews.set(position, frameLayout);
            return frameLayout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mImageViews.get(position));
        }
    }


}
