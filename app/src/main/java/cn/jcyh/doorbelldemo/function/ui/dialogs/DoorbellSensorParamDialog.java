package cn.jcyh.doorbelldemo.function.ui.dialogs;

import android.app.ProgressDialog;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jcyh.doorbelldemo.bus.nim.NIMMessageTextAction;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.widget.MyDeviceParam;
import cn.jcyh.doorbelldemo.widget.MySwitchView;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.command.ndk.CommandControl;
import cn.jcyh.nimlib.entity.CommandJson;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellSensorParam;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

/**
 * Created by Jogger on 2018/2/24.
 */

public class DoorbellSensorParamDialog extends BaseDialogFragment {

    @BindView(R.id.my_switch)
    MySwitchView mySwitch;
    @BindView(R.id.my_net_push)
    MyDeviceParam myNetPush;
    @BindView(R.id.my_ring_alarm)
    MyDeviceParam myRingAlarm;
    @BindView(R.id.my_video_call)
    MyDeviceParam myVideoCall;
    //    @BindView(R.id.my_dial)
//    MyDeviceParam myDial;
//    @BindView(R.id.my_send_msg)
//    MyDeviceParam mySendMsg;
    @BindView(R.id.my_record)
    MyDeviceParam myRecord;
    private OnMonitorParamListener mListener;
    private DoorbellSensorParam mDoorbellSensorParam;
    private Doorbell mDoorbell;
    private ProgressDialog mProgressDialog;
    private DoorbellSensorParam mParamSet;

    public interface OnMonitorParamListener {
        void confirm(DoorbellSensorParam param);
    }

    public void setOnMonitorParamListener(OnMonitorParamListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_doorbell_sensor_param;
    }

