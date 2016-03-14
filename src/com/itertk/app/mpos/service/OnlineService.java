package com.itertk.app.mpos.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 * 检测在线
 */
public class OnlineService extends IntentService {
    private static final String TAG = "OnlineService";
    private boolean run;
    private long timeout = 30000;
    public static final String ACTION_CHECK_ONLINE = "com.itertk.app.mpos.action.CheckOnline";


    public OnlineService() {
        super("OnlineService");
        Log.d(TAG, "OnlineService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        run = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        run = false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_ONLINE.equals(action)) {
                handleCheckOnline();
            }
        }
    }

    private void handleCheckOnline() {
        while (run) {
            Log.d(TAG, "handleCheckOnline");
            ((MPosApplication) getApplication()).getMsgBuilder().buildSyncClientUpdateCheck("1.0.0").send(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "recv onFailure");
                    ((MPosApplication) getApplication()).setOnlineState(false);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    try {
                        LinkeaResponseMsg.ClientUpdateCheckResponseMsg clientUpdateCheckResponseMsg =
                                LinkeaResponseMsgGenerator.generateClientUpdateCheckResponseMsg(responseString);
                        if (clientUpdateCheckResponseMsg.isSuccess()) {
                            ((MPosApplication) getApplication()).setOnlineState(true);
                        } else {
                            ((MPosApplication) getApplication()).setOnlineState(false);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                }
            });

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
