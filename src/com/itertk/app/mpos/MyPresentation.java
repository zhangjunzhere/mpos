package com.itertk.app.mpos;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import java.io.File;
import java.util.Random;

/**
 * Created by Administrator on 2014/11/6.
 * 双屏异步处理
 */
public class MyPresentation extends Presentation {
    ImageView imageAD;
    File[] files;

    public MyPresentation(Context outerContext, Display display) {
        super(outerContext, display);

        files = new File("/sdcard/mpos/").listFiles();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_my);

        imageAD = (ImageView)findViewById(R.id.imageAD);
    }

    public void randShow(){
        show();
       Random random=new Random();
       File file = files[random.nextInt(files.length)];

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        imageAD.setImageBitmap(bitmap);
    }
}
