package cn.jcyh.doorbelldemo.function.ui.dialogs;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.widget.DividerCommonLinearItemDecoration;
import cn.jcyh.doorbelldemo.widget.MyLinearLayoutManager;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.utils.ScreenUtil;

/**
 * Created by jogger on 2018/8/15.
 */
public class AppointAdminDialog extends BaseDialogFragment {
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BaseQuickAdapter<BindDoorbellUser, BaseViewHolder> mAdapter;
    private List<BindDoorbellUser> mBindDoorbellUsers;
    private BindDoorbellUser mCurrentUser;

    public void setDatas(List<BindDoorbellUser> bindDoorbellUsers) {
        mBindDoorbellUsers = bindDoorbellUsers;
        if (mBindDoorbellUsers == null || mBindDoorbellUsers.size() == 0) return;
        mCurrentUser = mBindDoorbellUsers.get(0);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    private OnAppointAdminDialogListener mListener;

    public interface OnAppointAdminDialogListener {
        void onSelected(BindDoorbellUser bindDoorbellUser);
    }

    public void seOnAppointAdminDialogListener(OnAppointAdminDialogListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_appoint_admin;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
//            params.height = (int) (0.55f * ScreenUtil.getSrceenHeight(mActivity));
            params.height = ScreenUtil.dip2px(mActivity, 410);
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
        super.onResume();
    }

    @Override
    protected void init(View view) {
        rvContent.setLayoutManager(new MyLinearLayoutManager(mActivity));
        rvContent.addItemDecoration(new DividerCommonLinearItemDecoration(mActivity));
        mAdapter = new BaseQuickAdapter<BindDoorbellUser, BaseViewHolder>(R.layout.rv_common_text_item2, mBindDoorbellUsers) {
            @Override
            protected void convert(BaseViewHolder helper, BindDoorbellUser item) {
                helper.setText(R.id.tv_text, item.getNickName() + " " + item.getUserName());
                if (mCurrentUser.getUserId().equals(item.getUserId())) {
                    helper.setTextColor(R.id.tv_text, getResources().getColor(R.color.pink_eb4e00));
                    ((TextView) helper.getView(R.id.tv_text)).setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else {
                    helper.setTextColor(R.id.tv_text, getResources().getColor(R.color.gray_888888));
                    ((TextView) helper.getView(R.id.tv_text)).setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                }
            }
        };
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mCurrentUser = mAdapter.getItem(position);
                mAdapter.notifyDataSetChanged();
            }
        });
        rvContent.setAdapter(mAdapter);
    }

    @OnClick({R.id.tv_confirm, R.id.iv_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                if (mListener != null)
                    mListener.onSelected(mCurrentUser);
                break;
            case R.id.iv_cancel:
                break;
        }
        dismiss();
    }
}
