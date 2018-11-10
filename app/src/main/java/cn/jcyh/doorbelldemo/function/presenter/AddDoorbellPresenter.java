package cn.jcyh.doorbelldemo.function.presenter;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jcyh.doorbelldemo.base.BasePresenter;
import cn.jcyh.doorbelldemo.bus.nim.NIMMessageTextAction;
import cn.jcyh.doorbelldemo.constant.Constant;
import cn.jcyh.doorbelldemo.control.ControlCenter;
import cn.jcyh.doorbelldemo.function.contract.AddDoorbellContract;
import cn.jcyh.doorbelldemo.function.model.AddDoorbellModel;
import cn.jcyh.eaglekinglockdemo.R;
import cn.jcyh.eaglekinglockdemo.http.OnHttpRequestListener;
import cn.jcyh.nimlib.command.ndk.CommandControl;
import cn.jcyh.nimlib.entity.CommandJson;
import cn.jcyh.nimlib.entity.UserDoorbellRequest;
import cn.jcyh.nimlib.entity.UserRequest;
import cn.jcyh.utils.L;
import cn.jcyh.utils.T;

/**
 * Created by jogger on 2018/7/3.
 */
public class AddDoorbellPresenter extends BasePresenter<AddDoorbellContract.View, AddDoorbellContract.Model> implements AddDoorbellContract.Presenter {
    private EventBus mEventBus;
    private UserRequest mUserRequest;

    @Override
    public void attachView(AddDoorbellContract.View view) {
        super.attachView(view);
        mUserRequest = ControlCenter.getUserManager().getUser();
        mEventBus = EventBus.getDefault();
        if (!mEventBus.isRegistered(this))
            mEventBus.register(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mEventBus.isRegistered(this))
            mEventBus.unregister(this);
    }

    @Override
    public void sendBindRequest() {
        if (TextUtils.isEmpty(mView.getDeviceID())) {
            T.show(R.string.input_no_null);
            return;
        }
        mView.showProgressDialog();
        L.e("-------发送绑定指令："+mView.getDeviceID()+","+ControlCenter.getUserManager().getUser().getUserName());
        CommandControl.sendBindCommand(mView.getDeviceID(),ControlCenter.getUserManager().getUser().getUserName());
    }


    @Override
    public AddDoorbellContract.Model attacheModel() {
        return new AddDoorbellModel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNIMMessageAction(NIMMessageTextAction messageAction) {
        CommandJson commandJson = messageAction.getParcelableExtra(Constant.COMMAND);
        String fromAccount = messageAction.getStringExtra(Constant.FROM_ACCOUNT);
        L.e("---------onNIMMessageAction"+fromAccount+":"+mView.getDeviceID().toLowerCase());
        if (!fromAccount.toLowerCase().equals(mView.getDeviceID().toLowerCase())) return;
        if (commandJson.getCommandType().equals(CommandJson.CommandType
                .BIND_DOORBELL_RESPONSE)) {
            switch (commandJson.getFlag()) {
                case "0":
                    //拒绝绑定
                    mView.cancelProgressDialog();
                    T.show(R.string
                            .target_reject_bind);
                    break;
                case "1":
                    // 开始绑定设备
                    startBind();
                    break;
                case "2":
                    //已绑定
                    mView.cancelProgressDialog();
                    T.show(R.string.binded);
                    break;
                case "3":
                    //猫眼网络存在问题
                    mView.cancelProgressDialog();
                    T.show(R.string
                            .doorbell_net_error);
                    break;
                default:
                    mView.cancelProgressDialog();
                    break;
            }
        }
    }

    /**
     * 绑定到服务器
     */
    private void startBind() {
        UserDoorbellRequest userDoorbellRequest =new UserDoorbellRequest(mUserRequest.getUserId(), mView.getDeviceID());
        mModel.bindDoorbell(userDoorbellRequest, new OnHttpRequestListener<Boolean>() {
            @Override
            public void onFailure(int errorCode, String desc) {
                if (mView == null) return;
                mView.cancelProgressDialog();
                T.show(desc);
            }

            @Override
            public void onSuccess(Boolean aBoolean) {
                if (mView == null) return;
                T.show(R.string.add_succ);
                mView.cancelProgressDialog();
                ControlCenter.getDoorbellManager().setLastDoorbellID(mView.getDeviceID());
                mView.addDoorbellSuccess();
            }
        });
    }
}
