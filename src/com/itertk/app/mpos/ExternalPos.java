package com.itertk.app.mpos;


import java.math.BigDecimal;

/**
 * Created by Administrator on 2015/1/3.
 * 外部刷卡器基类
 */
public interface ExternalPos {

    public interface ConnectDeviceListener{
        public void onConnectDeviceListener(boolean success);
    }

    public interface CheckCardListener{
        public void onCheckCardListener(boolean posCancel, boolean success, String cardNo, String track1, String track2, String track3,boolean iccard,String icdata,String cardseqno);
    }

    public interface StartPinListener{
        public void onStartPinListener(boolean success, String pinBlock, String ksn);
    }

    public interface UpdateWorkingKeyListener{
        public void onUpdateWorkingKeyListener(boolean success);
    }

    public abstract void connectDevice(ConnectDeviceListener connectDeviceListener);

    public abstract void disconnectDevice();

    public abstract boolean isDeviceConnected();

    public abstract void cancelOperation();

    public abstract void checkCard(CheckCardListener checkCardListener);

    public abstract void startPin(String cardNo,BigDecimal amt, StartPinListener startPinListener);

    public abstract void updateWorkingKey(String macKey, String macCheckValue, String pinKey, String pinCheckValue, String trackKey, String trackCheckValue, UpdateWorkingKeyListener updateWorkingKeyListener);

    public abstract String getSn();
}
