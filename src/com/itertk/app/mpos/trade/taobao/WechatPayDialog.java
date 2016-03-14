package com.itertk.app.mpos.trade.taobao;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyTTS;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.trade.pos.DecimalHelper;
import com.itertk.app.mpos.utility.ShopOrderOperation;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by smile_gao on 2015/3/27.
 * 订货订单微信支付dialog
 */
public class WechatPayDialog extends Dialog {
    private static String TAG = "WeixinPayDialog";
    Context context;
    private MyTTS tts;
    EditText tvscancode ;
    TextView totalPrice;
    Button mBtncheckout;
    String scanCode ="";

    EditText textInputGun;
    String saveCode="";
    private Boolean mPaySuccess = false;
    private String mTradeNo;
    private String mTotalPrice;
    private String mTradeDetail;

    public WechatPayDialog(Context context, int theme, String  tradeNo, String totalPrice, String tradeDetail) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        mTradeNo = tradeNo;
        mTotalPrice = totalPrice;
        mTradeDetail = tradeDetail;
        saveCode = "";
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }
    public void setTitle(String title) {
        totalPrice.setText(title);
    }
    public Boolean getPayResult(){return mPaySuccess;}

    void initScanDevice()
    {
        scanCode = new String();
        textInputGun = (EditText) findViewById(R.id.textInputGun);
        textInputGun.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("textInput", "smile "+scanCode);
                Log.d("textInput", "getDevice Name:  "+event.getDevice().getName()+"  "+event.getNumber());
                if (event.getDevice().getName().contains("HID")) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            Log.d("textInput smile", scanCode);
                            tvscancode.setText(scanCode);
                            saveCode = scanCode;
                            placeOrder(saveCode);
                            scanCode = "";
                        } else {
                            if(event.getScanCode()<10)
                                scanCode += event.getNumber();
                            else
                            {
                                scanCode += (char) event.getUnicodeChar();
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_weixin);
        tvscancode = (EditText) findViewById(R.id.scanCode);
        tvscancode.setText(scanCode);
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        setTitle("总计消费"+DecimalHelper.getDecimalString(mTotalPrice)+"元");

        findViewById(R.id.dialogback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WechatPayDialog.this.dismiss();
            }
        });

        mBtncheckout = (Button) findViewById(R.id.btnCheckout);
        mBtncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveCode == null || scanCode.length()==0)
                {
                    saveCode = tvscancode.getText().toString().trim();
                }
                Log.i("smile","scancode: "+saveCode);
                placeOrder(saveCode);
            }
        });
        initScanDevice();
    }
    void placeOrder(final  String code)
    {
        if(code == null || code.length() ==0)
        {
            Log.i("smile","code length =0");
            ToastHelper.showToast(getContext(),"二维码不能为空");
            return;
        }

         doWeixinPay(mTradeNo ,code);
    }

    void doWeixinPay(String trade_no,String code)
    {
         final  LoadingDialog loadingDialog = new LoadingDialog(getContext(),R.style.MyDialog, "微信支付中");
        loadingDialog.show();
        final String ip = Utils.getWifiIp(getContext());
        Log.i("smile", "ip: " + ip);
//        String body = new Gson().toJson(mShopOrder);
        String body = mTradeDetail;
        MPosApplication.getInstance().getMsgBuilder().buildWinxinPayOrder(trade_no ,code,DecimalHelper.getWeixinDecimalString(mTotalPrice),ip).send(
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, throwable.toString());
                        loadingDialog.dismiss();
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.WechatOrderResponseMsg payOrderResponseMsg =
                                    LinkeaResponseMsgGenerator.generateWechatOrderResponseMsg(responseString);
                            if(payOrderResponseMsg.success)
                            {
                                mPaySuccess = true;
                               dismiss();
                            }
                            else
                            {
                                String erroInfo = payOrderResponseMsg.result_code_msg;
                                MessageBoxDialog messageBox  = new MessageBoxDialog(getContext(),erroInfo, false);
                                messageBox.show();
                                Log.e(TAG, erroInfo);
                            }

                        }catch (Exception e)
                        {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());

                        }
                        loadingDialog.dismiss();
                    }
                }
        );
    }
}

