package com.itertk.app.mpos.login;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.MyPresentation;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
/*
* 忘记密码
* */
public class ForgetPasswordActivity extends Activity implements View.OnClickListener {
    static final String TAG = "ForgetPasswordActivity";
    Button btnGetCheckCode = null;
    Button btnSubmit = null;
    EditText textPhone = null;
    EditText textCheckCode = null;
    EditText textPassword;
    EditText textPasswordConfirm;
    MPosApplication mPosApplication;


    private void onbtnGetCheckCode() {
        if (textPhone.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "手机号为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mPosApplication.getMsgBuilder().setMember_id(textPhone.getText().toString());


        final LoadingDialog loadingDialog = new LoadingDialog(ForgetPasswordActivity.this, R.style.MyDialog);
        loadingDialog.show();

        mPosApplication.getMsgBuilder().buildGetSmsCodeMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingDialog.dismiss();
                try {
                    LinkeaResponseMsg.GetSmsCodeResponseMsg getSmsCodeResponseMsg =
                            LinkeaResponseMsgGenerator.generateGetSmsCodeResponseMsg(responseString);
                    if (getSmsCodeResponseMsg.isSuccess()) {
                        Toast.makeText(getApplicationContext(), getSmsCodeResponseMsg.result_code_msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, getSmsCodeResponseMsg.result_code_msg);
                    } else {
                        String erroInfo = getSmsCodeResponseMsg.result_code_msg;
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

    private void onbtnSubmit() {
        if (textPhone.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "手机号为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (textCheckCode.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "验证码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (textPassword.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (textPasswordConfirm.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "确认密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!textPassword.getText().toString().equals(textPasswordConfirm.getText().toString())) {
            Toast.makeText(getApplicationContext(), "两次输入密码不一致",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final LoadingDialog loadingDialog = new LoadingDialog(ForgetPasswordActivity.this, R.style.MyDialog);
        loadingDialog.show();

        mPosApplication.getMsgBuilder().buildResetPasswordMsg(textCheckCode.getText().toString(), textPassword.getText().toString()).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingDialog.dismiss();
                try {
                    LinkeaResponseMsg.ResetPasswordResponseMsg resetPasswordResponseMsg =
                            LinkeaResponseMsgGenerator.generateResetPasswordResponseMsg(responseString);
                    if (resetPasswordResponseMsg.isSuccess()) {
                        String erroInfo = resetPasswordResponseMsg.result_code_msg;
                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, erroInfo);
                        finish();
                    } else {
                        String erroInfo = resetPasswordResponseMsg.result_code_msg;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetCheckCode:
                onbtnGetCheckCode();
                break;
            case R.id.btnSubmit:
                onbtnSubmit();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        MyActionbar.setRegisterActionBarLayout(this, R.drawable.login_title, "忘记密码");

        mPosApplication = (MPosApplication) getApplication();


        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        btnGetCheckCode = (Button) findViewById(R.id.btnGetCheckCode);
        btnGetCheckCode.setOnClickListener(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textPhone = (EditText) findViewById(R.id.textPhone);
        textPhone.setOnFocusChangeListener(textFocusChangeListener);

        textCheckCode = (EditText) findViewById(R.id.textCheckCode);
        textCheckCode.setOnFocusChangeListener(textFocusChangeListener);
        textPhone.setInputType(EditorInfo.TYPE_CLASS_PHONE);

        textPassword = (EditText) findViewById(R.id.textPassword);
        textPassword.setOnFocusChangeListener(textFocusChangeListener);

        textPasswordConfirm = (EditText) findViewById(R.id.textPasswordConfirm);
        textPasswordConfirm.setOnFocusChangeListener(textFocusChangeListener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.forget_password, menu);
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
        if (event.getDeviceId() ==3) return false;

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d("dispatchTouchEvent", ev.toString());
//        if (ev.getDeviceId() == 3) return false;
//        return super.dispatchTouchEvent(ev);
//    }
//
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        Log.d("dispatchKeyEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyEvent(event);
//    }
//
//    @Override
//    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
//        Log.d("dispatchKeyShortcutEvent", event.toString());
//        if(event.getDeviceId() == 3) return false;
//        return super.dispatchKeyShortcutEvent(event);
//    }



    MyPresentation myPresentation;
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
       myPresentation =  ((MPosApplication)getApplication()).showExternalAd(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        ((MPosApplication)getApplication()).destroyExternalAD(myPresentation);
    }
}
