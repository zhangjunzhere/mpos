package com.itertk.app.mpos.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

/*
* 控制面板主界面
* */
public class ConfigHomeActivity extends Activity implements View.OnClickListener{
    ImageButton btnProductConfig = null;
    ImageButton btnUserConfig = null;
    ImageButton btnSystemConfig = null;

    private void onbtnProductConfig(){
        Intent it = new Intent(ConfigHomeActivity.this, ProductConfigActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void onbtnUserConfig(){
        Intent it = new Intent(ConfigHomeActivity.this, UserConfigActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void onbtnSystemConfig(){
        Intent it = new Intent(ConfigHomeActivity.this, SystemConfigActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProductConfig: onbtnProductConfig(); break;
            case R.id.btnUserConfig: onbtnUserConfig(); break;
            case R.id.btnSystemConfig: onbtnSystemConfig(); break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_home);
        MyActionbar.setConfigActionBarLayout(this);

        btnProductConfig = (ImageButton)findViewById(R.id.btnProductConfig);
        btnProductConfig.setOnClickListener(this);

        btnUserConfig = (ImageButton)findViewById(R.id.btnUserConfig);
        btnUserConfig.setOnClickListener(this);

        btnSystemConfig = (ImageButton)findViewById(R.id.btnSystemConfig);
        btnSystemConfig.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.config_home, menu);
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
    protected void onResume() {
        super.onResume();
        //overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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
