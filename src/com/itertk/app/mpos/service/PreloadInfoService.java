package com.itertk.app.mpos.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.trade.pos.UpLoadOrderHelper;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.map.BaiduLocation;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;


/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 * 预加载基础信息
 */
public class PreloadInfoService extends Service {
    private static final String TAG = "PreloadInfoService";
    public static final String ACTION_PRELOAD_INFO = "com.itertk.app.mpos.action.preloadinfo";
    public static final String ACTION_PRELOAD_MEMBER = "com.itertk.app.mpos.action.preload_member";

    public static String bankorderPath = "";

    private long timeout = 3000;

    private BaiduLocation baiduLocation;
    public static final int UPLOAD_ORDER_NEXT = 1001;

    BankOrderFileObserver mBankOrderFileObserver;
    private Context mContext;
    private String postFix = ".png";

    private int MSG_FILE_CHANGED = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        registerBluetTooth();
        baiduLocation = new BaiduLocation();
        baiduLocation.onCreate(this);

        mBankOrderFileObserver = new BankOrderFileObserver(getFilesDir().getPath(), FileObserver.CREATE );
        mBankOrderFileObserver.startWatching();

        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        baiduLocation.onTerminate();
        mBankOrderFileObserver.stopWatching();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected void onHandleIntent(Intent intent) {
        final Semaphore semaphore = new Semaphore(0);
        final int TASK_COUNT = 5;


        Context context = PreloadInfoService.this;

        if(intent == null){
            return;
        }
        String action = intent.getAction();

        if(ACTION_PRELOAD_INFO.equals(action)) {
            LinkeaRequest.downloadProductList(context, semaphore);
            LinkeaRequest.downloadProductCatalog(context, semaphore);
            LinkeaRequest.getAdvMsgResponse(context, semaphore);
            LinkeaRequest.getPermissionMsgResponse(context, semaphore);
            LinkeaRequest.queryUserListResponse(context, semaphore);
            mHandler.sendMessage(Message.obtain(mHandler,MSG_FILE_CHANGED));
        }else if(ACTION_PRELOAD_MEMBER.equals(action)) {
            LinkeaRequest.getShopOrderStatusMsgResponse(context, semaphore);
        }
		//       uploadOrder();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
////                        if (semaphore.availablePermits() < TASK_COUNT) {
////                            Thread.sleep(timeout);
////                        }else{
////                            stopSelf();
////                            break;
////                        }
//                        semaphore.acquire(TASK_COUNT);
//                        stopSelf();
//
//                        Log.d(TAG,"PreloadInfoService done");
//
//                    } catch (Exception e) {
//                        Log.e(TAG,e.toString());
//                    }
//                }
//            }
//        }).run();
    }

    void uploadOrder() {
        SaleOrderDao sdao = MPosApplication.getInstance().getDataHelper().getDaoSession().getSaleOrderDao();
        List<SaleOrder> orderlist = sdao.queryBuilder().where(SaleOrderDao.Properties.Upload.eq(false)).list();
        for (SaleOrder s: orderlist)
        {
            UpLoadOrderHelper.uploadOrder(s);
        }
        Log.i("smile","orderlist: "+orderlist.size());
    }
    void registerBluetTooth()
    {
       IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        this.registerReceiver(mReceiver, filter);


        filter = new IntentFilter( BluetoothAdapter.ACTION_STATE_CHANGED );
        this.registerReceiver( mReceiver, filter );
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Log.i("smile", "blue tooth action: " + action);
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                }
            } catch (Exception e) {
                System.out.println("Broadcast Error : " + e.toString());
            }
        }
    };


    /**
     *  file observer to listen bankorder sign images.
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MSG_FILE_CHANGED){
                mHandler.removeMessages(MSG_FILE_CHANGED);
                mHandler.sendEmptyMessageDelayed(MSG_FILE_CHANGED, 2*60*60*1000); //2 hours poll
                onFileChanged();
            }
            super.handleMessage(msg);
        }
    };

    class BankOrderFileObserver extends FileObserver {
        //mask:指定要监听的事件类型，默认为FileObserver.ALL_EVENTS
        public BankOrderFileObserver(String path, int mask) {
            super(path, mask);
        }

        public BankOrderFileObserver(String path) {
            super(path);
        }

        @Override
        public void onEvent(int event, String path) {
            final int action = event & FileObserver.ALL_EVENTS;
            switch (action) {
                case FileObserver.CREATE:
                case FileObserver.MODIFY:
                     mHandler.sendMessage(Message.obtain(mHandler,MSG_FILE_CHANGED));
                    break;
            }
        }
    }


    private void onFileChanged(){
        File[] files = getFilesDir().listFiles();
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(postFix)) {
                String tradeNo = name.substring(0, name.length() - postFix.length());
                String imagebase64 = Utils.readFile(mContext, name);
                uploadSign(mContext, tradeNo, imagebase64);
            }
        }
    }

    void uploadSign(final Context context, final String tradeno,String sign)
    {
        MPosApplication.getInstance().getMsgBuilder().buildPaySignMsg(tradeno,sign).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, "上传签单失败" + tradeno);
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("smile success",responseString);
                Boolean success = false;
                try {
                    LinkeaResponseMsg.PaySignUploadResponseMsg paySignUploadResponseMsg = LinkeaResponseMsgGenerator.generatePaySignResponseMsg(responseString);
                    success = paySignUploadResponseMsg.success;
                }catch (Exception e){
                    Log.e(TAG, "上传签单失败" + tradeno);
                }
                String message = success ?  "上传签单成成功" : "上传签单成失败" ;
                ToastHelper.showToast(context, message + tradeno);
                if(success){
                    mContext.deleteFile( tradeno+postFix);
                }
            }
        });
    }
}
