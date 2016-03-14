package com.itertk.app.mpos.account;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.config.UpdateFragment;
import com.itertk.app.mpos.trade.pos.OrderFragment;
import com.itertk.app.mpos.trade.pos.ProductStatisticFragment;
import com.itertk.app.mpos.trade.pos.SaleStatisticFragment;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/*
* 账户管理主界面
* */
public class AccountHomeActivity extends Activity {
    LinearLayout btnAccountCenter;
    LinearLayout btnAccountLevel;
    LinearLayout btnCash;
    LinearLayout btnSaleRecord;
    LinearLayout btnSaleStatistic;
    LinearLayout btnProductStatistic;


    AccountCenterFragment accountCenterFragment;
    AccountLevelFragment accountLevelFragment;
    AccountCashFragment accountCashFragment;
    OrderFragment accountSaleRecordFragment;
    SaleStatisticFragment accountsaleStatisticFragment;
    ProductStatisticFragment accountproductStatisticFragment;
    MPosApplication mPosApplication;

    FragmentTransaction transaction;
    String TAG="AccountHomeActivity";
    void onBtnAccountCenter(){
        btnAccountCenter.setSelected(true);
        btnAccountLevel.setSelected(false);
        btnCash.setSelected(false);
        btnProductStatistic.setSelected(false);
        btnSaleStatistic.setSelected(false);
        btnSaleRecord.setSelected(false);

        accountCenterFragment = new AccountCenterFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, accountCenterFragment);
        transaction.commit();
    }

    void onBtnAccountLevel(){
        btnAccountCenter.setSelected(false);
        btnAccountLevel.setSelected(true);
        btnCash.setSelected(false);
        btnProductStatistic.setSelected(false);
        btnSaleStatistic.setSelected(false);
        btnSaleRecord.setSelected(false);

        accountLevelFragment = new AccountLevelFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, accountLevelFragment);
        transaction.commit();
    }

    void onBtnCash(){
        btnAccountCenter.setSelected(false);
        btnAccountLevel.setSelected(false);
        btnCash.setSelected(true);
        btnProductStatistic.setSelected(false);
        btnSaleStatistic.setSelected(false);
        btnSaleRecord.setSelected(false);

        accountCashFragment = new AccountCashFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, accountCashFragment);
        transaction.commit();
    }

    void onBtnSaleRecord(){
        btnAccountCenter.setSelected(false);
        btnAccountLevel.setSelected(false);
        btnCash.setSelected(false);
        btnProductStatistic.setSelected(false);
        btnSaleStatistic.setSelected(false);
        btnSaleRecord.setSelected(true);

        accountSaleRecordFragment = new OrderFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, accountSaleRecordFragment);
        transaction.commit();
    }

    void onBtnSaleStatistic(){
        btnAccountCenter.setSelected(false);
        btnAccountLevel.setSelected(false);
        btnCash.setSelected(false);
        btnProductStatistic.setSelected(false);
        btnSaleStatistic.setSelected(true);
        btnSaleRecord.setSelected(false);

        accountsaleStatisticFragment = new SaleStatisticFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, accountsaleStatisticFragment);
        transaction.commit();
    }

    void onBtnProductStatistic(){
        btnAccountCenter.setSelected(false);
        btnAccountLevel.setSelected(false);
        btnCash.setSelected(false);
        btnProductStatistic.setSelected(true);
        btnSaleStatistic.setSelected(false);
        btnSaleRecord.setSelected(false);

        accountproductStatisticFragment = new ProductStatisticFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, accountproductStatisticFragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_home);
        MyActionbar.setAccountActionBarLayout(this);

        btnAccountCenter = (LinearLayout)findViewById(R.id.btnAccountCenter);
        btnAccountCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnAccountCenter();
            }
        });

        btnAccountLevel = (LinearLayout)findViewById(R.id.btnAccountLevel);
        btnAccountLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnAccountLevel();
            }
        });

        btnCash = (LinearLayout)findViewById(R.id.btnCash);
        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnCash();
            }
        });

        btnSaleRecord = (LinearLayout)findViewById(R.id.btnSaleRecord);
        btnSaleRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onBtnSaleRecord();
            }
        });
        btnSaleStatistic=(LinearLayout)findViewById(R.id.btnSaleStatistic);
        btnSaleStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnSaleStatistic();
            }
        });
        btnProductStatistic=(LinearLayout)findViewById(R.id.btnProductStatistic);
        btnProductStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBtnProductStatistic();
            }
        });
        mPosApplication=(MPosApplication)getApplication();

        onBtnAccountCenter();
    }

    @Override
    protected void onResume(){
        super.onResume();
        refresh();
    }

    private void refresh() {
        final LoadingDialog loadingDialog=new LoadingDialog(this,R.style.MyDialog);
        loadingDialog.show();
        mPosApplication.getMsgBuilder().buildUserInfoMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                try {
                    LinkeaResponseMsg.UserInfoResponseMsg userInfoResponseMsg =
                            LinkeaResponseMsgGenerator.generateUserInfoResponseMsg(responseString);
                    if(userInfoResponseMsg.success){
                        mPosApplication.getMember().member.setTo_cash_amount(userInfoResponseMsg.member.to_cash_amount);
                        mPosApplication.getMember().member.setcash_amount(userInfoResponseMsg.member.amount);
                        onBtnAccountCenter();
                        loadingDialog.dismiss();
                    }else {
                        String erroInfo = userInfoResponseMsg.result_code_msg;
                        Log.i(TAG,erroInfo);
                        loadingDialog.dismiss();
                    }

                } catch (Exception e) {
                    Log.i(TAG,e.toString());
                    loadingDialog.dismiss();
                }
            }
        });
    }


                @Override
                public boolean onCreateOptionsMenu (Menu menu){
                    // Inflate the menu; this adds items to the action bar if it is present.
                    //getMenuInflater().inflate(R.menu.account_home, menu);
                    return true;
                }

                @Override
                public boolean onOptionsItemSelected (MenuItem item){
                    // Handle action bar item clicks here. The action bar will
                    // automatically handle clicks on the Home/Up button, so long
                    // as you specify a parent activity in AndroidManifest.xml.
                    int id = item.getItemId();
                    if (id == R.id.action_settings) {
                        return true;
                    }
                    return super.onOptionsItemSelected(item);
                }

                @Override
                public boolean onKeyDown ( int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                        return true;
                    }
                    return super.onKeyDown(keyCode, event);
                }
            }
