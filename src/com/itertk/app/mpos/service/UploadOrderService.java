package com.itertk.app.mpos.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.trade.pos.UpLoadOrderHelper;

import java.util.List;

/**
 * Created by smile_gao on 2015/4/8.
 * 上传订单
 */
public class UploadOrderService extends Service {
    public static  final  String UPLOAD="com.itertk.app.mpos.action.upload";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    protected void onHandleIntent(Intent intent){
        uploadOrder();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("UploadOrderService", "onCreate");
    }
    void uploadOrder() {
        SaleOrderDao sdao = MPosApplication.getInstance().getDataHelper().getDaoSession().getSaleOrderDao();
        List<SaleOrder> orderlist = sdao.queryBuilder().where(SaleOrderDao.Properties.Upload.eq(false)).list();
        for (SaleOrder s: orderlist)
        {
            UpLoadOrderHelper.uploadOrder(s);
        }
        Log.i("smile", "orderlist: " + orderlist.size());
    }
}
