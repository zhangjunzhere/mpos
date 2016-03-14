package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.MyPresentation;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
/*
* 用户注册
* */
public class RegisterActivity extends Activity implements View.OnClickListener {
    static final String TAG = "RegisterActivity";
    EditText textPhone = null;
    EditText textPassword = null;
    EditText textPasswordConfirm = null;
    EditText textFullname = null;
    EditText textShopName = null;
    EditText textShopAddr = null;
    EditText textIDCard = null;
    LinearLayout mShopType;
    TextView type_show;
    int type_id=-1;

    Button btnRegister = null;

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&& !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private void onbtnRegister() {
        Log.d(TAG, "onbtnRegister");
        if (textPhone.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "手机号为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Utils.phoneRegex(textPhone.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "手机号格式不正确,请验证后重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textPassword.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textPasswordConfirm.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "确认密码为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textFullname.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "姓名为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textIDCard.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "身份证为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textShopName.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "店铺名称不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textShopAddr.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "店铺地址不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!textPassword.getText().toString().trim().equals(textPasswordConfirm.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "两次输入密码不一样",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textPassword.getText().toString().trim().length() <6) {
            Toast.makeText(getApplicationContext(), "密码至少为6位",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (textPassword.getText().toString().trim().length() >16) {
            Toast.makeText(getApplicationContext(), "密码长度不能超过16位",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String errInfo="";
        try {
            errInfo= Utils.IDCardValidate(textIDCard.getText().toString().trim());
        }catch (Exception e){
            Log.i(TAG,e.toString());
            Toast.makeText(getApplicationContext(), "身份证号格式不正确,请检查后重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!errInfo.isEmpty()) {
            Toast.makeText(getApplicationContext(), errInfo,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(type_id==-1){
            Toast.makeText(getApplicationContext(), "请选择经营类型",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        final LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this, R.style.MyDialog);
        loadingDialog.show();

        if(getLocalIpAddress()==null){
            Toast.makeText(getApplicationContext(), "无法取得IP地址，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        ((MPosApplication) getApplication()).getMsgBuilder().buildRegisterMsg(textPhone.getText().toString().trim(),textPassword.getText().toString().trim(),
                textFullname.getText().toString().trim(), textShopAddr.getText().toString().trim(),textShopName.getText().toString().trim(),
                String.valueOf(type_id),getLocalIpAddress(),textIDCard.getText().toString().trim()).send(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                loadingDialog.dismiss();
                String response = new String(responseBody);
                Log.d("response", response);
                Gson gson = new Gson();

                try {
                    LinkeaResponseMsg.RegisterResponseMsg registerResponseMsg = gson.fromJson(response, LinkeaResponseMsg.class).registerResponseMsg;

                    if (registerResponseMsg.isSuccess()) {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this, MemberActivateActivity.class);
                        intent.putExtra("member_id", textPhone.getText().toString().trim());
                        Bundle bundle=new Bundle();
                        bundle.putString("pone",textPhone.getText().toString().trim());
                        bundle.putString("pwd",textPassword.getText().toString().trim());
                        bundle.putString("username",textFullname.getText().toString().trim());
                        bundle.putString("cardid",textIDCard.getText().toString().trim());
                        bundle.putString("shopaddr",textShopAddr.getText().toString().trim());
                        bundle.putString("shopname",textShopName.getText().toString().trim());
                        bundle.putString("shoptype",String.valueOf(type_id));
                        bundle.putString("ip",getLocalIpAddress());
                        intent.putExtra("reinfo",bundle);

                        startActivity(intent);

                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), registerResponseMsg.result_code_msg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, registerResponseMsg.result_code_msg);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loadingDialog.dismiss();
                Log.e(TAG, error.toString());
                Toast.makeText(getApplicationContext(), "通信失败", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                onbtnRegister();
                break;
            case R.id.btnshopType:
                onbtnGetType();
                break;
        }
    }

    private void  onbtnGetType(){
        ShopTypeDialog shopTypeDialog=new ShopTypeDialog(this,R.style.MyDialog);
        shopTypeDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        MyActionbar.setRegisterActionBarLayout(this, R.drawable.login_title, "商户注册");


        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textPhone = (EditText) findViewById(R.id.textPhone);
        textPhone.setOnFocusChangeListener(textFocusChangeListener);
        textPhone.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        textPassword = (EditText) findViewById(R.id.textPassword);
        textPassword.setOnFocusChangeListener(textFocusChangeListener);

        textPasswordConfirm = (EditText) findViewById(R.id.textPasswordConfirm);
        textPasswordConfirm.setOnFocusChangeListener(textFocusChangeListener);

        textFullname = (EditText) findViewById(R.id.textFullname);
        textFullname.setOnFocusChangeListener(textFocusChangeListener);

        textIDCard=(EditText)findViewById(R.id.textIDCard);
        textIDCard.setOnFocusChangeListener(textFocusChangeListener);

        textShopName = (EditText) findViewById(R.id.textShop_name);
        textShopName.setOnFocusChangeListener(textFocusChangeListener);
        textShopName.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        textShopAddr = (EditText) findViewById(R.id.textShop_addr);
        textShopAddr.setOnFocusChangeListener(textFocusChangeListener);
        textShopAddr.setInputType(EditorInfo.TYPE_CLASS_TEXT);

        mShopType=(LinearLayout)findViewById(R.id.btnshopType);
        mShopType.setOnClickListener(this);
        type_show=(TextView)findViewById(R.id.textShop_type);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        IntentFilter filter=new IntentFilter("com.iteritk.app.mpos.send_type");
        this.registerReceiver(myRegisterReceiver,filter);

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.register, menu);
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
        Log.d("onKeyDown", event.toString());
        if (event.getDeviceId() ==3) return false;

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
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

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        this.unregisterReceiver(myRegisterReceiver);
    }

    BroadcastReceiver myRegisterReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.iteritk.app.mpos.send_type")){
                type_id=intent.getIntExtra("TYPE_ID",-1);
                String type_name=intent.getStringExtra("TYPE_NAME");
                type_show.setText(type_name);
            }
        }
    };
}