    public void setParam(DoorbellSensorParam doorbellSensorParam) {
        mDoorbellSensorParam = doorbellSensorParam;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    protected void init(View view) {
        assert getArguments() != null;
        mDoorbell = (Doorbell) getArguments().getSerializable(Constant.DOORBELL);
        mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setMessage(getString(R.string.waiting));
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        if (mDoorbellSensorParam != null) {
            mParamSet = new DoorbellSensorParam();
            mySwitch.setSwitch(mDoorbellSensorParam.getMonitor());
            myNetPush.setCheck(mDoorbellSensorParam.getNetPush() == 1);
            myRecord.setCheck(mDoorbellSensorParam.getVideotap() == 1);//录像
            myRingAlarm.setCheck(mDoorbellSensorParam.getRingAlarm() == 1);//铃声报警
//            Boolean modeSendMsg = mDoorbellModelParam.getSendMsg() == 1; //短信
//            if (modeSendMsg) {
//                myDial.setCheck(false);
//            }
//            myDial.setCheckable(!modeSendMsg);
//            mySendMsg.setCheck(modeSendMsg);
//            Boolean modeDial = mDoorbellModelParam.getDial() == 1; //电话
//            if (modeDial) {
//                mySendMsg.setCheck(false);
//                myVideoCall.setCheck(false);
//            }
//            mySendMsg.setCheckable(!modeDial);
//            myVideoCall.setCheckable(!modeDial);
//            myDial.setCheck(modeDial);
            myVideoCall.setCheck(mDoorbellSensorParam.getVideoCall() == 1);//视频呼叫
        }
        initSwitchView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ibtn_cancel, R.id.my_switch, R.id.tv_confirm, R.id.my_net_push,
//            R.id.my_dial,R.id.my_send_msg,
            R.id.my_ring_alarm, R.id.my_video_call, R.id.my_record})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_cancel:
                dismiss();
                break;
            case R.id.my_switch:
                if (mySwitch.getIsSwitch()) {
                    mParamSet.setMonitor(0);
                    mySwitch.setSwitch(0);
                } else {
                    mParamSet.setMonitor(1);
                    mySwitch.setSwitch(1);
                }
                initSwitchView();
                break;
            case R.id.tv_confirm:
                commitUpdate();
                break;
            case R.id.my_net_push:
                myNetPush.setCheck(!myNetPush.isChecked());
                mParamSet.setNetPush(myNetPush.isChecked() ? 1 : 0);
                break;
            case R.id.my_ring_alarm:
                myRingAlarm.setCheck(!myRingAlarm.isChecked());
                mParamSet.setRingAlarm(myRingAlarm.isChecked() ? 1 : 0);
                break;
            case R.id.my_video_call:
                if (myVideoCall.isChecked()) {
                    myVideoCall.setCheck(false);
                    mParamSet.setVideoCall(0);
                } else {
                    myVideoCall.setCheck(true);
                    myVideoCall.setCheckable(true);
//                    myDial.setCheck(false);
//                    mySendMsg.setCheckable(true);
//                    if (!mySendMsg.isChecked())
//                        myDial.setCheckable(true);
                    mParamSet.setVideoCall(1);
//                    mParamSet.setDial(0);
                }
                break;
//            case R.id.my_dial:
//                if (myDial.isChecked()) {
//                    myDial.setCheck(false);
//                    mySendMsg.setCheckable(true);
//                    myVideoCall.setCheckable(true);
//                    mParamSet.setDial(0);
//                } else {
//                    myDial.setCheck(true);
//                    myDial.setCheckable(true);
//                    mySendMsg.setCheck(false);
//                    mySendMsg.setCheckable(false);
//                    myVideoCall.setCheck(false);
//                    myVideoCall.setCheckable(false);
//                    mParamSet.setDial(1);
//                    mParamSet.setSendMsg(0);
//                    mParamSet.setVideoCall(0);
//                }
//                break;
//            case R.id.my_send_msg:
//                if (mySendMsg.isChecked()) {
//                    mySendMsg.setCheck(false);
//                    myDial.setCheckable(true);
//                    mParamSet.setSendMsg(0);
//                } else {
//                    mySendMsg.setCheck(true);
//                    mySendMsg.setCheckable(true);
//                    myDial.setCheck(false);
//                    myDial.setCheckable(false);
//                    myVideoCall.setCheckable(true);
//                    mParamSet.setSendMsg(1);
//                    mParamSet.setDial(0);
//                }
//                break;
            case R.id.my_record:
                myRecord.setCheck(!myRecord.isChecked());
                mParamSet.setVideotap(myRecord.isChecked() ? 1 : 0);
                break;
        }
    }

    private void initSwitchView() {
        if (mySwitch.getIsSwitch()) {
            myNetPush.setCheckBoxVisible(true);
            myRingAlarm.setCheckBoxVisible(true);
//            myDial.setCheckBoxVisible(true);
//            mySendMsg.setCheckBoxVisible(true);
            myVideoCall.setCheckBoxVisible(true);
            myRecord.setCheckBoxVisible(true);

            myNetPush.setClickable(true);
            myRingAlarm.setClickable(true);
//            myDial.setClickable(true);
//            mySendMsg.setClickable(true);
            myVideoCall.setClickable(true);
            myRecord.setClickable(true);

            myNetPush.setCheckable(true);
            myRingAlarm.setCheckable(true);
//            if (!myDial.isChecked() && !mySendMsg.isChecked()) {
//                myDial.setCheckable(true);
//                mySendMsg.setCheckable(true);
//            } else {
//                myDial.setCheckable(myDial.isChecked());
//                mySendMsg.setCheckable(mySendMsg.isChecked());
//            }
            myVideoCall.setCheckable(true);
            myRecord.setCheckable(true);
        } else {
            myNetPush.setCheckBoxVisible(false);
            myRingAlarm.setCheckBoxVisible(false);
//            myDial.setCheckBoxVisible(false);
//            mySendMsg.setCheckBoxVisible(false);
            myVideoCall.setCheckBoxVisible(false);
            myRecord.setCheckBoxVisible(false);

            myNetPush.setCheckable(false);
            myRingAlarm.setCheckable(false);
//            myDial.setCheckable(false);
//            mySendMsg.setCheckable(false);
            myVideoCall.setCheckable(false);
            myRecord.setCheckable(false);

            myNetPush.setClickable(false);
            myRingAlarm.setClickable(false);
//            myDial.setClickable(false);
//            mySendMsg.setClickable(false);
            myVideoCall.setClickable(false);
            myRecord.setClickable(false);
        }
    }

    /**
     * 提交更改
     */
    private void commitUpdate() {
        mProgressDialog.show();
        CommandControl.sendDoorbellSensorParamsCommand(mDoorbell.getDeviceId(), mParamSet);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNIMMessageTextAction(NIMMessageTextAction messageTextAction) {
        CommandJson commandJson = messageTextAction.getParcelableExtra(Constant.COMMAND);
        if (!CommandJson.CommandType.DOORBELL_PARAMS_RESPONSE.equals(commandJson.getCommandType()))
            return;
        L.e("---------->onNIMMessageTextAction");
        if (commandJson.getFlag2() > 0) {
            T.show(R.string.setting_success);
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mDoorbellSensorParam = mParamSet;
            if (mListener != null) {
                mListener.confirm(mDoorbellSensorParam);
            }
            dismiss();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            T.show(R.string.set_error);
        }
    }
}
