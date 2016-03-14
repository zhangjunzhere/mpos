package com.itertk.app.mpos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itertk.app.mpos.StartActivity;

/**
 * Created by jz on 2015/4/23.
 * 开机启动
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot="android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)){
            Intent bootStartIntent=new Intent(context,StartActivity.class);
            bootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(bootStartIntent);
        }
    }
}
