package com.itertk.app.mpos.utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by smile_gao on 2015/3/25.
 * 对toast的封装
 */
public class ToastHelper {
    static Toast toast;


    public  static  void showToast(Context context,String msg)
    {
        if(toast !=null)
        {
            toast.cancel();

        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }





}
