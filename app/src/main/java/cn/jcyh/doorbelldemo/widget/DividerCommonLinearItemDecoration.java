package cn.jcyh.doorbelldemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Jogger on 2017/5/2.
 * linear布局分割线
 */

public class DividerCommonLinearItemDecoration extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;

    public DividerCommonLinearItemDecoration(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontal(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            int left, right, top, bottom;
            if (i == 0) {
                left = child.getLeft() - params.leftMargin;
                right = child.getRight() + params.rightMargin
                        + mDivider.getIntrinsicWidth();
                top = child.getTop() + params.bottomMargin;
                bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
            left = child.getLeft() - params.leftMargin;
            right = child.getRight() + params.rightMargin
                    + mDivider.getIntrinsicWidth();
            top = child.getBottom() + params.bottomMargin;
            bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isfirstRow(RecyclerView parent, int pos, int spanCount,
                               int childCount) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            //如是第一行则返回true
            return (pos / spanCount + 1) == 1;
        }
        return false;
    }

    private int getSpanCount(RecyclerView parent) {
        // 列数
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            spanCount = layoutManager.getItemCount();
        }
        return spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition,
                               RecyclerView parent) {
        int childCount = parent.getAdapter().getItemCount();
        int spanCount = getSpanCount(parent);
//        if (isfirstRow(parent, itemPosition, getSpanCount(parent), childCount)) {
//            outRect.set(mDivider.getIntrinsicHeight(), 0, mDivider.getIntrinsicWidth(), 0);
//        } else {
        outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
//        }
//        if (isFirstBottom(parent, itemPosition, childCount))// 如果是最后一行，则不需要绘制底部
//        {
//            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
//        } else {
//            outRect.set(PADDING, 0, mDivider.getIntrinsicWidth() - PADDING, 0);
//        }
    }
}
