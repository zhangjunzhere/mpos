package com.itertk.app.mpos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.itertk.app.mpos.utility.Arith;
import com.newland.me.ConnUtils;
import com.newland.me.DeviceManager;
import com.newland.me.ME3xDriver;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.ModuleType;
import com.newland.mtype.common.MESeriesConst;
import com.newland.mtype.event.DeviceEvent;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.cardreader.CardReaderResult;
import com.newland.mtype.module.common.cardreader.CardResultType;
import com.newland.mtype.module.common.cardreader.CardRule;
import com.newland.mtype.module.common.cardreader.OpenCardReaderEvent;
import com.newland.mtype.module.common.cardreader.OpenCardType;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.pin.AccountInputType;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.PinInputEvent;
import com.newland.mtype.module.common.pin.PinManageType;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.bluetooth.BlueToothV100ConnParams;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/1/4.
 * Me30刷卡器
 */
public class NewLandME30 implements ExternalPos {
    private final static String TAG = "NewLandME30";
    Context context;
    private DeviceManager deviceManager;
    Device device;
    Handler handler;

    public NewLandME30(Context context) {
        this.context = context;
        deviceManager = ConnUtils.getDeviceManager();
        handler = new Handler();
    }

    @Override
    public void connectDevice(final ConnectDeviceListener connectDeviceListener) {
        final ConnectDeviceListener connectDeviceEventListener = connectDeviceListener;

        if ((device != null) && device.isAlive()) {
            Log.d(TAG, "already connected!!");
            connectDeviceEventListener.onConnectDeviceListener(true);
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                Object[] pairedObjects = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray();
                if (pairedObjects.length <= 0) {
                    Log.d(TAG, "no bluetooth device");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectDeviceEventListener.onConnectDeviceListener(false);
                        }
                    });
                    return;
                }
                BluetoothDevice bluetoothDevice = (BluetoothDevice) pairedObjects[0];
                BlueToothV100ConnParams params = new BlueToothV100ConnParams(bluetoothDevice.getAddress());

                deviceManager.init(context, new ME3xDriver(), params, new DeviceEventListener<ConnectionCloseEvent>() {
                    @Override
                    public void onEvent(ConnectionCloseEvent connectionCloseEvent, Handler handler) {
                        device = null;
                        Log.d(TAG, connectionCloseEvent.toString());
                    }

                    @Override
                    public Handler getUIHandler() {
                        return handler;
                    }
                });

                try {
                    deviceManager.connect();
                    device = deviceManager.getDevice();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectDeviceEventListener.onConnectDeviceListener(true);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "connect device " + e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            connectDeviceEventListener.onConnectDeviceListener(false);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void disconnectDevice() {
        if (isDeviceConnected())
            deviceManager.disconnect();
    }

    @Override
    public boolean isDeviceConnected() {
        if ((device != null) && device.isAlive()) return true;
        return false;
    }

    @Override
    public void cancelOperation() {
        try {
            if (isDeviceConnected()) {
                Log.d(TAG, "reset device");
                device.reset();
            }
        }catch (Exception e){
            Log.d(TAG, e.toString());
            disconnectDevice();
        }

    }

    @Override
    public void updateWorkingKey(final String macKey, final String macCheckValue, final String pinKey, final String pinCheckValue, final String trackKey, final String trackCheckValue,
                                 UpdateWorkingKeyListener updateWorkingKeyListener) {
        final UpdateWorkingKeyListener updateWorkingKeyEventListener = updateWorkingKeyListener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isDeviceConnected()) {
                        Log.d(TAG, "no device connected");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateWorkingKeyEventListener.onUpdateWorkingKeyListener(false);
                            }
                        });
                        return;
                    }

                    PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
                    int mkIndex = Const.MKIndexConst.DEFAULT_MK_INDEX;
                    if (pinInput == null) {
                        Log.d(TAG, "no device pinipnut module");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateWorkingKeyEventListener.onUpdateWorkingKeyListener(false);
                            }
                        });
                        return;
                    }
                    //load main key
                   // pinInput.loadMainKey(KekUsingType.ENCRYPT_TMK,Const.MKIndexConst.DEFAULT_MK_INDEX, ISOUtils.hex2byte("D8411E593B93C9C777D29A8D2B654D1E"));
