package com.itertk.app.mpos.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
/*
* 修改密码
* */
public class ModifyPasswordActivity extends Activity {
    final private static String TAG = "ModifyPasswordActivity";
    TextView textPasswordConfirm;
    TextView textPassword;
    TextView textOriginalPassword;
    Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        MyActionbar.setLoginActionBarLayout(this, "修改密码");


        textOriginalPassword = (TextView) findViewById(R.id.textOriginalPassword);
        textPassword = (TextView) findViewById(R.id.textPassword);
        textPasswordConfirm = (TextView) findViewById(R.id.textPasswordConfirm);


        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textOriginalPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "原密码为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPassword.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "新密码为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPasswordConfirm.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请确认新密码",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!textPassword.getText().toString().equals(textPasswordConfirm.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "两次新密码输入不一致",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                ((MPosApplication) getApplication()).getMsgBuilder().buildUpdatePasswordMsg(textOriginalPassword.getText().toString(), textPassword.getText().toString()).send(new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, throwable.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.UpdatePasswordResponseMsg updatePasswordResponseMsg = LinkeaResponseMsgGenerator.generateUpdatePasswordResponseMsg(responseString);
                            if (updatePasswordResponseMsg.isSuccess()) {
                                Log.d(TAG, "修改密码成功");
                                finish();
                            } else {
                                Log.d(TAG, "修改密码失败");
                                String erroInfo = updatePasswordResponseMsg.result_code_msg;
                                Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, erroInfo);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());
                        }
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.modify_password, menu);
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
