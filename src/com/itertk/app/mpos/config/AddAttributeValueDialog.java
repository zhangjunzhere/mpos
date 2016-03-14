package com.itertk.app.mpos.config;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.itertk.app.mpos.R;

/**
 * Created by Administrator on 2014/7/13.
 * 添加属性值对话框
 */
public class AddAttributeValueDialog extends Dialog {
    Context context;
    public AddAttributeValueDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }
    public AddAttributeValueDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_add_attribute_value);
    }
}
