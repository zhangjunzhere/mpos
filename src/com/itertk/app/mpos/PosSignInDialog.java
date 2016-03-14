package com.itertk.app.mpos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.utility.BlueToothHelper;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/*
* 签到成功
* */

public class PosSignInDialog extends Dialog {
    private static String TAG = "PosDialog";
    Context context;
    TextView textTitle;

    ExternalPos pos;
    Button btnCancel;
    private OnClickButtonOKListener onClickButtonOKListener;
    private OnClickButtonCancelListener onClickButtonCancelListener;

    private MyTTS tts;


    public PosSignInDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        pos = ((MPosApplication) getContext().getApplicationContext()).getPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }


    private void saveTrackKey(String trackKey) {
        Log.d(TAG, "saveTrackKey");
        SharedPreferences sharedPre = context.getSharedPreferences("key", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString("track", trackKey);
        //提交
        editor.commit();
    }

    private void saveAllKey(String trackKey, String macKey, String pinKey) {
        Log.d(TAG, "saveTrackKey");
        SharedPreferences sharedPre = context.getSharedPreferences("key", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString("track", trackKey);
        editor.putString("mac", macKey);
        editor.putString("pin", pinKey);
        //提交
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_bankcard);
        setCanceledOnTouchOutside(false);
        Utils.setPosSignDate(getContext(),Utils.getTime());
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText("设备连接中");//签到中


        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pos.cancelOperation();
//                if (!BlueToothHelper.isME30Connected()) {
//                    getContext().startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
//                }
                PosSignInDialog.this.dismiss();

                if (onClickButtonCancelListener != null)
                    onClickButtonCancelListener.onClickButtonCancel();
            }
        });

        Log.d(TAG, "onCreate");


        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "onShow");
                if (!checkBluetoothState()) return;

                pos.connectDevice(new ExternalPos.ConnectDeviceListener() {
                    @Override
                    public void onConnectDeviceListener(boolean success) {
                        if (success) {
                            textTitle.setText("签到中");//签到中
                         //   Log.i("PosSign", pos.getSn() + "   " + MPosApplication.getInstance().getPosId());
                            String sn ="";
                            try
                            {
                                sn = pos.getSn();
                            }catch (Exception e)
                            {
                                textTitle.setText("无法连接到pos机");
                                btnCancel.setText("确定");
                                btnCancel.setBackgroundResource(R.drawable.btn_red);
                                return;
                            }
                            if (!sn.equals(MPosApplication.getInstance().getPosId())) {
                                textTitle.setText("pos机序列号与当前终端绑定的序列号不同");
                                btnCancel.setText("确定");
                                btnCancel.setBackgroundResource(R.drawable.btn_red);
                            } else {
                                signServer();
                            }
                        }
                        else
                        {
                            textTitle.setText("无法连接到pos机");
                            btnCancel.setText("确定");
                            btnCancel.setBackgroundResource(R.drawable.btn_red);
                            return;
                        }
                    }
                });

            }
        });
    }
    private  void signServer()
    {

        MPosApplication.getInstance().getMsgBuilder().buildSignInMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String
                    responseString, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                textTitle.setText("签到失败");
                btnCancel.setText("确定");
                btnCancel.setBackgroundResource(R.drawable.btn_red);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String
                    responseString) {
                Log.d("pos sign in", responseString);

                try {
                    final LinkeaResponseMsg.SignInResponseMsg signInResponseMsg =
                            LinkeaResponseMsgGenerator.generateSignInResponseMsg(responseString);
                    if (signInResponseMsg.isSuccess()) {

                        ((MPosApplication) (context.getApplicationContext())).getPos().updateWorkingKey(signInResponseMsg.tmkMacKey, signInResponseMsg.macCheckValue,
                                signInResponseMsg.tmkPinKey, signInResponseMsg.pinCheckValue, signInResponseMsg.tmkTakKey, signInResponseMsg.trackCheckValue, new ExternalPos.UpdateWorkingKeyListener() {

                                    @Override
                                    public void onUpdateWorkingKeyListener(boolean success) {
                                        if (success) {
                                            ((MPosApplication) (context.getApplicationContext())).setTrackKey(signInResponseMsg.tmkTakKey);
                                            ((MPosApplication) (context.getApplicationContext())).setMacKey(signInResponseMsg.tmkMacKey);
                                            ((MPosApplication) (context.getApplicationContext())).setPinKey(signInResponseMsg.tmkPinKey);
                                            saveTrackKey(signInResponseMsg.tmkTakKey);
                                            saveAllKey(signInResponseMsg.tmkTakKey, signInResponseMsg.tmkMacKey, signInResponseMsg.tmkPinKey);
                                            Log.d(TAG, "update working key success!");
                                            textTitle.setText("签到成功!");
                                            btnCancel.setText("确定");
                                            btnCancel.setBackgroundResource(R.drawable.btn_blue);
                                        } else {
                                            Log.d(TAG, "update working key failed!!!!!!!!!!!!!!!!!!!!");
                                            textTitle.setText("签到失败!");
                                            btnCancel.setText("确定");
                                            btnCancel.setBackgroundResource(R.drawable.btn_red);
                                        }
                                    }
                                });

                    } else {
                        String erroInfo = signInResponseMsg.result_code_msg;

                        Log.e(TAG, erroInfo);
                        textTitle.setText("签到失败");
                        btnCancel.setText("确定");
                        btnCancel.setBackgroundResource(R.drawable.btn_red);
                    }
                } catch (Exception e) {

                    Log.d(TAG, e.toString());

                    textTitle.setText("签到失败");
                    btnCancel.setText("确定");
                    btnCancel.setBackgroundResource(R.drawable.btn_red);
                }
            }
        } );
    }
    private boolean checkBluetoothState() {
        if (!BlueToothHelper.blueToothEnable()) {
            BlueToothHelper.enableBlueTooth();
        }
        if (!BlueToothHelper.isME30Connected()) {
            textTitle.setText("请确认Pos机是否连接");
            btnCancel.setText("确定");
            btnCancel.setBackgroundResource(R.drawable.btn_red);
            return false;
        }
//        Log.i("PosSign", pos.getSn() + "   " + MPosApplication.getInstance().getPosId());
//        if(pos.isDeviceConnected())
//        {
//            Log.i("PosSign", pos.getSn()+"   "+MPosApplication.getInstance().getPosId());
//            if(!pos.getSn().equals(MPosApplication.getInstance().getPosId()))
//            {
//                textTitle.setText("Pos机序列号与当前终端绑定的序列号不同");
//                btnCancel.setText("确定");
//                btnCancel.setBackgroundResource(R.drawable.btn_red);
//                return false;
//            }
//        }
        return true;
    }


    public void setOnClickButtonOKListener(OnClickButtonOKListener onClickButtonOKListener) {
        this.onClickButtonOKListener = onClickButtonOKListener;
    }

    public void setOnClickButtonCancelListener(OnClickButtonCancelListener onClickButtonCancelListener) {
        this.onClickButtonCancelListener = onClickButtonCancelListener;
    }

    public interface OnClickButtonOKListener {
        public void onClickButtonOK();
    }

    public interface OnClickButtonCancelListener {
        public void onClickButtonCancel();
    }


}
