package cn.jcyh.doorbelldemo.function.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.base.BaseDoorbellActivity;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.control.ui.DialogHelper;
import cn.jcyh.doorbelldemo.function.contract.DoorbellParamsContract;
import cn.jcyh.doorbelldemo.function.presenter.DoorbellParamsPresenter;
import cn.jcyh.doorbelldemo.function.ui.dialogs.AppointAdminDialog;
import cn.jcyh.doorbelldemo.function.ui.dialogs.DoorBellParamDialog;
import cn.jcyh.doorbelldemo.function.ui.dialogs.DoorbellSensorParamDialog;
import cn.jcyh.doorbelldemo.function.ui.dialogs.UnbindAdminDialog;
import cn.jcyh.doorbelldemo.function.ui.dialogs.UnbindHintDialog;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellModelParam;
import cn.jcyh.nimlib.entity.DoorbellSensorParam;
import cn.jcyh.utils.L;

public class DoorbellParamsActivity extends BaseDoorbellActivity<DoorbellParamsPresenter> implements DoorbellParamsContract.View {
    @BindView(R.id.fl_mode_net_push)
    FrameLayout flModeNetPush;
    @BindView(R.id.fl_mode_record)
    FrameLayout flModeRecord;
    @BindView(R.id.fl_mode_video_call)
    FrameLayout flModeVideoCall;
    @BindView(R.id.fl_mode_leave_message)
    FrameLayout flModeLeaveMessage;

    @BindView(R.id.fl_sensor_net_push)
    FrameLayout flSensorNetPush;
    @BindView(R.id.fl_sensor_ring_alarm)
    FrameLayout flSensorRingAlarm;
    @BindView(R.id.fl_sensor_video_call)
    FrameLayout flSensorVideoCall;
    @BindView(R.id.rl_monitor)
    RelativeLayout rl_monitor;
    @BindView(R.id.fl_sensor_record)
    FrameLayout flSensorRecord;
    @BindView(R.id.tv_monitor_state)
    TextView tvMonitorState;
    private Doorbell mDoorbell;
    private DialogHelper mSensorSetDialog;
    private DialogHelper mDoorbellSetDialog;
    private DialogHelper mUnbindUserDialog;
    private DialogHelper mUnbindAdminDialog;
    private DialogHelper mAppointAdminDialog;

    @Override
    public int getLayoutId() {
        return R.layout.activity_doorbell_params;
    }

    @Override
    protected DoorbellParamsPresenter createPresenter() {
        mDoorbell = (Doorbell) getIntent().getSerializableExtra(Constant.DOORBELL);
        return new DoorbellParamsPresenter();
    }

    @Override
    protected void init() {
    }

    @Override
    protected void loadData() {
        mPresenter.getDoorbellParams();
    }

    //初始化门铃参数
    private void initModeParams(DoorbellModelParam doorbellModelParam) {
        flModeNetPush.setVisibility(doorbellModelParam.getNetPush() == 1 ?
                View.VISIBLE : View.GONE);
        if (doorbellModelParam.getVideotap() == 1) {
            flModeRecord.setVisibility(View.VISIBLE);
        } else {
            flModeRecord.setVisibility(View.GONE);
        }
        if (doorbellModelParam.getLeaveMessage() == 1) {
            flModeLeaveMessage.setVisibility(View.VISIBLE);
            flModeRecord.setVisibility(View.GONE);
        } else {
            flModeLeaveMessage.setVisibility(View.GONE);
        }
        flModeVideoCall.setVisibility(doorbellModelParam.getVideoCall() == 1 ?
                View.VISIBLE : View.GONE);
//        flModeSendMsg.setVisibility(doorbellModelParam.getSendMsg() == 1 ?
//                View.VISIBLE : View.GONE);
//        flModeDial.setVisibility(doorbellModelParam.getDial() == 1 ?
//                View.VISIBLE : View.GONE);
    }

