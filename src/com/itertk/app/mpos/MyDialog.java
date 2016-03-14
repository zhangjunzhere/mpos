package com.itertk.app.mpos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Administrator on 2014/7/11.
 */
public class MyDialog extends Dialog {
    Context context;
    public MyDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public MyDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.mydialog);
    }
}
