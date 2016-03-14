package com.itertk.app.mpos;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.config.UpdateManager;
import com.itertk.app.mpos.login.LoginActivity;
import com.itertk.app.mpos.login.TermInfoActivity;
import com.itertk.app.mpos.login.TerminalActivateActivity;
import com.itertk.app.mpos.map.BaiduMapChoiceAddressActivity;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/*
* 启动界面
* */
public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSystemBarVisible(this, false);
        setContentView(R.layout.activity_start);

        final MPosApplication mPosApplication = MPosApplication.getInstance();
        mPosApplication.getMyTTS().speak("欢迎使用旺铺宝");

        LinkeaRequest.checkApplicationUpdate(StartActivity.this, new LinkeaRequest.OnRequestResultListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "连接平台成功，进入在线模式", Toast.LENGTH_SHORT).show();
                mPosApplication.setOnlineState(true);
                checkMode();
            }

            @Override
            public void onFailure() {
                //but no reponse here when network offline,
                Toast.makeText(getApplicationContext(), "连接平台失败，进入离线模式", Toast.LENGTH_SHORT).show();
                checkMode();
            }
        });

   }

    private void checkMode(){
        if(isTerminalActivated()){
            if(isTermInfoShow()) {
                if (isGPSUpload()) {
                    startLoginActivity();
                } else {
                    startGPSUpload();
                }
            }else{
                startTermInfoShow();
            }

        }else {
            startActiveDevice();
        }
    }

    public void startLoginActivity() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Intent it = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(it);
                StartActivity.this.finish();
            }
        };
        timer.schedule(task, 2000);


    }

    private void startActiveDevice(){
        startActivity(new Intent(StartActivity.this, TerminalActivateActivity.class));
        this.finish();
    }

    private void startTermInfoShow(){
        Intent intent=new Intent(this, TermInfoActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("posid",posId);
        bundle.putString("termid",terminalId);
        bundle.putString("termmac",terminalMac);
        intent.putExtra("TERMINFO",bundle);
        startActivity(intent);
        this.finish();
    }

    private void startGPSUpload(){
        startActivity(new Intent(StartActivity.this, BaiduMapChoiceAddressActivity.class));
        this.finish();
    }

    String terminalId,terminalMac,posId;
    private boolean isTerminalActivated() {
        SharedPreferences sharedPre = getSharedPreferences(LoginActivity.terminalConfig, MODE_PRIVATE);
        terminalId = sharedPre.getString(LoginActivity.keyTerminalId, "");
        terminalMac = sharedPre.getString(LoginActivity.keyTerminalMac, "");
        posId=sharedPre.getString(LoginActivity.keyPosId,"");

        if (terminalId.isEmpty() || terminalMac.isEmpty())
            return false;
        else {
            MPosApplication mPosApplication;
            mPosApplication=(MPosApplication) getApplication();
            mPosApplication.getMsgBuilder().setTerm_id(terminalId);
            mPosApplication.getMsgBuilder().setTerm_mac(terminalMac);
            return true;
        }
    }

    private boolean isGPSUpload() {
        SharedPreferences sharedPre = getSharedPreferences(LoginActivity.terminalConfig, MODE_PRIVATE);
        String gpsFlag = sharedPre.getString(LoginActivity.KeyGPSFlag, "");

        if (gpsFlag.isEmpty()) {
            return false;
        }
        else {
            return true;
        }
    }

    private boolean isTermInfoShow(){
        return Utils.getInfo(this, "termInfoShow");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setSystemBarVisible(final Activity context, boolean visible) {
        int flag = context.getWindow().getDecorView().getSystemUiVisibility();   // 获取当前SystemUI显示状态
        // int fullScreen = View.SYSTEM_UI_FLAG_SHOW_FULLSCREEN;
        int fullScreen = 0x8;   // 4.1 View.java的源码里面隐藏的常量SYSTEM_UI_FLAG_SHOW_FULLSCREEN，其实Eclipse里面也可以调用系统隐藏接口，重新提取下android.jar，这里就不述了。
        if (visible) {   // 显示系统栏
            if ((flag & fullScreen) != 0) {  // flag标志位中已经拥有全屏标志SYSTEM_UI_FLAG_SHOW_FULLSCREEN
                context.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);   // 显示系统栏
            }
        } else {    // 隐藏系统栏
            if ((flag & fullScreen) == 0) {  // flag标志位中不存在全屏标志SYSTEM_UI_FLAG_SHOW_FULLSCREEN
                context.getWindow().getDecorView().setSystemUiVisibility(flag | fullScreen); // 把全屏标志位加进去
            }
        }
    }


}
