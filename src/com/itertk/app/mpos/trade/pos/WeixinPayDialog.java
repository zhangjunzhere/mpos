package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyTTS;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.trade.taobao.MessageBoxDialog;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by smile_gao on 2015/3/27.
 * 微信支付窗口
 */
public class WeixinPayDialog extends Dialog {
    private static String TAG = "WeixinPayDialog";
    Context context;
    private MyTTS tts;
    EditText tvscancode ;
    TextView totalPrice;
    Button mBtncheckout;
    String scanCode ="";
    SaleOrder mSaleOrder;
    EditText textInputGun;
    String saveCode="";
    boolean mDirectPay=false;
    public WeixinPayDialog(Context context, int theme,SaleOrder saleOrder) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        mSaleOrder = saleOrder;
        saveCode = "";
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }
    public void setDirectPay(boolean direct)
    {
        mDirectPay = direct;
    }
    public void setTitle(String title) {
        totalPrice.setText(title);
    }
    void initScanDevice()
    {
//        WeixinPayDialog dlg = new WeixinPayDialog(getContext(),R.style.MyDialog,"135792468",mSaleOrder);
//        dlg.show();
        scanCode = new String();
    //    textInputGun = (EditText) findViewById(R.id.textInputGun);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_weixin);
        tvscancode = (EditText) findViewById(R.id.scanCode);
        tvscancode.setText(scanCode);
        tvscancode.addTextChangedListener(new TextWatcher() {
            int prelength=0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    Log.i("smile text change",i+" "+i2 +"  "+charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                  String code =tvscancode.getText().toString();
                String codenospace = code.replace(" ","");
                if(code.length()>prelength&&codenospace.length()%4==0&&codenospace.length()>3&&!code.endsWith(" "))
                {
                    code+=" ";
                    tvscancode.setText(code);
                    tvscancode.setSelection(code.length());
                }
                prelength=code.length();

            }
        });
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        setTitle("总计消费"+DecimalHelper.getDecimalString(mSaleOrder.getShihouPrice())+"元");

        mBtncheckout = (Button) findViewById(R.id.btnCheckout);
        mBtncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(saveCode == null || scanCode.length()==0)
                {
                    saveCode = tvscancode.getText().toString().trim();
                }
                Log.i("smile","scancode: "+saveCode);
                saveCode = saveCode.replace(" ","");
                if(!mDirectPay)
                {
                    placeOrder(saveCode);
                }
                else
                {
                    doWeixinPay(scanCode);
                }
            }
        });
        ImageView back = (ImageView) findViewById(R.id.dialogback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
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
        Log.i("smile","weixin not direct pay");
        LoadingDialogHelper.show(getContext());
        MPosApplication.getInstance().getMsgBuilder().buildCreateOrderMsg2("" + mSaleOrder.getShihouPrice(), "1009", "支付-" + MPosApplication.getInstance().getMember().member.member_id,UpLoadOrderHelper.genChoutoutOrderToJson(mSaleOrder)
        ).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, throwable.toString());
                Toast.makeText(getContext(),"网络连接失败", Toast.LENGTH_SHORT).show();
                LoadingDialogHelper.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                try {
                    LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                            LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                    if (createOrderResponseMsg.success) {
                        mSaleOrder.setSaleOrderNo(createOrderResponseMsg.order.trade_no);
                        mSaleOrder.update();
                        doWeixinPay(code);
                    }
                    else
                    {

                        Toast.makeText(getContext(), createOrderResponseMsg.result_code_msg, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e)
                {

                }


            }
        });



    }
    private boolean getScancode(KeyEvent event)
    {
        Log.d("textInput", "smile "+scanCode);
        Log.d("textInput", "getDevice Name:  "+event.getDevice().getName()+"  "+event.getNumber());
        if (event.getDevice().getName().contains("HID")) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
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
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("dispatchKeyEvent", event.toString());
        getScancode(event);
        return super.dispatchKeyEvent(event);
    }
    void doWeixinPay(String code)
    {
        final String ip = Utils.getWifiIp(getContext());
        Log.i("smile","ip: "+ip);
        MPosApplication.getInstance().getMsgBuilder().buildWinxinPayOrder(mSaleOrder.getSaleOrderNo() ,code,DecimalHelper.getDecimalString(mSaleOrder.getShihouPrice()),ip).send(
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, throwable.toString());
                        Toast.makeText(getContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                        LoadingDialogHelper.dismiss();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.d(TAG, responseString);
                        try {
                            LinkeaResponseMsg.WechatOrderResponseMsg payOrderResponseMsg =
                                    LinkeaResponseMsgGenerator.generateWechatOrderResponseMsg(responseString);
                            if(payOrderResponseMsg.success)
                            {
                                mSaleOrder.setPayedTime(Utils.getTime());
                                mSaleOrder.update();
                                PrintHelper.printExternalOrder(getContext(),mSaleOrder,null,null);
                                MPosApplication.getInstance().getMyActivityManager().pop(PosActivity.class);
                                Intent it = new Intent(getContext(),  MPosApplication.getInstance().getMyActivityManager().getBackToActivityClass());
                                getContext().startActivity(it);

                            }
                            else
                            {
                                LoadingDialogHelper.dismiss();
                                String erroInfo = payOrderResponseMsg.result_code_msg;
                                final MessageBoxDialog msgbox = new MessageBoxDialog(getContext(),"支付失败", erroInfo,false);
                                msgbox.show();
                                msgbox.setOnDismissListener(new OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        WeixinPayDialog.this.dismiss();
                                    }
                                });

                                //, PosActivity.class);
                               // Toast.makeText(getContext(), erroInfo, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, erroInfo);
                            }

                        }catch (Exception e)
                        {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, e.toString());

                        }
                        LoadingDialogHelper.dismiss();
                    }
                }
        );
    }
}

