package com.itertk.app.mpos;

import android.app.Activity;
import android.util.Log;

import com.itertk.app.mpos.trade.TradeHomeActivity;
import com.itertk.app.mpos.trade.pos.PosActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by smile_gao on 2015/3/27.
 * acitivity 管理器
 */
public class MyActivityManager {
    List<Activity> activityList;
    private Class  backToActivityClass = PosActivity.class;
    public MyActivityManager()
    {
        activityList = new ArrayList<Activity>();
    }
    public synchronized void push(Activity ac)
    {
           activityList.add(ac);
    }
    public synchronized void pop(Class ac)
    {
        if(activityList == null || activityList.size() ==0)
        {
            return ;
        }
        Iterator<Activity> itor = activityList.iterator();
        while (itor.hasNext())
        {
             Activity a =  itor.next();
//            Log.i("smile","getCanonicalName:"+ac.getCanonicalName());
//            Log.i("smile","getName:"+ac.getName());
//            Log.i("smile","getSimpleName:"+ac.getSimpleName());
//            Log.i("smile","getLocalClassName:"+a.getLocalClassName());
//            Log.i("smile","getComponentName:"+a.getComponentName());
//            Log.i("smile","getCanonicalName:"+a.getClass().getCanonicalName());
//            Log.i("smile","getName:"+a.getClass().getName());
//            Log.i("smile","getSimpleName:"+a.getClass().getSimpleName());
            if(a.getClass().getCanonicalName().equals(ac.getCanonicalName()))
            {
                Log.i("smile","myactivitymanager remove "+ac.getClass().toString());
                a.finish();
                itor.remove();
            }
        }
    }

    public void setBackToActivityClass(Class ac)
    {
        backToActivityClass = ac;
    }
    public  Class getBackToActivityClass()
    {
        if(backToActivityClass == null)
        {
            backToActivityClass = TradeHomeActivity.class;
        }
            return backToActivityClass;
    }
}
