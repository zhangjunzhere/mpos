package com.itertk.app.mpos.config;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
/*
* 系统设置主界面
* */
public class SystemConfigActivity extends Activity implements View.OnClickListener{
    //LinearLayout btnShop = null;
    LinearLayout btnUpdate = null;
    LinearLayout btnBackup = null;
    LinearLayout btnUserCenter = null;
    LinearLayout btnDevice = null;
    LinearLayout btnTechnicalSupport = null;

    UserCenterFragment userCenterFragment;
    DeviceFragment deviceFragment;
    //ShopFragment shopFragment = null;
    UpdateFragment updateFragment = null;
    BackupFragment backupFragment = null;
    TechnocialSupportFragment technocialSupportFragment = null;

    FragmentTransaction transaction;

//    private void onbtnShop(){
//        btnShop.setSelected(true);
//        btnUpdate.setSelected(false);
//        btnBackup.setSelected(false);
//        if (null == shopFragment)
//        {
//            shopFragment = new ShopFragment();
//        }
//        transaction = getFragmentManager().beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        transaction.replace(R.id.set_container, shopFragment);
//        transaction.commit();
//    }

    private void onbtnUpdate(){
        //btnShop.setSelected(false);
        btnUpdate.setSelected(true);
        btnBackup.setSelected(false);
        btnUserCenter.setSelected(false);
        btnDevice.setSelected(false);
        btnTechnicalSupport.setSelected(false);
        if (null == updateFragment)
        {
            updateFragment = new UpdateFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, updateFragment);
        transaction.commit();
    }

    private void onbtnBackup(){
        //btnShop.setSelected(false);
        btnUpdate.setSelected(false);
        btnBackup.setSelected(true);
        btnUserCenter.setSelected(false);
        btnDevice.setSelected(false);
        btnTechnicalSupport.setSelected(false);
        if (null == backupFragment)
        {
            backupFragment = new BackupFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, backupFragment);
        transaction.commit();
    }

    private void onbtnUserCenter(){
        //btnShop.setSelected(false);
        btnUpdate.setSelected(false);
        btnBackup.setSelected(false);
        btnUserCenter.setSelected(true);
        btnDevice.setSelected(false);
        btnTechnicalSupport.setSelected(false);
        if (null == userCenterFragment)
        {
            userCenterFragment = new UserCenterFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, userCenterFragment);
        transaction.commit();
    }

    private void onbtnDevice(){
        btnUpdate.setSelected(false);
        btnBackup.setSelected(false);
        btnUserCenter.setSelected(false);
        btnTechnicalSupport.setSelected(false);
        btnDevice.setSelected(true);
        if (null == deviceFragment)
        {
            deviceFragment = new DeviceFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, deviceFragment);
        transaction.commit();
    }

    private void onbtnTechnocialSupport(){
        btnUpdate.setSelected(false);
        btnBackup.setSelected(false);
        btnUserCenter.setSelected(false);
        btnDevice.setSelected(false);
        btnTechnicalSupport.setSelected(true);
        if (null == technocialSupportFragment)
        {
            technocialSupportFragment = new TechnocialSupportFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.set_container, technocialSupportFragment);
        transaction.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //case R.id.btnShop: onbtnShop(); break;
            case R.id.btnUpdate: onbtnUpdate(); break;
            case R.id.btnBackup: onbtnBackup(); break;
            case R.id.btnUserCenter: onbtnUserCenter(); break;
            case R.id.btnDevice: onbtnDevice(); break;
            case R.id.btnTechnicalSupport: onbtnTechnocialSupport(); break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_config);
        MyActionbar.setConfigActionBarLayout(this);

//        btnShop = (LinearLayout)findViewById(R.id.btnShop);
//        btnShop.setOnClickListener(this);

        btnUpdate = (LinearLayout)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        btnBackup = (LinearLayout)findViewById(R.id.btnBackup);
        btnBackup.setOnClickListener(this);

        btnUserCenter = (LinearLayout)findViewById(R.id.btnUserCenter);
        btnUserCenter.setOnClickListener(this);

        btnDevice = (LinearLayout)findViewById(R.id.btnDevice);
        btnDevice.setOnClickListener(this);

        btnTechnicalSupport = (LinearLayout)findViewById(R.id.btnTechnicalSupport);
        btnTechnicalSupport.setOnClickListener(this);


        //onbtnShop();

        onbtnDevice();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.system_config, menu);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
