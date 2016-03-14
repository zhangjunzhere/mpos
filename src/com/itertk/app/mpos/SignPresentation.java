package com.itertk.app.mpos;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;

/**
 * Created by Administrator on 2014/11/20.
 * 副屏签名
 */
public class SignPresentation extends Presentation {
    public SignPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
