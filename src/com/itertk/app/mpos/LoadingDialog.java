package com.itertk.app.mpos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/*
* 加载对话框
* */

public class LoadingDialog extends Dialog {
    Context context;
    TextView textTitle;
    ImageView imageLoading;
    String title;


    public LoadingDialog(Context context, int theme, String title) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.title = title;
    }
    public LoadingDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_loading);

        setCanceledOnTouchOutside(false);

        imageLoading = (ImageView)findViewById(R.id.imageLoading);
        LinearInterpolator linearInterpolator = new LinearInterpolator();
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.loading);
        animation.setInterpolator(linearInterpolator);
        // 使用ImageView显示动画
        imageLoading.startAnimation(animation);

    }


}
