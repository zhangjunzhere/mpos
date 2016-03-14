package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.ExternalPos;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyTTS;
import com.itertk.app.mpos.R;

import java.math.BigDecimal;
import java.util.Hashtable;

/**
 * 刷卡器操作对话框
 */
public class PosDialog extends Dialog {
    private static String TAG = "PosDialog";
    Context context;
    TextView textTitle;
    String title;
    //Button btnOK;
    ExternalPos pos;
    Button btnCancel;
    private OnClickButtonOKListener onClickButtonOKListener;
    private OnClickButtonCancelListener onClickButtonCancelListener;

    public Hashtable<String, String> cardInfo;
    public String pin1;
    public String pin2;

    private MyTTS tts;
    BigDecimal mTotalPrice = Arith.newZeroBigDecimal();

    public PosDialog(Context context, int theme, String title) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = title;


        pos = ((MPosApplication) getContext().getApplicationContext()).getPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
    }

    public void setTitle(String title) {
        textTitle.setText(title);
    }

    public void setButtonText(String title) {
        btnCancel.setText(title);
    }

    public PosDialog(Context context, int theme,BigDecimal totalprice) {
        super(context, theme);
        this.context = context;
        pos = ((MPosApplication) getContext().getApplicationContext()).getPos();
        tts = ((MPosApplication) getContext().getApplicationContext()).getMyTTS();
        mTotalPrice = totalprice;
    }


    void swipCard(){
        textTitle.setText("请刷卡");
        tts.speak(textTitle.getText().toString());
        btnCancel.setText("");


        pos.checkCard(new ExternalPos.CheckCardListener() {
            @Override
            public void onCheckCardListener(boolean posCancel, boolean success, String cardNo, String track1, String track2, String track3,boolean iccard,String icdata ,String cardseqno) {
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


                    Log.d(TAG, "TRACK1=" + track1 + " TRACK2=" + track2 + " TRACK3=" + track3+" iccard: "+iccard);

                    textTitle.setText("请输入密码");
                    tts.speak(textTitle.getText().toString());
                    pos.startPin(cardNo,mTotalPrice, new ExternalPos.StartPinListener() {
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
                pos.cancelOperation();
                PosDialog.this.dismiss();

                if (onClickButtonCancelListener != null)
                    onClickButtonCancelListener.onClickButtonCancel();
            }
        });

        Log.d(TAG, "onCreate");



        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.d(TAG, "onShow");

                if (pos.isDeviceConnected()) {
                    swipCard();
                } else {
                    pos.connectDevice(new ExternalPos.ConnectDeviceListener() {
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
                                btnCancel.setText("确定");
                                btnCancel.setBackgroundResource(R.drawable.btn_red);
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
