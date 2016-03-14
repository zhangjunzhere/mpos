package com.itertk.app.mpos.config;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.itertk.app.mpos.BlankFragment;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
/*
* 用户配置主界面
* */
public class UserConfigActivity extends Activity implements View.OnClickListener{
    LinearLayout btnUser = null;
    //LinearLayout btnCard = null;

    UserListFragment userListFragment = null;
    BankcardListFragment bankcardListFragment = null;
    FragmentTransaction transaction;

    private void clearItemArea(){
        BlankFragment blankFragment = new BlankFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, blankFragment);
        transaction.commit();
    }

    private void onbtnUser(){
        btnUser.setSelected(true);
        //btnCard.setSelected(false);

        if (null == userListFragment)
        {
            userListFragment = new UserListFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_container, userListFragment);
        transaction.commit();

        clearItemArea();
    }

    private void onbtnCard(){
        //btnCard.setSelected(true);
        btnUser.setSelected(false);

        if (null == bankcardListFragment)
        {
            bankcardListFragment = new BankcardListFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_container, bankcardListFragment);
        transaction.commit();

        clearItemArea();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnUser: onbtnUser(); break;
            //case R.id.btnCard: onbtnCard(); break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);
        MyActionbar.setConfigActionBarLayout(this);

        btnUser = (LinearLayout)findViewById(R.id.btnUser);
        btnUser.setOnClickListener(this);

        //btnCard = (LinearLayout)findViewById(R.id.btnCard);
        //btnCard.setOnClickListener(this);







        onbtnUser();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.user_config, menu);
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
