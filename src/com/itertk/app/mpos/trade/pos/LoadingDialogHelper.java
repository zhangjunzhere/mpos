package com.itertk.app.mpos.trade.pos;

import android.content.Context;
import android.util.Log;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.R;

/**
 * Created by smile_gao on 2015/3/28.
 * 等待窗口辅助类
 */
public class LoadingDialogHelper {
    static LoadingDialog dlg ;
    public synchronized static  void show(Context context)
    {

        if(dlg != null && dlg.isShowing())
        {
            dlg.dismiss();
        }

        dlg = new LoadingDialog(context, R.style.MyDialog);
        dlg.show();

    }
    public synchronized   static  void dismiss()
    {
        if(dlg!=null)
                 dlg.dismiss();
    }


}