//                    pinInput.loadMainKeyAndVerify(KekUsingType.ENCRYPT_TMK,Const.MKIndexConst.DEFAULT_MK_INDEX,ISOUtils.hex2byte("D8411E593B93C9C777D29A8D2B654D1E"),ISOUtils.hex2byte("A37CA1EC"),-1);

                    pinInput.loadWorkingKeyAndVerify(WorkingKeyType.PININPUT, mkIndex, Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX,
                            ISOUtils.hex2byte(pinKey),ISOUtils.hex2byte(pinCheckValue));//, ISOUtils.hex2byte(pinCheckValue)

                    pinInput.loadWorkingKeyAndVerify(WorkingKeyType.DATAENCRYPT, mkIndex, Const.DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX,
                            ISOUtils.hex2byte(trackKey),ISOUtils.hex2byte(trackCheckValue));//, ISOUtils.hex2byte(trackCheckValue)

                    pinInput.loadWorkingKeyAndVerify(WorkingKeyType.MAC, mkIndex, Const.MacWKIndexConst.DEFAULT_MAC_WK_INDEX,
                            ISOUtils.hex2byte(macKey),ISOUtils.hex2byte(macCheckValue));//, ISOUtils.hex2byte(macCheckValue)

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateWorkingKeyEventListener.onUpdateWorkingKeyListener(true);
                        }
                    });
                }catch (Exception e){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateWorkingKeyEventListener.onUpdateWorkingKeyListener(false);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void checkCard(CheckCardListener checkCardListener) {
        final CheckCardListener checkCardEventListener = checkCardListener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isDeviceConnected()) {
                        Log.d(TAG, "no device connected");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                            }
                        });
                        return;
                    }

                    final CardReader cardReader = (CardReader) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_CARDREADER);
                    if (cardReader == null) {
                        Log.d(TAG, "no card reader");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                            }
                        });
                        return;
                    }

                    CardReaderResult cardReaderResult = cardReader.openCardReader(new OpenCardType[]{OpenCardType.ICCARD, OpenCardType.SWIPER}, 30, TimeUnit.SECONDS, "请刷卡或插入IC卡", CardRule.ALLOW_LOWER);
                    ModuleType[] openedModuleTypes = cardReaderResult.getOpenedCardReaders();
                    if (openedModuleTypes == null || openedModuleTypes.length <= 0) {
                        Log.d(TAG, "start cardreader,but return is none!may user canceled?");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                checkCardEventListener.onCheckCardListener(true, false, null, null, null, null,false,"","");
                            }
                        });
                        return;
                    }

                    if (openedModuleTypes.length > 1) {
                        Log.d(TAG, "should return only one type of cardread action!but is " + openedModuleTypes.length);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                            }
                        });
                        return;
                    }

                    switch (openedModuleTypes[0]) {
                        case COMMON_SWIPER:
                            CardResultType cardResultType = cardReaderResult.getCardResultType();
                            // logger.info("========刷卡结果=============" + cardResultType.toString());
                            if (cardResultType == CardResultType.SWIPE_CARD_FAILED) {
                                Log.d(TAG, "swip failed!");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                                    }
                                });
                                return;
                            }

                            Swiper swiper = (Swiper) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_SWIPER);
                            final SwipResult swipRslt = swiper.readEncryptResult(new SwiperReadModel[]{SwiperReadModel.READ_FIRST_TRACK, SwiperReadModel.READ_SECOND_TRACK, SwiperReadModel.READ_THIRD_TRACK},
                                    new WorkingKey(Const.DataEncryptWKIndexConst.DEFAULT_TRACK_WK_INDEX), MESeriesConst.TrackEncryptAlgorithm.BY_UNIONPAY_MODEL);
                            if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
                                Log.d(TAG, swipRslt.toString());
                               // final  String ksn = ISOUtils.hexString(swipRslt.getKsn());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkCardEventListener.onCheckCardListener(false, true, swipRslt.getAccount().getAcctNo(),
                                                swipRslt.getFirstTrackData() == null ? "" : ISOUtils.hexString(swipRslt.getFirstTrackData()),
                                                swipRslt.getSecondTrackData() == null ? "" : ISOUtils.hexString(swipRslt.getSecondTrackData()),
                                                swipRslt.getThirdTrackData() == null ? "" : ISOUtils.hexString(swipRslt.getThirdTrackData()),
                                                false,"","");
                                    }
                                });

                                return;
                            } else {
                                Log.d(TAG, "" + swipRslt.getRsltType());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                                    }
                                });
                            }
                        case COMMON_ICCARD:
                        {
                            EventHolder<OpenCardReaderEvent> listener = new EventHolder<OpenCardReaderEvent>();
                            cardReader.openCardReader(new OpenCardType[]{OpenCardType.ICCARD, OpenCardType.SWIPER}, 30, TimeUnit.SECONDS, "请刷卡或插入IC卡", CardRule.ALLOW_LOWER, listener);
                            try {
                                listener.startWait();
                            } catch (InterruptedException e) {
                                cardReader.cancelCardRead();
                               // transferListener.onOpenCardreaderCanceled();
                            } finally {
                              //  clearScreen();
                            }

                            OpenCardReaderEvent event = listener.event;
                             openedModuleTypes = event.getOpenedCardReaders();
                            EmvModule module = (EmvModule) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_EMV);
                            module.setOnlinePinConfig(null);
                            SimpleTransferListener transferListener= new SimpleTransferListener(deviceManager,NewLandME30.this,checkCardEventListener);
                            EmvTransController controller = module.getEmvTransController(transferListener);

                            controller.startEmv(new BigDecimal(1), new BigDecimal("0"), true);

                            break;
                        }
                        default:
                            Log.d(TAG, "not support cardreader module:" + openedModuleTypes[0]);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                                }
                            });
                    }

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                        }
                    });
                }
            }
        }).start();
    }
    public  void setIcdata(final SwipResult swipRslt, ExternalPos.CheckCardListener listener,final String icdata,final  String cardseqno) {
        final ExternalPos.CheckCardListener checkCardEventListener = listener;
        if (swipRslt.getRsltType() == SwipResultType.SUCCESS) {
            Log.d(TAG, swipRslt.toString());

            handler.post(new Runnable() {
                @Override
                public void run() {
                    checkCardEventListener.onCheckCardListener(false, true, swipRslt.getAccount().getAcctNo(),
                            swipRslt.getFirstTrackData() == null ? "" : ISOUtils.hexString(swipRslt.getFirstTrackData()),
                            swipRslt.getSecondTrackData() == null ? "" : ISOUtils.hexString(swipRslt.getSecondTrackData()),
                            swipRslt.getThirdTrackData() == null ? "" : ISOUtils.hexString(swipRslt.getThirdTrackData()),true,icdata,cardseqno);
                }
            });

            return;
        } else {
            Log.d(TAG, "" + swipRslt.getRsltType());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    checkCardEventListener.onCheckCardListener(false, false, null, null, null, null,false,"","");
                }
            });
        }
    }
    @Override
    public void startPin(final String cardNo,final BigDecimal amt, StartPinListener startPinListener) {
        final StartPinListener startPinEventListener = startPinListener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cardNo == null || cardNo.length() == 0) {
                    Log.d(TAG, "cardno is null");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startPinEventListener.onStartPinListener(false, null, null);
                        }
                    });
                    return;
                }

                if (!isDeviceConnected()) {
                    Log.d(TAG, "no device connected");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startPinEventListener.onStartPinListener(false, null, null);
                        }
                    });
                    return;
                }

                try {
                    final PinInput pinInput = (PinInput) deviceManager.getDevice().getStandardModule(ModuleType.COMMON_PININPUT);
                    if (pinInput == null) {
                        Log.d(TAG, "no device pinipnut module");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startPinEventListener.onStartPinListener(false, null, null);
                            }
                        });
                        return;
                    }

                    DecimalFormat df = new DecimalFormat("#.00");
                    String msg = "消费金额为:" + df.format(amt) + "\n请输入密码:";
                    if(amt.compareTo(Arith.newZeroBigDecimal())<0)
                    {
                        msg = "请输入密码";
                    }
                    final PinInputEvent pinInputEvent = pinInput.startStandardPinInput(new WorkingKey(Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX), PinManageType.MKSK,
                            AccountInputType.USE_ACCOUNT, cardNo, 6, new byte[]{'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F', 'F'}, true, msg, 30, TimeUnit.SECONDS);

                    if (pinInputEvent.isSuccess()) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startPinEventListener.onStartPinListener(true, ISOUtils.hexString(pinInputEvent.getEncrypPin()), ISOUtils.hexString(pinInputEvent.getKsn()));
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                startPinEventListener.onStartPinListener(false, null, null);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            startPinEventListener.onStartPinListener(false, null, null);
                        }
                    });
                }

            }
        }).start();
    }
    //smile add getsn
    @Override
    public String getSn()
    {
        if(!isDeviceConnected())
        {
            return null;
        }
        if(device != null)
        {
            return  device.getDeviceInfo().getCSN();
        }
        return  null;
    }
    //end smile
    private class EventHolder<T extends DeviceEvent> implements DeviceEventListener<T> {

        private T event;

        private final Object syncObj = new Object();

        private boolean isClosed = false;

        public void onEvent(T event, Handler handler) {
            this.event = event;
            synchronized (syncObj) {
                isClosed = true;
                syncObj.notify();
            }
        }

        public Handler getUIHandler() {
            return null;
        }

        void startWait() throws InterruptedException {
            synchronized (syncObj) {
                if (!isClosed)
                    syncObj.wait();
            }
        }

    }

}
