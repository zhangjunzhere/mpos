package com.itertk.app.mpos.trade.taobao;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itertk.app.mpos.R;

/**
 * Created by Administrator on 2014/7/11.
 * 用来show 结果通知的dialog
 */
public class MessageBoxDialog extends Dialog {

    private  String  mMessage;
    private String mBody="";
    private Boolean mSuccess = true;

    public MessageBoxDialog(Context context, String message, Boolean success){
        super(context, R.style.MyDialog);
        this.mMessage = message;
        this.mSuccess = success;
    }

    public MessageBoxDialog(Context context, String message,String body, Boolean success){
        super(context, R.style.MyDialog);
        this.mMessage = message;
        this.mBody = body;
        this.mSuccess = success;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_messagebox);

        TextView tv_message = (TextView) findViewById(R.id.tv_message);
        TextView tv_body = (TextView) findViewById(R.id.tv_body);
        ImageView iv_icon = (ImageView) findViewById(R.id.iv_icon);

        int colorId = R.color.solid_red;
        int iconId = R.drawable.failure;
        if(mSuccess){
             colorId = R.color.btn_blue;
             iconId = R.drawable.success;
        }

        tv_message.setText(mMessage);
        tv_body.setText(mBody);

        iv_icon.setImageResource(iconId);
        tv_message.setTextColor(getContext().getResources().getColor(colorId));

        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageBoxDialog.this.dismiss();
            }
        });
    }
}
