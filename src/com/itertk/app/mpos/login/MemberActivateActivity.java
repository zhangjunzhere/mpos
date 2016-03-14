package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.dbhelper.UserDao;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.itertk.app.mpos.utility.AESEncryptor;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Date;
/*
* 商户激活
* */
public class MemberActivateActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MemberActivateActivity";
    Button btnGetCheckCode = null;
    Button btnActivate = null;
    TextView textPhone = null;
    EditText textCheckCode = null;
    MPosApplication mPosApplication;
    String member_id;
    LoadingDialog loadingDialog ;

    String phone,pwd,username,shopaddr,shopname,shoptype,localip,cardID;

    void onbtnGetCheckCode() {

        ((MPosApplication) getApplication()).getMsgBuilder().buildRegisterMsg(member_id,pwd,
                username, shopaddr,shopname,
                shoptype,localip,cardID).send(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("response", response);
                Gson gson = new Gson();

                try {
                    LinkeaResponseMsg.RegisterResponseMsg registerResponseMsg = gson.fromJson(response, LinkeaResponseMsg.class).registerResponseMsg;

                    if (registerResponseMsg.isSuccess()) {
                        Toast.makeText(getApplicationContext(), "获取验证码成功", Toast.LENGTH_SHORT).show();
                        MyCount count=new MyCount(60*1000,1000);
                        count.start();
                        btnGetCheckCode.setAlpha(0.5f);
                        btnGetCheckCode.setEnabled(false);
                    } else {
                        Toast.makeText(getApplicationContext(), "获取验证码失败，请重新获取", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, registerResponseMsg.result_code_msg);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, error.toString());
                Toast.makeText(getApplicationContext(), "通信失败", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    void onbtnActivate() {
        final String checkCode = textCheckCode.getText().toString();
        if (checkCode.length() == 0) {
            Toast.makeText(getApplicationContext(), "验证码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        mPosApplication.getMsgBuilder().buildUserActivateMsg(member_id, checkCode).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "网络连接失败",
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    LinkeaResponseMsg.UserActivateResponseMsg userActivateResponseMsg =
                            LinkeaResponseMsgGenerator.generateUserActivateResponseMsg(responseString);
                    if (userActivateResponseMsg.isSuccess()) {
                        Toast.makeText(getApplicationContext(), userActivateResponseMsg.result_code_msg, Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(MemberActivateActivity.this, LoginActivity.class));
                        login(member_id,pwd);
                        //finish();
                    } else {

                        String erroInfo = userActivateResponseMsg.result_code_msg;
                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, erroInfo);

                        if (userActivateResponseMsg.result_code.equals("904")) {
                            startActivity(new Intent(MemberActivateActivity.this, LoginActivity.class));
                            finish();
                        }
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
            case R.id.btnActivate:
                onbtnActivate();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_activate);
        MyActionbar.setLoginActionBarLayout(this, "会员激活");

        mPosApplication = (MPosApplication) getApplication();

        loadingDialog = new LoadingDialog(this, R.style.MyDialog);


        member_id = getIntent().getStringExtra("member_id");

        Bundle bundle=getIntent().getBundleExtra("reinfo");
        phone=bundle.getString("phone");
        pwd=bundle.getString("pwd");
        username=bundle.getString("username");
        shopaddr=bundle.getString("shopaddr");
        shopname=bundle.getString("shopname");
        shoptype=bundle.getString("shoptype");
        localip=bundle.getString("ip");
        cardID=bundle.getString("cardid");

        textPhone = (TextView) findViewById(R.id.textPhone);

        textPhone.setText(member_id);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textCheckCode = (EditText) findViewById(R.id.textCheckCode);
        textCheckCode.setOnFocusChangeListener(textFocusChangeListener);


       btnGetCheckCode = (Button) findViewById(R.id.btnGetCheckCode);
       btnGetCheckCode.setOnClickListener(this);

        btnActivate = (Button) findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(this);
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.member_activate, menu);
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
    }*/

    public class MyCount extends CountDownTimer{
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnGetCheckCode.setEnabled(true);
            btnGetCheckCode.setAlpha(1.0f);
            btnGetCheckCode.setText("获取验证码");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnGetCheckCode.setText(millisUntilFinished/1000+"秒后再获取");
        }
    }

    private void login(final String userName,final String password){
        loadingDialog.show();
        mPosApplication.getMsgBuilder().buildLoginMsg(userName, password).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
                localVerify(userName);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                Boolean success = false;
                try {
                    LinkeaResponseMsg.LoginResponseMsg loginResponseMsg =
                            LinkeaResponseMsgGenerator.generateLoginResponseMsg(responseString);

                    success = loginResponseMsg.isSuccess();
                    if (success) {
                        //login success
                        mPosApplication.getMsgBuilder().setAccessToken(loginResponseMsg.token.access_token);
                        mPosApplication.getMsgBuilder().setMember_id(userName);
                        mPosApplication.setOnlineState(true);

                        mPosApplication.setMember(loginResponseMsg);
                        checkAuthProgress();
                        //save UserLoginId
                        User user =  new User();
                        user.setClerkNo(Long.parseLong(loginResponseMsg.member.member_id));
                        user.setClerkName(userName);
                        user.setLoginPwd(AESEncryptor.getInstance(getBaseContext()).encryption(password));
                        user.setPhone(loginResponseMsg.member.phone);
                        user.setType(0L); //administrator
                        user.setRemember_password(false);
                        user.setLast_login(new Date());

                        UserDao userDao =  MPosApplication.getInstance().getDataHelper().getUserManager();
                        userDao.insertOrReplace(user);
                        mPosApplication.setCurUser(user);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        if(!prefs.getBoolean("LOGIN",false)) {
                            Utils.saveInfo(getBaseContext(), "LOGIN", true);
                        }
                    } else {
                        String erroInfo = loginResponseMsg.result_code_msg;
                        //Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, erroInfo);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                    startActivity(new Intent(MemberActivateActivity.this, LoginActivity.class));
                    finish();
                }

                if(success){
                    loadingDialog.dismiss();
                    checkAuthProgress();
                    finish();
                }else{
                    localVerify(userName);
                }
            }
        });
    }
    private void localVerify(String userName){
        loadingDialog.dismiss();

        DataHelper dataHelper = mPosApplication.getDataHelper();
        UserDao userDao = dataHelper.getUserManager();
        User user = mPosApplication.getDataHelper().userLogin(getBaseContext(),member_id, pwd);
        if (user != null) {
            Log.d(TAG, "登录成功 " + user.getClerkName());
            user.setRemember_password(false);
            user.setLast_login(new Date());
            userDao.update(user);
            mPosApplication.setCurUser(user);

            if(user.getType() != DataHelper.USER_TPYE_ADMIN){
                LinkeaRequest.memberLogin(getApplicationContext(), null);
            }
            checkAuthProgress();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "登录失败，用户名或者密码错误", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "登录失败，用户名或者密码错误");
            startActivity(new Intent(MemberActivateActivity.this, LoginActivity.class));
            finish();
        }
    }
    private void checkAuthProgress() {
        if (isMemberBind()) {
            if (isMemberCertificated()) {
                if (isBankBind()) {
                    startActivity(new Intent(MemberActivateActivity.this, TradeHomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    startActivity(new Intent(MemberActivateActivity.this, BindBankCardActivity.class));
                }
            } else {
                startActivity(new Intent(MemberActivateActivity.this, CertificateActivity.class));
            }
        } else {
            startActivity(new Intent(MemberActivateActivity.this, BindMemberActivity.class));
        }

    }

    private boolean isMemberBind() {
        SharedPreferences sharedPre = getSharedPreferences(LoginActivity.memberConfig, MODE_PRIVATE);
        String member = sharedPre.getString(LoginActivity.keyMember, "");

        if (member.isEmpty())
            return false;
        else {
            return true;
        }
    }

    private boolean isMemberCertificated() {
        if(mPosApplication.getMember()==null){
            return  true;
        }
        String status = mPosApplication.getMember().authenticate.status;
        Log.d(TAG, "status=" + status + mPosApplication.getMember().authenticate.status_msg);

        if (status.equals("4"))
            return true;
        else
            return false;
    }

    private boolean isBankBind() {
        if(mPosApplication.getMember()==null){
            return  true;
        }
        String bankState = mPosApplication.getMember().authenticate.bank_state;
        Log.d(TAG, "bankstate" + bankState + mPosApplication.getMember().authenticate.bank_state_msg);

        if (BindBankCardActivity.AUTH_BANK_SUCCESS.equals(bankState)) {
            Utils.saveStringInfo(this,"BANKBIND_"+mPosApplication.getMember().member.member_id,mPosApplication.getMember().member.member_id);
            return true;
        }
        else if(mPosApplication.getMember().member.member_id.equals(Utils.getStringInfo(this,"BANKBIND_"+mPosApplication.getMember().member.member_id))){
            return  true;
        }else
            return false;
    }

}
