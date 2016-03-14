package com.itertk.app.mpos.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.ExternalPos;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyTTS;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.itertk.app.mpos.utility.BlueToothHelper;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/*
* 验证pos 序列号和激活时一致
* */

public class PosConDialog extends Dialog {
    private static String TAG = "PosConDialog";
    Activity context;
    TextView textTitle;

    ExternalPos pos;
    Button btnCancel;
    private OnClickButtonOKListener onClickButtonOKListener;
    private OnClickButtonCancelListener onClickButtonCancelListener;
    MPosApplication mPosApplication;



    public PosConDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;
        pos = ((MPosApplication) getContext().getApplicationContext()).getPos();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_bankcard);
        setCanceledOnTouchOutside(false);
        Utils.setPosSignDate(getContext(),Utils.getTime());
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText("设备连接中");
        mPosApplication = (MPosApplication) context.getApplication();


        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PosConDialog.this.dismiss();

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
                        try {
                            if (success) {
                                textTitle.setText("设备连接成功");//签到中
                                Log.i("PosSign", pos.getSn() + "   " + MPosApplication.getInstance().getPosId());
                                if (!pos.getSn().equals(MPosApplication.getInstance().getPosId())) {
                                    textTitle.setText("pos机序列号与当前终端绑定的序列号不同");
                                    btnCancel.setText("确定");
                                    btnCancel.setBackgroundResource(R.drawable.btn_red);
                                } else {
                                    dismiss();
                                    bindMember();
                                }
                            } else {
                                textTitle.setText("无法连接到pos机");
                                btnCancel.setText("确定");
                                btnCancel.setBackgroundResource(R.drawable.btn_red);
                                return;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            textTitle.setText("无法连接到pos机");
                            btnCancel.setText("确定");
                            btnCancel.setBackgroundResource(R.drawable.btn_red);
                        }
                    }
                });

            }
        });
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

    private void bindMember(){
        if(mPosApplication.getMember()!=null) {
            mPosApplication.getMsgBuilder().buildMemberBind(mPosApplication.getMember().member.member_id).send(new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, throwable.toString());
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG, responseString);
                    try {
                        LinkeaResponseMsg.ResponseMsg memberBindResonseMsg =
                                LinkeaResponseMsgGenerator.generateMemberBindResonseMsg(responseString);
                        if(memberBindResonseMsg.success){
                            if(mPosApplication.getMember()!=null)
                                saveMember(mPosApplication.getMember().member.member_id);
                            if( isMemberCertificated()){
                                if (isBankBind()) {
                                    context.startActivity(new Intent(context, TradeHomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                } else {
                                    context.startActivity(new Intent(context, BindBankCardActivity.class));
                                }
                            }
                            else{
                                context.startActivity(new Intent(context, CertificateActivity.class));
                            }
                            context.finish();
                        }else {
                            Toast.makeText(context, memberBindResonseMsg.result_code_msg, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, memberBindResonseMsg.result_code_msg);
                        }
                    }catch (Exception e){
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                }
            });

        }
    }


    private boolean isMemberCertificated() {
        if(mPosApplication.getMember()==null){
            return  true;
        }
        String status = mPosApplication.getMember().authenticate.status;

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

        if (BindBankCardActivity.AUTH_BANK_SUCCESS.equals(bankState))
            return true;
        else
            return false;
    }

    private void saveMember(String member) {
        String memberConfig = "memberconfig";
        String keyMember = "member";

        SharedPreferences sharedPre = context.getSharedPreferences(memberConfig, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPre.edit();

        editor.putString(getTeminalId()+"_"+keyMember, member);

        editor.commit();
    }

    private String getTeminalId() {
        String terminalConfig = "terminalconfig";
        String keyTerminalId = "terminalId";
        SharedPreferences sharedPre = context.getSharedPreferences(terminalConfig, context.MODE_PRIVATE);
        return sharedPre.getString(keyTerminalId, "");
    }


}
