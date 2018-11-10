package cn.jcyh.doorbelldemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jcyh.doorbelldemo.bean.DoorbellImgRecord;
import cn.jcyh.eaglekinglockdemo.BuildConfig;
import cn.jcyh.eaglekinglockdemo.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jogger on 2018/7/12.
 */
public class ImgRecordDescPageAdapter extends PagerAdapter {
    private List<FrameLayout> mFrameLayouts;
    private List<DoorbellImgRecord> mImgRecords;
    private MediaMetadataRetriever mMetadataRetriever;

    public ImgRecordDescPageAdapter(Context context, List<DoorbellImgRecord> records) {
        mFrameLayouts = new ArrayList<>();
        mMetadataRetriever = new MediaMetadataRetriever();
        mImgRecords = records;
        for (int i = 0; i < records.size(); i++) {
            mFrameLayouts.add(new FrameLayout(context));
        }
    }

    @Override
    public int getCount() {
        return mFrameLayouts.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {
        FrameLayout frameLayout = mFrameLayouts.get(position);
        frameLayout.setBackgroundColor(Color.BLACK);
        final DoorbellImgRecord record = mImgRecords.get(position);
        PhotoView photoView = new PhotoView(container.getContext());
        PhotoViewAttacher viewAttacher = new PhotoViewAttacher(photoView);
        if (record.getFilePath().endsWith(".mp4")) {
            //视频
            mMetadataRetriever.setDataSource(record.getFilePath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = mMetadataRetriever.getFrameAtTime();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            }
            Glide.with(container.getContext())
                    .load(baos.toByteArray())
//                        .error(R.drawable.img_load_failure)
                    .into(photoView);
        } else {
            Glide.with(container.getContext())
                    .load(record.getFilePath())
//                        .error(R.drawable.img_load_failure)
                    .into(photoView);
        }
        viewAttacher.update();
        frameLayout.addView(photoView);
        if (record.getFilePath().endsWith(".mp4")) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setImageResource(R.mipmap.icon_play);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File playFile = new File(record.getFilePath());
                    Uri uri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        uri = FileProvider.getUriForFile(container.getContext(), BuildConfig.APPLICATION_ID + ".fileprovider", playFile);
                    } else {
                        uri = Uri.fromFile(playFile);
                    }
                    intent.setDataAndType(uri, "video/*");
                    container.getContext().startActivity(intent);
                }
            });
            frameLayout.addView(imageView);
        }
        container.addView(frameLayout);
        mFrameLayouts.set(position, frameLayout);
        return frameLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mFrameLayouts.get(position));
    }
}
