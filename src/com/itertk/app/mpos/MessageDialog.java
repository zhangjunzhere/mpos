package com.itertk.app.mpos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Administrator on 2014/7/11.
 * 消息对话框
 */
public class MessageDialog extends Dialog {
    Context context;
    TextView textTitle;
    TextView textMiniTitle;
    Button btnOK;
    public MessageDialog(Context context) {
        super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
        this.context = context;
    }

    public void setTitle(String title, String mini){
        textTitle.setText(title);
        textMiniTitle.setText(mini);
    }

    public MessageDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_message);

        textTitle = (TextView)findViewById(R.id.textTitle);
        textMiniTitle = (TextView)findViewById(R.id.textMiniTitle);

        btnOK = (Button)findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDialog.this.dismiss();
            }
        });
    }
}
