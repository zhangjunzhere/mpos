package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.BankBranchChooseDialog;
import com.itertk.app.mpos.BankChooseDialog;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.MyPresentation;
import com.itertk.app.mpos.PictureDialog;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.config.DeviceInfo;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.locationhelper.Bank;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;
/*
* 绑定银行卡
* */
public class BindBankCardActivity extends Activity implements View.OnClickListener {
    static final String TAG = "BindBankCardActivity";
    public static String AUTH_BANK_UNAUDITED="1";
    public static String AUTH_BANK_AUTHENTICATION="2";
    public static String AUTH_BANK_OFF="3";
    public static String AUTH_BANK_SUCCESS="4";
    MPosApplication mPosApplication;
    TextView textFullname = null;

    RelativeLayout btnBank = null;
    Button btnSearch = null;
    RelativeLayout btnYHKPic = null;
    Button btnSubmit = null;

    EditText textSearch = null;
    EditText textCardNo = null;
    EditText textCardNoConfirm = null;
    EditText textCardUsername = null;
    EditText textKHH = null;

    TextView textBank;
    TextView textBankDescription;
    Bank lastBank=null;

    Bank bank = null;
    LinkeaResponseMsg.GetBankBranchResponseMsg.Branch branch;


    static final String bankConfig = "bankConfig";
    static final String keyYHK = "yhk";
    static final String keyCardno="cardno";
    static final String keyName="name";
    static final String keyKHH="khh";

    private void loadBankConfig() {
        Log.d(TAG, "loadBankConfig ");
        SharedPreferences sharedPre = getSharedPreferences(bankConfig, MODE_PRIVATE);

        textCardNo.setText(sharedPre.getString(keyCardno,""));
        textCardNoConfirm.setText(sharedPre.getString(keyCardno,""));
        textCardUsername.setText(sharedPre.getString(keyName,""));
        textKHH.setText(sharedPre.getString(keyKHH,""));

        if (sharedPre.getString(keyYHK, "").isEmpty()){
            unChooseYHK();
        }else{
            chooseYHK(Uri.parse(sharedPre.getString(keyYHK, "")));
        }

    }

