package cn.jcyh.doorbelldemo.function.presenter;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.bean.DoorbellAllParam;
import cn.jcyh.doorbelldemo.bus.nim.NIMMessageTextAction;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.DoorbellParamsContract;
import cn.jcyh.doorbelldemo.function.model.DoorbellParamsModel;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.command.ndk.CommandControl;
import cn.jcyh.nimlib.constant.Constant;
import cn.jcyh.nimlib.entity.BindDoorbellUser;
import cn.jcyh.nimlib.entity.CommandJson;
import cn.jcyh.nimlib.entity.DoorbellRequest;
import cn.jcyh.nimlib.entity.SetAdminRequest;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;
import cn.jcyh.nimlib.utils.GsonUtil;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/7/6.
 */
public class DoorbellParamsPresenter extends BasePresenter<DoorbellParamsContract.View, DoorbellParamsContract.Model> implements DoorbellParamsContract.Presenter {


    private final String mUserId;

    public DoorbellParamsPresenter() {
        mUserId = ControlCenter.getUserManager().getUser().getUserId();
    }

    @Override
    public void getBindUsers() {
        DoorbellRequest doorbellRequest=new DoorbellRequest(mView.getDoorbell().getDeviceId());
        mModel.getBindUsers(doorbellRequest, new OnHttpRequestListener<List<BindDoorbellUser>>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                T.show(desc);
                mView.getBindUsersFail();
            }

            @Override
            public void onSuccess(List<BindDoorbellUser> bindDoorbellUser) {
                if (mView == null) return;
                if (bindDoorbellUser == null || bindDoorbellUser.size() == 0) {
                    mView.getBindUsersFail();
                    T.show(R.string.get_data_failure);
                    return;
                }
                mView.getBindUsersSuccess(bindDoorbellUser);
            }
        });
    }

    //用户解绑
    @Override
    public void userUnbind() {
        mView.showProgressDialog();
        UserDoorbellRequest userDoorbellRequest = new UserDoorbellRequest(mUserId, mView.getDoorbell().getDeviceId());
        mModel.delDevice(userDoorbellRequest,
                new OnHttpRequestListener<Boolean>() {
                    @Override
                    public void onFailure(int errorCode, String desc) {
                        if (mView == null) return;
                        mView.cancelProgressDialog();
                        T.show(desc);
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (mView == null) return;
                        mView.cancelProgressDialog();
                        mView.userUnbindSuccess();
                    }
                });
    }

    /**
     * 指定新管理员并解绑当前
     */
    @Override
    public void appointAdmin(BindDoorbellUser bindDoorbellUser) {
        if (bindDoorbellUser == null) return;
        mView.showProgressDialog();
        SetAdminRequest setAdminRequest =new SetAdminRequest(mUserId
                ,mView.getDoorbell().getDeviceId(),
                bindDoorbellUser.getUserId());
        mModel.setAdminUnbindDoorbell(setAdminRequest,
                new OnHttpRequestListener<Boolean>() {
                    @Override
                    public void onFailure(int errorCode, String desc) {
                        if (mView == null) return;
                        mView.cancelProgressDialog();
                        T.show(desc);
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (mView == null) return;
                        mView.cancelProgressDialog();
                        mView.appointAdminAndUnbindSuccess();//指定新管理员并解绑成功
                    }
                });
    }

    @Override
    public void adminUnbind() {
        //管理员解绑并解绑其他用户
        mView.showProgressDialog();
        UserDoorbellRequest userDoorbellRequest =new UserDoorbellRequest(mUserId,mView.getDoorbell().getDeviceId());
        mModel.adminUnbind(userDoorbellRequest, new OnHttpRequestListener<Boolean>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                mView.cancelProgressDialog();
                T.show(desc);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                if (mView == null) return;
                mView.cancelProgressDialog();
                mView.adminUnbindSuccess();
            }
        });
    }

    @Override
    public void attachView(DoorbellParamsContract.View view) {
        super.attachView(view);
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }

    @Override
    public void detachView() {
        super.detachView();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    //获取设备参数
    @Override
    public void getDoorbellParams() {
        mView.showProgressDialog();
        CommandControl.getDoorbellParamsCommand(mView.getDoorbell().getDeviceId());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDoorbellParamsAction(NIMMessageTextAction textAction) {
        if (mView == null) return;
        mView.cancelProgressDialog();
        CommandJson commandJson = textAction.getParcelableExtra(Constant.COMMAND);
        if (!CommandJson.CommandType.DOORBELL_PARAMS_GET_RESPONSE.equals(commandJson.getCommandType()))
            return;
        DoorbellAllParam doorbellAllParam = GsonUtil.fromJson(commandJson.getFlag(), DoorbellAllParam.class);
        if (doorbellAllParam == null) return;
        mView.getModeParamsSuccess(doorbellAllParam.getDoorbellModelParam());
        mView.getSensorParamsSuccess(doorbellAllParam.getDoorbellSensorParam());
    }


    @Override
    public DoorbellParamsContract.Model attacheModel() {
        return new DoorbellParamsModel();
    }
}
