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
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.nimlib.command.ndk.CommandControl;
import cn.jcyh.nimlib.entity.CommandJson;
import cn.jcyh.nimlib.entity.Doorbell;
import cn.jcyh.nimlib.entity.DoorbellModelParam;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

/**
 * Created by Jogger on 2018/2/24.
 * 门铃设置
 */

public class DoorBellParamDialog extends BaseDialogFragment {


    @BindView(R.id.my_net_push)
    MyDeviceParam myNetPush;
    @BindView(R.id.my_leave_msg)
    MyDeviceParam myLeaveMsg;
    @BindView(R.id.my_video_call)
    MyDeviceParam myVideoCall;
    //    @BindView(R.id.my_dial)
//    MyDeviceParam myDial;
//    @BindView(R.id.my_send_msg)
//    MyDeviceParam mySendMsg;
    @BindView(R.id.my_record)
    MyDeviceParam myRecord;
    private OnDoorBellParamListener mListener;
    private DoorbellModelParam mDoorbellModelParam;
    private Doorbell mDoorbell;
    private ProgressDialog mProgressDialog;
    private DoorbellModelParam mParamSet;

    public interface OnDoorBellParamListener {
        void confirm(DoorbellModelParam doorbellModelParam);
    }

    public void setOnDoorBellParamListener(OnDoorBellParamListener listener) {
        mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_doorbell_param;
    }

    public void setParam(DoorbellModelParam doorbellModelParam) {
        mDoorbellModelParam = doorbellModelParam;
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

    private void initView() {
        if (mDoorbellModelParam != null) {
            mParamSet = new DoorbellModelParam(mDoorbellModelParam);
            myNetPush.setCheck(mDoorbellModelParam.getNetPush() == 1);
            myRecord.setCheck(mDoorbellModelParam.getVideotap() == 1);
            Boolean modeLeaveMessage = mDoorbellModelParam.getLeaveMessage() == 1;//留言
            if (modeLeaveMessage) {
                myVideoCall.setCheck(false);
                myRecord.setCheck(false);
            }
            myVideoCall.setCheckable(!modeLeaveMessage);
            myRecord.setCheckable(!modeLeaveMessage);
            myLeaveMsg.setCheck(modeLeaveMessage);
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
            Boolean modeVideoCall = mDoorbellModelParam.getVideoCall() == 1;//视频呼叫
            if (modeVideoCall) {
                myLeaveMsg.setCheck(false);
                myRecord.setCheckable(true);
            }
            myLeaveMsg.setCheckable(!modeVideoCall);
            myVideoCall.setCheck(modeVideoCall);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("----mDoorbellModelParam:" + mDoorbellModelParam);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.tv_confirm, R.id.my_leave_msg, R.id.my_video_call,
//            R.id.my_dial, R.id.my_send_msg,
            R.id.my_net_push, R.id.my_record, R.id.ibtn_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm:
                commitUpdate();
                break;
            case R.id.my_leave_msg:
                if (myLeaveMsg.isChecked()) {
                    myLeaveMsg.setCheck(false);
//                    if (!myDial.isChecked())
//                        myVideoCall.setCheckable(true);
                    myRecord.setCheckable(true);
                    mParamSet.setLeaveMessage(0);
                } else {
                    myLeaveMsg.setCheck(true);
                    myLeaveMsg.setCheckable(true);
                    myVideoCall.setCheck(false);
                    if (myRecord.isChecked()) {
                        myRecord.setCheck(false);
                        mParamSet.setVideotap(0);
                    }
                    myRecord.setCheckable(false);
//                    if (!myDial.isChecked())
//                        myVideoCall.setCheckable(false);
                    mParamSet.setLeaveMessage(1);
                    mParamSet.setVideoCall(0);
                }
                break;
            case R.id.my_video_call:
                if (myVideoCall.isChecked()) {
                    myVideoCall.setCheck(false);
                    if (!myRecord.isChecked())
                        myLeaveMsg.setCheckable(true);
                    mParamSet.setVideoCall(0);
                } else {
                    myVideoCall.setCheck(true);
                    myVideoCall.setCheckable(true);
                    if (myLeaveMsg.isChecked()) {
                        myLeaveMsg.setCheck(false);
                    }
                    myLeaveMsg.setCheckable(false);
                    myRecord.setCheckable(true);
//                    myDial.setCheck(false);
//                    if (!mySendMsg.isChecked())
//                        myDial.setCheckable(true);
//                    mySendMsg.setCheckable(true);
                    mParamSet.setVideoCall(1);
                    mParamSet.setLeaveMessage(0);
//                    mParamSet.setDial(0);
                }
                break;
//            case R.id.my_dial:
//                if (myDial.isChecked()) {
//                    myDial.setCheck(false);
//                    mySendMsg.setCheckable(true);
//                    myRecord.setCheckable(true);
//                    if (!myLeaveMsg.isChecked())
//                        myVideoCall.setCheckable(true);
//                    mParamSet.setDial(0);
//                } else {
//                    myDial.setCheck(true);
//                    myDial.setCheckable(true);
//                    mySendMsg.setCheck(false);
//                    mySendMsg.setCheckable(false);
//                    myVideoCall.setCheck(false);
//                    myVideoCall.setCheckable(false);
//                    myLeaveMsg.setCheckable(true);
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
//                    if (!myLeaveMsg.isChecked())
//                        myVideoCall.setCheckable(true);
//                    mParamSet.setSendMsg(1);
//                    mParamSet.setDial(0);
//                }
//                break;
            case R.id.my_net_push:
                myNetPush.setCheck(!myNetPush.isChecked());
                mParamSet.setNetPush(myNetPush.isChecked() ? 1 : 0);
                break;
            case R.id.my_record:
                if (myRecord.isChecked()) {
                    myRecord.setCheck(false);
                    myLeaveMsg.setCheckable(true);
                    mParamSet.setVideotap(0);
                } else {
                    myRecord.setCheck(true);
                    myRecord.setCheckable(true);
                    myLeaveMsg.setCheck(false);
                    myLeaveMsg.setCheckable(false);
                    mParamSet.setVideotap(1);
                    mParamSet.setLeaveMessage(0);
                }
                break;
            case R.id.ibtn_cancel:
                dismiss();
                break;
        }
    }

    /**
     * 提交更改
     */
    private void commitUpdate() {
        mProgressDialog.show();
        CommandControl.sendDoorbellModeParamsCommand(mDoorbell.getDeviceId(), mParamSet);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNIMMessageTextAction(NIMMessageTextAction messageTextAction) {
        CommandJson commandJson = messageTextAction.getParcelableExtra(Constant.COMMAND);
        if (!CommandJson.CommandType.DOORBELL_PARAMS_RESPONSE.equals(commandJson.getCommandType()))
            return;
        if (commandJson.getFlag2() > 0) {
            T.show(R.string.setting_success);
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            mDoorbellModelParam = mParamSet;
            if (mListener != null) {
                mListener.confirm(mDoorbellModelParam);
            }
            dismiss();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            T.show(R.string.set_error);
        }
    }

}