    //初始化感应参数
    private void initSensorParams(DoorbellSensorParam doorbellSensorParam) {
        tvMonitorState.setText(doorbellSensorParam.getMonitor() == 1 ? R.string.open : R.string.close);
        tvMonitorState.setSelected(doorbellSensorParam.getMonitor() == 1);
        if (doorbellSensorParam.getMonitor() == 1) {
            flSensorNetPush.setVisibility(doorbellSensorParam.getNetPush() == 1 ?
                    View.VISIBLE : View.GONE);
            flSensorRecord.setVisibility(doorbellSensorParam.getVideotap() == 1 ?
                    View.VISIBLE : View.GONE);
            flSensorVideoCall.setVisibility(doorbellSensorParam.getVideoCall() == 1 ?
                    View.VISIBLE : View.GONE);
//            flSensorSendMsg.setVisibility(doorbellModelParam.getSendMsg() == 1 ?
//                    View.VISIBLE : View.GONE);
            flSensorRingAlarm.setVisibility(doorbellSensorParam.getRingAlarm() == 1 ?
                    View.VISIBLE : View.GONE);
//            flSensorDial.setVisibility(doorbellModelParam.getDial() == 1 ?
//                    View.VISIBLE : View.GONE);
        } else {
            flSensorNetPush.setVisibility(View.GONE);
            flSensorRecord.setVisibility(View.GONE);
            flSensorVideoCall.setVisibility(View.GONE);
//            flSensorSendMsg.setVisibility(View.GONE);
            flSensorRingAlarm.setVisibility(View.GONE);
//            flSensorDial.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.tv_title, R.id.rl_doorbell_set, R.id.rl_monitor, R.id.tv_unbind})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title:
                finish();
                break;
            case R.id.rl_doorbell_set:
                if (mDoorbellSetDialog == null) return;
                mDoorbellSetDialog.commit();
                break;
            case R.id.rl_monitor:
                if (mSensorSetDialog == null) return;
                mSensorSetDialog.commit();
                break;
            case R.id.tv_unbind:
                mPresenter.getBindUsers();
                break;
        }
    }

    /**
     * 解绑设备
     */
    private void unbind(boolean isAdmin, final List<BindDoorbellUser> bindDoorbellUsers) {
        L.e("--------unbind>" + (isAdmin && bindDoorbellUsers != null && bindDoorbellUsers.size() != 0));
        if (isAdmin && bindDoorbellUsers != null && bindDoorbellUsers.size() != 0) {
            //管理员解绑
            if (mUnbindAdminDialog == null) {
                final UnbindAdminDialog adminDialog = new UnbindAdminDialog();
                adminDialog.setOnUnbindAdminDialogListener(new UnbindAdminDialog.OnUnbindAdminDialogListener() {
                    @Override
                    public void onUnbind() {
                        mPresenter.adminUnbind();
                        mUnbindAdminDialog.dismiss();
                    }

                    @Override
                    public void onAppointAdmin() {
                        appointAdmin(bindDoorbellUsers);
                        mUnbindAdminDialog.dismiss();
                    }
                });
                mUnbindAdminDialog = new DialogHelper(this, adminDialog);
            }
            mUnbindAdminDialog.commit();
        } else {
            //非管理员或只有一个用户解绑
            if (mUnbindUserDialog == null) {
                final UnbindHintDialog unbindUserDialog = new UnbindHintDialog();
                unbindUserDialog.setHintContent(getString(R.string.delete_device_msg));
                unbindUserDialog.setOnHintDialogListener(new UnbindHintDialog.OnHintDialogListener() {
                    @Override
                    public void confirm(boolean isConfirm) {
                        if (isConfirm) {
                            mPresenter.userUnbind();
                        }
                        mUnbindUserDialog.dismiss();
                    }
                });
                mUnbindUserDialog = new DialogHelper(this, unbindUserDialog);
            }
            mUnbindUserDialog.commit();
        }

    }