    private void saveBankConfig(String key, String value) {
        Log.d(TAG, "saveBankConfig " + key + ":" + value);
        SharedPreferences sharedPre = getSharedPreferences(bankConfig, MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString(key, value);

        //提交
        editor.commit();
    }

    private void ButtonSwitch(float alpha,boolean enable){
        btnBank.setEnabled(enable);
        btnBank.setAlpha(alpha);
        btnSearch.setAlpha(alpha);
        btnSearch.setEnabled(enable);
        btnYHKPic.setAlpha(alpha);
        btnYHKPic.setEnabled(enable);
        textSearch.setEnabled(enable);
        textSearch.setAlpha(alpha);
        textCardNo.setEnabled(enable);
        textCardNo.setAlpha(alpha);
        textCardNoConfirm.setEnabled(enable);
        textCardNoConfirm.setAlpha(alpha);
        textCardUsername.setEnabled(enable);
        textCardUsername.setAlpha(alpha);
        textKHH.setEnabled(enable);
        textKHH.setAlpha(alpha);
    }

    private void clearBankConfig() {
        unChooseYHK();
        textCardNo.setText("");
        textCardNoConfirm.setText("");
        textKHH.setText("");
        textCardUsername.setText("");
    }

    private void choosePic(int requestCode) {
/*        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image*//*");
        //intent.putExtra("crop", "true");
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        //intent.putExtra("outputX", 80);
        //intent.putExtra("outputY", 80);
        //intent.putExtra("return-data", true);


        startActivityForResult(intent, requestCode);*/
        if(DeviceInfo.checkCameraHardware(this)) {
            PictureDialog pictureDialog = new PictureDialog(this, R.style.MyDialog, null, requestCode);
            pictureDialog.show();
        }else {
            Utils.launchCamera(this,requestCode,false);
        }
    }

    private void onbtnBank() {
        final BankChooseDialog bankChooseDialog = new BankChooseDialog(this, R.style.MyDialog);
        bankChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bank = ((BankChooseDialog) dialog).getBankSelected();

                if (bank == null) {
                    if(lastBank==null) {
                        textBank.setText("请选择银行");
                        textBankDescription.setVisibility(View.VISIBLE);
                        textKHH.setText(null);
                    }else{
                        textBank.setText(lastBank.getName());
                        textBankDescription.setVisibility(View.INVISIBLE);
                    }
                } else {
                    textBank.setText(bank.getName());
                    textBankDescription.setVisibility(View.INVISIBLE);
                    lastBank=bank;
                    textKHH.setText(null);
                }
            }
        });
        bankChooseDialog.show();
    }

    private void onbtnSearch() {
        if ((bank == null || bank.getName() == null || bank.getName().isEmpty())&&lastBank==null) {
            Toast.makeText(getApplicationContext(), "请选择银行",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textSearch.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "请输入分行或者支行名称，再重新检索",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(bank==null&&lastBank!=null)
            bank=lastBank;
        BankBranchChooseDialog bankBranchChooseDialog = new BankBranchChooseDialog(this, R.style.MyDialog, bank, textSearch.getText().toString().trim());
        bankBranchChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                branch = ((BankBranchChooseDialog) dialog).getSelectedBankBranch();
                if (branch == null) {

                } else {
                    textKHH.setText(branch.name);
                }
            }
        });

        bankBranchChooseDialog.show();
    }

    private void onbtnYHKPic() {
        choosePic(1);
    }

    public String bitmapToBase64(String uriPath) {
        String string = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uriPath));


            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return string;
    }

    String tempState;
    private void onbtnSubmit() {
        if(mPosApplication.getMember()==null) {
            Toast.makeText(this,"您为离线用户，不能提交审核",Toast.LENGTH_SHORT).show();
            return;
        }
        checkBankStatus(false);
        String status=mPosApplication.getMember().authenticate.bank_state;
        if(AUTH_BANK_SUCCESS.equals(status)){//审核成功
            submitBank();
        }else if(AUTH_BANK_AUTHENTICATION.equals(status)){//审核中
        }else if(AUTH_BANK_OFF.equals(status)){//审核失败
            if(AUTH_BANK_UNAUDITED.equals(tempState)){
                submitBank();
                return;
            }
            btnSubmit.setText("提交审核");
            btnSubmit.setBackgroundResource(R.drawable.my_blue_selector);
            tempState=AUTH_BANK_UNAUDITED;
            clearBankConfig();
        }
        else if(AUTH_BANK_UNAUDITED.equals(status)){//未审核
            submitBank();
        }
    }

    private void submitBank(){
        if (textCardNo.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "卡号不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textCardNoConfirm.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "确认卡号不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textCardUsername.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "银行卡持有人不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(!textCardNo.getText().toString().trim().equals(textCardNoConfirm.getText().toString().trim())){
            Toast.makeText(getApplicationContext(), "两次输入的银行卡号不一致,请检查后重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (textKHH.getText().toString().toString().trim().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), "开户行不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPre = getSharedPreferences(bankConfig, MODE_PRIVATE);

        String yhkPath = sharedPre.getString(keyYHK, "");
        if (yhkPath.isEmpty()) {
            Toast.makeText(getApplicationContext(), "银行卡照片不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        saveBankConfig(keyCardno, textCardNo.getText().toString().trim());
        saveBankConfig(keyName, textCardUsername.getText().toString().trim());
        saveBankConfig(keyKHH, textKHH.getText().toString().trim());


        String bankImageName = "bank.jpg";
        String bankImageData = bitmapToBase64(yhkPath);

        final LoadingDialog loadingDialog = new LoadingDialog(this, R.style.MyDialog);
        loadingDialog.show();

        MPosApplication mPosApplication = (MPosApplication)getApplication();

        mPosApplication.getMsgBuilder().buildBindBankcardMsg(mPosApplication.getMember().authenticate.status, mPosApplication.getMember().authenticate.bank_state,
                bankImageData, bankImageName, textKHH.getText().toString(), textCardUsername.getText().toString(), textCardNo.getText().toString()).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());

                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingDialog.dismiss();
                Log.d(TAG, responseString);

                try {
                    LinkeaResponseMsg.BindBankcardResponseMsg bindBankcardResponseMsg =
                            LinkeaResponseMsgGenerator.generateBindBankcardResponseMsg(responseString);
                    if (bindBankcardResponseMsg.success) {
                        btnSubmit.setText("资料审核中，请耐心等待");
                        btnSubmit.setBackgroundResource(R.drawable.btn_green);
                        btnSubmit.setEnabled(false);
                        ButtonSwitch(0.5f,false);
/*                        Intent it = new Intent(BindBankCardActivity.this, TradeHomeActivity.class);
                        startActivity(it);
                        finish();*/
                    } else {
                        String erroInfo = ""+bindBankcardResponseMsg.result_code_msg;
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

    private void unChooseYHK() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed);
        textView.setText("未上传");
        textView.setTextColor(getResources().getColor(R.color.solid_red));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage);
        imageView.setImageResource(R.drawable.item_image);
    }

    private void chooseYHK(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage);
        imageView.setImageURI(imgUri);

        saveBankConfig(keyYHK, imgUri.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=Activity.RESULT_OK){
            return;
        }

/*        if (data == null) {
            unChooseYHK();
            return;
        }

        chooseYHK(data.getData());*/

        Uri uri=Utils.getDataUri(data,requestCode);
        if(uri==null){
            unChooseYHK();
        }else
            chooseYHK(uri);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBank:
                onbtnBank();
                break;
            case R.id.btnSearch:
                onbtnSearch();
                break;
            case R.id.btnYHKPic:
                onbtnYHKPic();
                break;
            case R.id.btnSubmit:
                onbtnSubmit();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_card);
        MyActionbar.setLoginActionBarLayout(this, "绑定银行卡");

        mPosApplication = (MPosApplication) getApplication();

        textFullname = (TextView) findViewById(R.id.textFullname);

        btnBank = (RelativeLayout) findViewById(R.id.btnBank);
        btnBank.setOnClickListener(this);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        btnYHKPic = (RelativeLayout) findViewById(R.id.btnYHKPic);
        btnYHKPic.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();

        textSearch = (EditText) findViewById(R.id.textSearch);
        textSearch.setOnFocusChangeListener(textFocusChangeListener);

        textCardNo = (EditText) findViewById(R.id.textCardNo);
        textCardNo.setOnFocusChangeListener(textFocusChangeListener);

        textCardNoConfirm = (EditText) findViewById(R.id.textCardNoConfirm);
        textCardNoConfirm.setOnFocusChangeListener(textFocusChangeListener);

        textCardUsername = (EditText) findViewById(R.id.textCardUsername);
        textCardUsername.setOnFocusChangeListener(textFocusChangeListener);

        textKHH = (EditText) findViewById(R.id.textKHH);

        if(mPosApplication.getMember()!=null) {
            //textFullname.setText(mPosApplication.getMember().member.member_name);
            String format=getResources().getString(R.string.bank_user_title);
            textFullname.setText( String.format(format,mPosApplication.getMember().member.member_name));
        }

        textBank = (TextView) findViewById(R.id.textBank);
        textBankDescription = (TextView) findViewById(R.id.textBankDescription);



        loadBankConfig();

        checkBankStatus(true);
    }

    private void checkBankStatus(boolean getUploadInfo){
        if(mPosApplication.getMember()==null)
            return;
        ButtonSwitch(1.0f,true);
        btnSubmit.setEnabled(true);
        String status=mPosApplication.getMember().authenticate.bank_state;
        if(AUTH_BANK_SUCCESS.equals(status)){
        }else if(AUTH_BANK_AUTHENTICATION.equals(status)){
            btnSubmit.setText("资料审核中，请耐心等待");
            btnSubmit.setBackgroundResource(R.drawable.btn_green);
            btnSubmit.setEnabled(false);
            ButtonSwitch(0.5f,false);
            if(getUploadInfo){
                showUploadInfo();
            }
        }else if(AUTH_BANK_OFF.equals(status)){
            if(AUTH_BANK_UNAUDITED.equals(tempState)){
                return;
            }
            btnSubmit.setText("资料审核失败，请重新上传资料");
            btnSubmit.setBackgroundResource(R.drawable.my_red_selector);
        }
        else if(AUTH_BANK_UNAUDITED.equals(status)){
        }
    }

    private void showUploadInfo(){
        textCardNo.setText(mPosApplication.getMember().member.card_no);
        textCardNoConfirm.setText(mPosApplication.getMember().member.card_no);
        textCardUsername.setText(mPosApplication.getMember().member.card_holder);
        textKHH.setText((mPosApplication.getMember().member.bank_name));
        ImageView imageView = (ImageView) findViewById(R.id.ItemImage);
        ImageLoader.getInstance().displayImage(mPosApplication.getMember().authenticate.bank_image_url, imageView);
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed);
        textView.setText("已上传");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.bind_bank_card, menu);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getDeviceId() ==3) return false;

//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
//            return true;
//        }
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
        myPresentation = ((MPosApplication)getApplication()).showExternalAd(this);
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
