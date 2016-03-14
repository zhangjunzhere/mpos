package com.itertk.app.mpos;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.nfc.Tag;
import android.os.Handler;
import android.util.Log;


import com.newland.me.DeviceManager;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceRTException;
import com.newland.mtype.ModuleType;
import com.newland.mtype.ProcessTimeoutException;
import com.newland.mtype.common.MESeriesConst;
import com.newland.mtype.conn.DeviceConnParams;
import com.newland.mtype.conn.DeviceConnType;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.emv.SecondIssuanceRequest;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.MacAlgorithm;
import com.newland.mtype.module.common.pin.MacResult;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinInputEvent.NotifyStep;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.tlv.TLVPackage;
import com.newland.mtype.util.Dump;
import com.newland.mtype.util.ISOUtils;

/**
 * 交易过程监听实现,c卡刷卡获取信息
 *
 * @author evil
 */
public class SimpleTransferListener implements TransferListener {
    private String TAG = "SimpleTransferListener";
    private SwipResult swipRslt;
    private Dialog message_dialog;
    private static List L_55TAGS = new ArrayList();
    NewLandME30 pos;
    ExternalPos.CheckCardListener checkCardListener;

    private DeviceManager deviceManager;

    static {
        L_55TAGS.add(0x9f26);
        L_55TAGS.add(0x9F27);
        L_55TAGS.add(0x9F10);
        L_55TAGS.add(0x9F37);
        L_55TAGS.add(0x9F36);
        L_55TAGS.add(0x95);
        L_55TAGS.add(0x9A);
        L_55TAGS.add(0x9C);
        L_55TAGS.add(0x9F02);
        L_55TAGS.add(0x5F2A);
        L_55TAGS.add(0x82);
        L_55TAGS.add(0x9F1A);
        L_55TAGS.add(0x9F03);
        L_55TAGS.add(0x9F33);
        L_55TAGS.add(0x9F74);
        L_55TAGS.add(0x9F34);
        L_55TAGS.add(0x9F35);
        L_55TAGS.add(0x9F1E);
        L_55TAGS.add(0x84);
        L_55TAGS.add(0x9F09);
        L_55TAGS.add(0x9F41);
        L_55TAGS.add(0x91);
        L_55TAGS.add(0x71);
        L_55TAGS.add(0x72);
        L_55TAGS.add(0xDF31);
        L_55TAGS.add(0x9F63);
        L_55TAGS.add(0x8A);
        L_55TAGS.add(0xDF32);
        L_55TAGS.add(0xDF33);
        L_55TAGS.add(0xDF34);
    }

    public SimpleTransferListener(DeviceManager dm, NewLandME30 pos, ExternalPos.CheckCardListener checkCardListener) {
        swipRslt = null;
        deviceManager = dm;
        this.pos = pos;
        this.checkCardListener = checkCardListener;
    }

    @Override
    public void onQpbocFinished(EmvTransInfo context) {
        Log.i("SimpleTransferListener", "onQpbocFinished");

    }

    @Override
    public void onEmvFinished(boolean isSuccess, EmvTransInfo context) throws Exception {
        if (isSuccess) {
            TLVPackage tlvPackage = context.setExternalInfoPackage(L_55TAGS);
            String yu55 = ISOUtils.hexString(tlvPackage.pack());
         //   String ksn =  ISOUtils.hexString(context.getKsn());
            String cardsqeno = "";
            if(context.getCardSequenceNumber().length()==1)
            {
                cardsqeno = "00";
            }
            else  if(context.getCardSequenceNumber().length()==2)
            {
                cardsqeno = "0";
            }
            cardsqeno+=context.getCardSequenceNumber();
            if(cardsqeno!=null)
                Log.i("smile","cardsqeno: "+cardsqeno);
            pos.setIcdata(swipRslt,checkCardListener,yu55,cardsqeno);
        }
        Log.i("SimpleTransferListener", "onEmvFinished " + " success:" + isSuccess);
    }

    @Override
    public void onError(EmvTransController arg0, Exception arg1) {
        Log.i("SimpleTransferListener", "onError " + arg1.getMessage());
    }

    @Override
    public void onFallback(EmvTransInfo arg0) throws Exception {

        startSwipTransfer();
    }

