package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.utility.AESEncryptor;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.MyPresentation;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.utility.LinkeaRequest;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.dbhelper.UserDao;
import com.itertk.app.mpos.service.PreloadInfoService;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.Date;
import java.util.List;

/*
* 登录
* */

public class LoginActivity extends Activity implements View.OnClickListener{
    private static final String keyCode = "5013ED48-4360-48f5-BB85-3E67711D46E2";
    private static final String TAG = "LoginActivity";
    private static final String userConfig = "userconfig";
    private static final String keyUserName = "username";
    private static final String keyPassword = "password";
    private static final String keyRememberPassword = "remember";
    public static final String terminalConfig = "terminalconfig";
    public static final String keyTerminalId = "terminalId";
    public static final String keyTerminalMac = "terminalMac";
    public static final String keyPosId = "posid";
    public static final String memberConfig = "memberconfig";
    public static final String keyMember = "member";
    public static final String KeyGPSFlag = "GPSFlag";
    Button btnRegister = null;
    Button btnLogin = null;
    Button btnForgetPassword = null;
    EditText textUserName = null;
    EditText textPassword = null;
    CheckBox checkRememberPassword = null;
    MPosApplication mPosApplication;

    LoadingDialog loadingDialog ;

    //smile
    private ImageView mQRcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyActionbar.setLoginActionBarLayout(this, "用户登录");
        loadingDialog = new LoadingDialog(this, R.style.MyDialog);
        mPosApplication = (MPosApplication) getApplication();


        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        btnForgetPassword = (Button) findViewById(R.id.btnForgetPassword);
        btnForgetPassword.setOnClickListener(this);
        btnForgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textUserName = (EditText) findViewById(R.id.textUserName);
        textUserName.setOnFocusChangeListener(textFocusChangeListener);

        textPassword = (EditText) findViewById(R.id.textPassword);
        textPassword.setOnFocusChangeListener(textFocusChangeListener);

        checkRememberPassword = (CheckBox) findViewById(R.id.checkRememberPassword);

        loadLoginInfo();

//       try {
//            Boolean remember_password = false;
//            Date loginDate = new Date();
//            User user = new User(null, "user", AESEncryptor.getInstance(getBaseContext()).encryption("123456"), "13588886666", 2L,null,remember_password,loginDate,null);
//            mPosApplication.getDataHelper().getUserManager().insert(user);
//        } catch (Exception e) {
//
//        }

        if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO){
            InputManager inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
            Toast.makeText(this, "检测到扫描枪，请关闭物理键盘", Toast.LENGTH_LONG).show();
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showInputMethodPicker();
        }


