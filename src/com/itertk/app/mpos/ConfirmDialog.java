package com.itertk.app.mpos;
/*
* 确认对话框
* */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class ConfirmDialog extends Dialog {
    Context context;
    TextView textTitle;
    String title;
    Button btnOK;
    Button btnCancel;
    private OnClickButtonOKListener onClickButtonOKListener;
    private OnClickButtonCancelListener onClickButtonCancelListener;

    public ConfirmDialog(Context context, int theme , String title) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = title;
    }
    public ConfirmDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_confirm);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(title);

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConfirmDialog.this.dismiss();
                if(onClickButtonOKListener != null) onClickButtonOKListener.onClickButtonOK();
            }
        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDialog.this.dismiss();
                if(onClickButtonCancelListener != null) onClickButtonCancelListener.onClickButtonCancel();
            }
        });


    }

    public void setOnClickButtonOKListener(OnClickButtonOKListener onClickButtonOKListener) {
        this.onClickButtonOKListener = onClickButtonOKListener;
    }

    public void setOnClickButtonCancelListener(OnClickButtonCancelListener onClickButtonCancelListener) {
        this.onClickButtonCancelListener = onClickButtonCancelListener;
    }

    public interface OnClickButtonOKListener{
        public void onClickButtonOK();
    }
    public interface OnClickButtonCancelListener{
        public void onClickButtonCancel();
    }
}