    public SwipResult getTrackText(int flag) {

        int trackKey = Const.DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX;
        Swiper swiper = (Swiper) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
        SwipResult swipRslt = getSwipResult(swiper, trackKey, MESeriesConst.TrackEncryptAlgorithm.BY_UNIONPAY_MODEL, flag);
        if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
            return swipRslt;
        }
        return null;
    }

    private SwipResult getSwipResult(Swiper swiper, int trackKey, String encryptType, int flag) {

        SwipResult swipRslt;
        if (flag == Const.CardType.COMMON) {
            swipRslt = swiper.readEncryptResult(new SwiperReadModel[]{SwiperReadModel.READ_SECOND_TRACK, SwiperReadModel.READ_THIRD_TRACK}, new WorkingKey(trackKey), encryptType);
        } else {
            swipRslt = swiper.readEncryptResult(new SwiperReadModel[]{SwiperReadModel.READ_IC_SECOND_TRACK}, new WorkingKey(trackKey), encryptType);
        }
        return swipRslt;
    }

    @Override
    public void onRequestOnline(EmvTransController controller, EmvTransInfo context) throws Exception {
        // 此处判断是开启度开启操作card_reader_flag=0，PBOC流程 card_reader_flag=1
//        if (((MyApplication) mainActivity.getApplication()).getOpen_card_reader_flag() != 1) {
//
//            mainActivity.appendInteractiveInfoAndShow("开启联机交易:" + context.externalToString(), MessageTag.DATA);
//            mainActivity.appendInteractiveInfoAndShow(">>>>请求在线交易处理", MessageTag.NORMAL);
//            mainActivity.appendInteractiveInfoAndShow("终端验证结果(95):" + (context.getTerminalVerificationResults() == null ? "无返回" : Dump.getHexDump(context.getTerminalVerificationResults())), MessageTag.DATA);
//            mainActivity.appendInteractiveInfoAndShow("应用密文(9f26):" + (context.getAppCryptogram() == null ? "无返回" : Dump.getHexDump(context.getAppCryptogram())), MessageTag.DATA);
//            mainActivity.appendInteractiveInfoAndShow("持卡人验证方法结果(9f34):" + (context.getCvmRslt() == null ? "无返回" : Dump.getHexDump(context.getCvmRslt())), MessageTag.DATA);
//            mainActivity.appendInteractiveInfoAndShow(">>>>卡号:" + context.getCardNo(), MessageTag.DATA);
//            mainActivity.appendInteractiveInfoAndShow(">>>>卡序列号:" + context.getCardSequenceNumber(), MessageTag.DATA);
        if (null != context.getTrack_2_eqv_data()) {
            Log.i("smie", ">>>>二磁道明文:" + ISOUtils.hexString(context.getTrack_2_eqv_data()));
        }

        // 获取IC卡磁道信息
        swipRslt = getTrackText(Const.CardType.ICCARD);
        if (null != swipRslt.getSecondTrackData()) {
            Log.i("smile", ">>>>二磁道密文:" + ISOUtils.hexString(swipRslt.getSecondTrackData()));
        }

        // 此处判断是为外部输入密码操作还是执行流程输入密码操作Ic_pinInput_flag=1为流程输入密码
        // Ic_pinInput_flag=0为外部输入密码
//        int Ic_pinInput_flag = 0;
//        if (Ic_pinInput_flag != 1) {
//           // Log.i("simpleTransfer", "icdata <br>请输入交易密码...");
//           // doPinInput(swipResult);
//
//        } else {
//
//            Log.i("simpleTransfer ", ">>>>密码:" + context.getOnLinePin());
//            // pos.setIcdata(swipResult,checkCardListener);
//        }

        // todo !!!!!!!!!!从该处context中获取ic卡卡片信息后，发送银联8583交易
 //       controller.doEmvFinish(true);
//
          SecondIssuanceRequest request = new SecondIssuanceRequest();
           request.setAuthorisationResponseCode("00");// 取自银联8583规范 39域值,该参数按交易实际值填充
        // request.setIssuerAuthenticationData(arg0);//取自银联8583规范 55域 0x91值,该参数按交易实际值填充
        // request.setIssuerScriptTemplate1(arg0);//取自银联8583规范 55域 0x71值,该参数按交易实际值填充
        // request.setIssuerScriptTemplate2(arg0);//取自银联8583规范 55域 0x72值,该参数按交易实际值填充
//			request.setAuthorisationCode(authorisationCode);//取自银联8583规范 38域值,该参数按交易实际值填充
                  controller.secondIssuance(request);
//        controller.doEmvFinish(true);
//        } else {
        // 获取IC卡刷卡结果
        //  SwipResult swipResult1 =getTrackText(Const.CardType.ICCARD);
        //      ((MyApplication) mainActivity.getApplication()).setSwipResult(swipResult);

        //  }
    }

    @Override
    public void onRequestPinEntry(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
        Log.i("smile", "错误的事件返回，不可能要求密码输入！");
        arg0.cancelEmv();

    }

    @Override
    public void onRequestSelectApplication(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
        Log.i("simpleTransfer", "错误的事件返回，不可能要求应用选择！");
        arg0.cancelEmv();

    }

    @Override
    public void onRequestTransferConfirm(EmvTransController arg0, EmvTransInfo arg1) throws Exception {
        Log.i("simpleTransfer", "错误的事件返回，不可能要求交易确认！");
        arg0.cancelEmv();

    }

    @Override
    public void onOpenCardreaderCanceled() {
        Log.i("simpleTransfer", "用户撤销刷卡操作！");
        //mainActivity.processingUnLock();
    }

    @Override
    public void onSwipMagneticCard(SwipResult swipRslt, BigDecimal amt) {
        startSwipTransfer(swipRslt, amt);
    }

    public void startSwipTransfer() {
        new Thread(new Runnable() {
            @Override
            public void run() {


                // Input amount
                BigDecimal amt = new BigDecimal(0);//(MyApplication) mainActivity.getApplication()).getAmt();
                DecimalFormat df = new DecimalFormat("#.00");
                Log.i("simpleTransfer", "输入金额为:" + df.format(amt).toString() + "<br>请刷卡...");
                //   connected_device.getController().clearScreen();

                // 刷卡

                try {
                    // 密码输入
                    doPinInput(swipRslt);

                } catch (Exception e) {


                }
            }

        }).start();
    }

    public void startSwipTransfer(final SwipResult swipRslt, final BigDecimal amt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
    }

    private PinInputEvent inputPwd(String acctHash, BigDecimal amt, SwipResult swipRslt, DeviceEventListener<PinInputEvent> listener) throws Exception {
        DecimalFormat df = new DecimalFormat("#.00");
        String msg = "消费金额为:" + df.format(amt) + "\n请输入交易密码:";
        if (listener == null)
            return startPininput(acctHash, 6, msg);
        else
            startPininput(acctHash, 6, msg, listener);

        return null;
    }

    public PinInputEvent startPininput(String acctHash, int inputMaxLen, String msg) {

        if (acctHash == null) {
            // throw new DeviceRTException(AppExCode.GET_PININPUT_FAILED, "acctHash should not be null!");
        }

        PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
        PinInputEvent event = pinInput.startStandardPinInput(new WorkingKey(Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX), PinManageType.MKSK, AccountInputType.USE_ACCT_HASH, acctHash, inputMaxLen, new byte[]{'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F'}, true, msg, 30, TimeUnit.SECONDS);

        return event;
    }

    public void startPininput(String acctHash, int inputMaxLen, String msg, DeviceEventListener<PinInputEvent> listener) {

        if (acctHash == null) {
            // throw new DeviceRTException(AppExCode.GET_PININPUT_FAILED, "acctHash should not be null!");
        }

        PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
        pinInput.startStandardPinInput(new WorkingKey(Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX), PinManageType.MKSK, AccountInputType.USE_ACCT_HASH, acctHash, inputMaxLen, new byte[]{'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F'}, true, msg, 30, TimeUnit.SECONDS, listener);
    }

    public void doPinInput(SwipResult swipRslt) throws Exception {
        PinInputEvent event = null;
        //  DeviceConnParams connParams = getDeviceConnParams();

        //    Log.i(TAG,"connParams: "+connParams.toString());
        //if (DeviceConnType.IM81CONNECTOR_V100 != connParams.getConnectType())
        {
            Log.i(TAG, "IM81CONNECTOR_V100");
            event = inputPwd(swipRslt.getAccount().getAcctHashId(), new BigDecimal(1), swipRslt, null);
            if (event == null) {
                Log.i(TAG, "密码输入撤销");
                return;
            }
            Log.i(TAG, "密码输入完成");
            Log.i(TAG, "ksn:" + Dump.getHexDump(event.getKsn()));
            Log.i(TAG, "密码:" + Dump.getHexDump(event.getEncrypPin()));

        }
        /*
        else {// IM81连接方式
            Log.i(TAG,"IM81连接方式");
            event = inputPwd(swipRslt.getAccount().getAcctHashId(), new BigDecimal(100), swipRslt, new DeviceEventListener<PinInputEvent>() {

                @Override
                public Handler getUIHandler() {
                    return null;
                }

                @Override
                public void onEvent(PinInputEvent event, Handler arg1) {
                    if (event.isProcessing()) {
                        NotifyStep step = event.getNotifyStep();

                    } else if (event.isUserCanceled()) {
                        Log.i(TAG, "密码输入撤销");
                    } else if (event.isSuccess()) {
                        Log.i(TAG,"密码输入完成");
                        Log.i(TAG,"ksn:" + Dump.getHexDump(event.getKsn()));
                        Log.i(TAG,"密码:" + Dump.getHexDump(event.getEncrypPin()));
                        Log.i(TAG, "交易完成");
                    } else {
                        Log.e(TAG, "密码输入失败!", event.getException());
                     //   mainActivity.appendInteractiveInfoAndShow("密码输入失败!" + event.getException(), MessageTag.ERROR);
                     //   mainActivity.processingUnLock();
                    }
                }
            });
        }
        */
    }

    public DeviceConnParams getDeviceConnParams() {
        Device device = deviceManager.getDevice();
        if (device == null)
            return null;

        return (DeviceConnParams) device.getBundle();
    }

    private void reDoSwipeCard() {

    }

}

