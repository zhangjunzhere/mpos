package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bbpos.wisepad.WisePadController;
import com.itertk.app.mpos.BBPos;
import com.itertk.app.mpos.ItertkPos;
import com.itertk.app.mpos.ItertkPosResponseMsg;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyTTS;
import com.itertk.app.mpos.R;

import java.util.Hashtable;



public class ItertkBankcardDialog extends Dialog {
    private static String TAG = "BankcardDialog";
    Context context;
    TextView textTitle;
    String title;
    //Button btnOK;
    Button btnCancel;
    private OnClickButtonOKListener onClickButtonOKListener;
    private OnClickButtonCancelListener onClickButtonCancelListener;
    //BBPos bbPos;
    public Hashtable<String, String> cardInfo;
    public String pin1;
    public String pin2;

    private MyTTS tts;

    private ItertkPos itertkPos;

    public ItertkBankcardDialog(Context context, int theme, String title) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = title;

        itertkPos = ((MPosApplication)getContext().getApplicationContext()).getItertkPos();
        //bbPos = ((MPosApplication) getContext().getApplicationContext()).getBbPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }

    public void setTitle(String title) {
        textTitle.setText(title);
    }

    public void setButtonText(String title) {
        btnCancel.setText(title);
    }

    public ItertkBankcardDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        itertkPos = ((MPosApplication)getContext().getApplicationContext()).getItertkPos();
       // bbPos = ((MPosApplication) getContext().getApplicationContext()).getBbPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }


    void swipCard(){
        textTitle.setText("请刷卡");
        tts.speak(textTitle.getText().toString());
        btnCancel.setText("取消");

        itertkPos.buildReadCardMsg("1", "2", "3", "4").Send(new ItertkPosResponseMsg() {
            @Override
            public void onData(byte[] data) {
                if ((data.length > 20) && (data[0] == 0x1b) && data[2] == 0x73){

                }else{
                    String cardno = new String();

                    cardInfo = new Hashtable<String, String>();
                    cardInfo.put("maskedPAN", cardno);
                }
                ItertkBankcardDialog.this.dismiss();
            }
        });


//        bbPos.checkCard(new BBPos.CheckCardListener() {
//            @Override
//            public void onCheckCardListener(boolean cancel, WisePadController.CheckCardResult checkCardResult, Hashtable<String, String> stringStringHashtable) {
//                cardInfo = stringStringHashtable;
//
//                if (cardInfo != null) {
//                    textTitle.setText("请输入密码");
//                    tts.speak(textTitle.getText().toString());
//                    bbPos.startPinEntry(new BBPos.StartPinEntryListener() {
//                        @Override
//                        public void onStartPinEntryListener(WisePadController.PinEntryResult pinEntryResult, String s, String s2) {
//                            pin1 = s;
//                            pin2 = s2;
//                            ItertkBankcardDialog.this.dismiss();
//                        }
//                    });
//                } else {
//                    ItertkBankcardDialog.this.dismiss();
//                }
//            }
//        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_bankcard);
        setCanceledOnTouchOutside(false);

        textTitle = (TextView) findViewById(R.id.textTitle);


        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                bbPos.cancelCheckCard();
//                bbPos.resetDevice();

                ItertkBankcardDialog.this.dismiss();


                if (onClickButtonCancelListener != null)
                    onClickButtonCancelListener.onClickButtonCancel();
            }
        });

        Log.d(TAG, "onCreate");



//        setOnShowListener(new OnShowListener() {
//            @Override
//            public void onShow(DialogInterface dialog) {
//                Log.d(TAG, "onShow");
//
//                if (bbPos.isConnected()) {
//                    swipCard();
//                } else {
//                    bbPos.connectDevice(new BBPos.ConnectDeviceListener() {
//                        @Override
//                        public void onConnectDeviceListener(boolean success) {
//                            if (success) {
//                                textTitle.setText("连接设备成功！");
//                                tts.speak(textTitle.getText().toString());
//
//                                try {
//                                    Thread.sleep(200);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                swipCard();
//                            } else {
//                                textTitle.setText("连接设备失败！");
//                                tts.speak(textTitle.getText().toString());
//                                btnCancel.setText("确定");
//                                btnCancel.setBackgroundResource(R.drawable.btn_red);
//                            }
//                        }
//                    });
//
//                    textTitle.setText("正在连接设备");
//                    tts.speak(textTitle.getText().toString());
//                }
//            }
//        });
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