//        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
//        Display[] presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
//
//        Log.d(TAG, "" + presentationDisplays.length);
//
//        if (presentationDisplays.length > 0) {
//            // If there is more than one suitable presentation display, then we could consider
//            // giving the user a choice.  For this example, we simply choose the first display
//            // which is the one the system recommends as the preferred presentation display.
//            Display display = presentationDisplays[0];
//            Presentation presentation = new MyPresentation(this, display);
//            presentation.show();
//        }


        //smile_gao add for qrcode
        mQRcode = (ImageView) findViewById(R.id.qrcode);
        Bitmap bmp = QRCodeHelper.generateQRCode("hahahlsladf");
        mQRcode.setImageBitmap(bmp);


        //preload base information
        Intent preloadService = new Intent(this, PreloadInfoService.class);
        preloadService.setAction(PreloadInfoService.ACTION_PRELOAD_INFO);
        startService(preloadService);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.login, menu);
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
        myPresentation = ((MPosApplication)getApplication()).showExternalAd(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if(prefs.getBoolean("LOGIN",false)) {
            RelativeLayout barCodeArea=(RelativeLayout)findViewById(R.id.barCode_area);
            barCodeArea.setVisibility(View.GONE);
            btnRegister.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.BELOW,R.id.checkRememberPassword);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT,R.id.relativeLayout2);
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT,R.id.relativeLayout2);
            layoutParams.topMargin=50;
            btnLogin.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_RIGHT,R.id.relativeLayout2);
            lp.addRule(RelativeLayout.ALIGN_BASELINE,R.id.checkRememberPassword);
            btnForgetPassword.setLayoutParams(lp);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        ((MPosApplication)getApplication()).destroyExternalAD(myPresentation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void saveLoginInfo() {
        Log.d(TAG, "saveLoginInfo");
        SharedPreferences sharedPre = getSharedPreferences(userConfig, MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString(keyUserName, textUserName.getText().toString());
        editor.putString(keyPassword, textPassword.getText().toString());
        editor.putBoolean(keyRememberPassword, checkRememberPassword.isChecked());
        //提交
        editor.commit();
    }

    private void loadLoginInfo() {
        Log.d(TAG, "loadLoginInfo");
//        SharedPreferences sharedPre = getSharedPreferences(userConfig, MODE_PRIVATE);
//        textUserName.setText(sharedPre.getString(keyUserName, ""));
//        textPassword.setText(sharedPre.getString(keyPassword, ""));
//        checkRememberPassword.setChecked(sharedPre.getBoolean(keyRememberPassword, false));
        UserDao userDao =  MPosApplication.getInstance().getDataHelper().getUserManager();
        List<User> users = userDao.queryBuilder().where(UserDao.Properties.Remember_password.eq(1)).orderDesc(UserDao.Properties.Last_login).list();

        if(users != null && users .size() > 0){
            User user = users.get(0);
            textUserName.setText(user.getClerkName());
            textPassword.setText(AESEncryptor.getInstance(getBaseContext()).decryption(user.getLoginPwd()));
            checkRememberPassword.setChecked(true);
        }
    }

    private void clearLoginInfo() {
        Log.d(TAG, "clearLoginInfo");
        SharedPreferences sharedPre = getSharedPreferences(userConfig, MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.remove(keyUserName);
        editor.remove(keyPassword);
        editor.remove(keyRememberPassword);
        //提交
        editor.commit();
    }

    private boolean isTerminalActivated() {
        SharedPreferences sharedPre = getSharedPreferences(terminalConfig, MODE_PRIVATE);
        String terminalId = sharedPre.getString(keyTerminalId, "");
        String terminalMac = sharedPre.getString(keyTerminalMac, "");

        if (terminalId.isEmpty() || terminalMac.isEmpty())
            return false;
        else {
            mPosApplication.getMsgBuilder().setTerm_id(terminalId);
            mPosApplication.getMsgBuilder().setTerm_mac(terminalMac);
            return true;
        }
    }

    private boolean isMemberBind() {
        SharedPreferences sharedPre = getSharedPreferences(memberConfig, MODE_PRIVATE);
        String member = sharedPre.getString(getTeminalId()+"_"+keyMember, "");

        if(mPosApplication.getMember()==null){
            return  true;
        }
        if(member.equals(mPosApplication.getMember().member.member_id)){
            return true;
        }else{
            return false;
        }

/*        if (member.isEmpty())
            return false;
        else {
            return true;
        }*/
    }
    private String getTeminalId() {
        String terminalConfig = "terminalconfig";
        String keyTerminalId = "terminalId";
        SharedPreferences sharedPre = getSharedPreferences(terminalConfig, MODE_PRIVATE);
        return sharedPre.getString(keyTerminalId, "");
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

    private void checkAuthProgress() {
//        startActivity(new Intent(LoginActivity.this, TradeHomeActivity.class));
       // if (isTerminalActivated()) {
            if (isMemberBind()) {
                //startActivity(new Intent(LoginActivity.this, TradeHomeActivity.class));
                if (isMemberCertificated()) {
                    if (isBankBind()) {
                        startActivity(new Intent(LoginActivity.this, TradeHomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    } else {
                        startActivity(new Intent(LoginActivity.this, BindBankCardActivity.class));
                    }
                } else {
                    startActivity(new Intent(LoginActivity.this, CertificateActivity.class));
                }
            } else {
                startActivity(new Intent(LoginActivity.this, BindMemberActivity.class));
            }
       /* } else {
            startActivity(new Intent(LoginActivity.this, TerminalActivateActivity.class));
        }*/
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnForgetPassword:
                onbtnForgetPassword();
                break;
            case R.id.btnRegister:
                onbtnRegister();
                break;
            case R.id.btnLogin:
                onbtnLogin();
                break;
        }
    }

    private void onbtnForgetPassword() {
        Log.d(TAG, "onbtnForgetPassword");

        if (!mPosApplication.isOnline()) {
            Toast.makeText(getApplicationContext(), "离线状态下，无法找回密码",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void onbtnRegister() {
        Log.d(TAG, "onbtnRegister");

        if (!mPosApplication.isOnline()) {
            Toast.makeText(getApplicationContext(), "离线状态下，无法注册",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(it);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    private void onbtnLogin() {
        Log.d(TAG, "onbtnLogin");

        final String userName = textUserName.getText().toString();
        final String password = textPassword.getText().toString();

        if (textUserName.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "用户名不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textPassword.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "密码不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkRememberPassword.isChecked()) {
            saveLoginInfo();
        }

        loadingDialog.show();
        login(userName, password);
    }

    private void login(final String userName,final String password){
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
                        User user;
                        UserDao userDao =  MPosApplication.getInstance().getDataHelper().getUserManager();
                        List<User> users = userDao.queryBuilder().where(UserDao.Properties.Type.eq(DataHelper.USER_TPYE_MEMBER)).list();
                        if(users.size() > 0){
                            user = users.get(0);
                        }else {
                            user = new User();
                        }
                        user.setClerkNo(Long.parseLong(loginResponseMsg.member.member_id));
                        user.setClerkName(userName);
                        user.setLoginPwd(AESEncryptor.getInstance(getBaseContext()).encryption(password));
                        user.setPhone(loginResponseMsg.member.phone);
                        user.setType(DataHelper.USER_TPYE_MEMBER); //administrator
                        user.setRemember_password(checkRememberPassword.isChecked());
                        user.setLast_login(new Date());
                        userDao.insertOrReplace(user);
                        mPosApplication.setCurUser(user);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        if(!prefs.getBoolean("LOGIN",false)) {
                            Utils.saveInfo(getBaseContext(), "LOGIN", true);
                        }

                        Intent preloadService = new Intent(LoginActivity.this, PreloadInfoService.class);
                        preloadService.setAction(PreloadInfoService.ACTION_PRELOAD_MEMBER);
                        startService(preloadService);

                    } else {
                        String erroInfo = loginResponseMsg.result_code_msg;
                        //Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, erroInfo);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
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
        User user = mPosApplication.getDataHelper().userLogin(getBaseContext(),textUserName.getText().toString(), textPassword.getText().toString());
        if (user != null) {
            Toast.makeText(getApplicationContext(), "离线登录成功", Toast.LENGTH_SHORT).show();
            user.setRemember_password(checkRememberPassword.isChecked());
            user.setLast_login(new Date());
            userDao.update(user);
            mPosApplication.setCurUser(user);

            if(user.getType() != DataHelper.USER_TPYE_ADMIN){
                LinkeaRequest.memberLogin(getApplicationContext(),null);
            }
            checkAuthProgress();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "登录失败，用户名或者密码错误", Toast.LENGTH_SHORT).show();
        }
    }

}
