package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bbpos.wisepad.WisePadController;
import com.itertk.app.mpos.BBPos;
import com.itertk.app.mpos.ExternalPos;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyTTS;
import com.itertk.app.mpos.R;

import java.math.BigDecimal;
import java.util.Hashtable;

/**
 * 银行卡dialog
 */

public class BankcardDialog extends Dialog {
    private static String TAG = "BankcardDialog";
    Context context;
    TextView textTitle;
    String title;
    //Button btnOK;
    Button btnCancel;
    private OnClickButtonOKListener onClickButtonOKListener;
    private OnClickButtonCancelListener onClickButtonCancelListener;
    ExternalPos bbPos;
    public Hashtable<String, String> cardInfo;
    public String pin1;
    public String pin2;

    private MyTTS tts;

    public BankcardDialog(Context context, int theme, String title) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = title;

        bbPos = ((MPosApplication) getContext().getApplicationContext()).getPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }

    public void setTitle(String title) {
        textTitle.setText(title);
    }

    public void setButtonText(String title) {
        btnCancel.setText(title);
    }

    public BankcardDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        bbPos = ((MPosApplication) getContext().getApplicationContext()).getPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }


    void swipCard(){
        textTitle.setText("请刷卡");
        tts.speak(textTitle.getText().toString());
        btnCancel.setText("");


        bbPos.checkCard(new ExternalPos.CheckCardListener() {


            @Override
            public void onCheckCardListener(boolean posCancel, boolean success, String cardNo, String track1, String track2, String track3,boolean iccard, String icdata,String cardseqno) {
                if(posCancel){
                    tts.speak("客户取消");
                    dismiss();
                    return;
                }

                if (success){
                    cardInfo = new Hashtable<String, String>();
                    cardInfo.put("no", cardNo);
                    cardInfo.put("track1", track1);
                    cardInfo.put("track2", track2);
                    cardInfo.put("track3", track3);
                    cardInfo.put("isiccard",String.valueOf(iccard));
                    cardInfo.put("icdata",icdata);
                    cardInfo.put("cardseqno",cardseqno);
                    cardInfo.put("ksn",MPosApplication.getInstance().getPosId());

                    Log.d(TAG, "TRACK1=" + track1 + " TRACK2=" + track2 + " TRACK3=" + track3);

                    textTitle.setText("请输入密码");
                    tts.speak(textTitle.getText().toString());
                    bbPos.startPin(cardNo,new BigDecimal(-1), new ExternalPos.StartPinListener() {
                        @Override
                        public void onStartPinListener(boolean success, String pinBlock, String ksn) {
                            if(success){
                                pin1 = pinBlock;
                                pin2 = ksn;
                            }else{
                                textTitle.setText("无效密码");
                                tts.speak(textTitle.getText().toString());
                            }

                            dismiss();
                        }
                    });

                }else{
                    textTitle.setText("刷卡失败，请重刷");
                    tts.speak(textTitle.getText().toString());
                    dismiss();
                }

            }
        });
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

                bbPos.cancelOperation();

                BankcardDialog.this.dismiss();


                if (onClickButtonCancelListener != null)
                    onClickButtonCancelListener.onClickButtonCancel();
            }
        });

        Log.d(TAG, "onCreate");



        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "onShow");

                if (bbPos.isDeviceConnected()) {
                    swipCard();
                } else {
                    bbPos.connectDevice(new ExternalPos.ConnectDeviceListener() {
                        @Override
                        public void onConnectDeviceListener(boolean success) {
                            if (success) {
                                textTitle.setText("连接设备成功！");
                                tts.speak(textTitle.getText().toString());

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                swipCard();
                            } else {
                                textTitle.setText("连接设备失败！");
                                tts.speak(textTitle.getText().toString());

                                btnCancel.setBackgroundResource(R.drawable.btn_red);
                                btnCancel.setText("确定");
                            }
                        }
                    });

                    textTitle.setText("正在连接设备");
                    tts.speak(textTitle.getText().toString());
                }
            }
        });
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