    /**
     * 指定新的管理员
     */
    private void appointAdmin(List<BindDoorbellUser> bindDoorbellUsers) {
        if (mAppointAdminDialog == null) {
            AppointAdminDialog appointAdminDialog = new AppointAdminDialog();
            appointAdminDialog.setDatas(bindDoorbellUsers);
            appointAdminDialog.seOnAppointAdminDialogListener(new AppointAdminDialog.OnAppointAdminDialogListener() {
                @Override
                public void onSelected(BindDoorbellUser bindDoorbellUser) {
                    mPresenter.appointAdmin(bindDoorbellUser);
                }
            });
            mAppointAdminDialog = new DialogHelper(this, appointAdminDialog);
        }
        mAppointAdminDialog.commit();
    }

    @Override
    protected void onDestroy() {
        if (mDoorbellSetDialog != null && mDoorbellSetDialog.isShowing())
            mDoorbellSetDialog.dismiss();
        if (mSensorSetDialog != null && mSensorSetDialog.isShowing())
            mSensorSetDialog.dismiss();
        if (mUnbindUserDialog != null && mUnbindUserDialog.isShowing())
            mUnbindUserDialog.dismiss();
        if (mUnbindAdminDialog != null && mUnbindAdminDialog.isShowing())
            mUnbindAdminDialog.dismiss();
        if (mAppointAdminDialog != null && mAppointAdminDialog.isShowing())
            mAppointAdminDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public Doorbell getDoorbell() {
        return mDoorbell;
    }

    @Override
    public void getBindUsersSuccess(List<BindDoorbellUser> bindDoorbellUsers) {
        boolean isAdmin = false;
        List<BindDoorbellUser> otherUsers = new ArrayList<>(bindDoorbellUsers);
        for (int i = 0; i < otherUsers.size(); i++) {
            BindDoorbellUser bindDoorbellUser = otherUsers.get(i);
            if (bindDoorbellUser.getUserId().equals(ControlCenter.getUserManager().getUser().getUserId())) {
                isAdmin = bindDoorbellUser.isAdmin();
                otherUsers.remove(i);
                break;
            }
        }
        unbind(isAdmin, otherUsers);
    }

    @Override
    public void getBindUsersFail() {

    }

    @Override
    public void userUnbindSuccess() {
        finish();
    }

    @Override
    public void appointAdminAndUnbindSuccess() {
        finish();
    }

    @Override
    public void adminUnbindSuccess() {
        finish();
    }

    @Override
    public void getModeParamsSuccess(DoorbellModelParam doorbellModelParam) {
        DoorBellParamDialog doorBellParamDialog = new DoorBellParamDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.DOORBELL, mDoorbell);
        doorBellParamDialog.setArguments(bundle);
        doorBellParamDialog.setParam(doorbellModelParam);
        doorBellParamDialog.setOnDoorBellParamListener(new DoorBellParamDialog
                .OnDoorBellParamListener() {
            @Override
            public void confirm(DoorbellModelParam param) {
                initModeParams(param);
            }
        });
        mDoorbellSetDialog = new DialogHelper(this, doorBellParamDialog);
        initModeParams(doorbellModelParam);
    }


    @Override
    public void getSensorParamsSuccess(DoorbellSensorParam doorbellSensorParam) {
        DoorbellSensorParamDialog sensorParamDialog = new DoorbellSensorParamDialog();
        sensorParamDialog.setParam(doorbellSensorParam);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.DOORBELL, mDoorbell);
        sensorParamDialog.setArguments(bundle);
        sensorParamDialog.setOnMonitorParamListener(new DoorbellSensorParamDialog
                .OnMonitorParamListener() {

            @Override
            public void confirm(DoorbellSensorParam param) {
                initSensorParams(param);
            }
        });
        mSensorSetDialog = new DialogHelper(this, sensorParamDialog);
        initSensorParams(doorbellSensorParam);
    }

}
