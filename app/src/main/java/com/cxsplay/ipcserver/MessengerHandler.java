package com.cxsplay.ipcserver;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by CxS on 2021/3/11 10:24
 */
public class MessengerHandler extends Handler {

    @Override
    public void handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case Constants.MSG_FROM_CLIENT:
                String msgFromService = msg.getData().getString("reply");
                LogUtils.d("---msgFromService--->" + msgFromService);
                break;
            default:
                super.handleMessage(msg);
        }
    }
}
