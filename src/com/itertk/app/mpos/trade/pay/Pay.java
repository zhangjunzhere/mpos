package com.itertk.app.mpos.trade.pay;

import android.util.Log;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.trade.pos.UpLoadOrderHelper;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by smile_gao on 2015/4/3.
 */
public class Pay implements IPay {
    public final String TAG ="ParentPay";
    public  enum  Pay_Fail_Type{network,returnfail,excetption,others};
    OnPayFinishListener mPayFinishListener;
    boolean mNeedCreateOrder =false;
    SaleOrder mSaleOrder;
    public  Pay(OnPayFinishListener listener, boolean needcreateorder)
    {
        mPayFinishListener = listener;
        mNeedCreateOrder = needcreateorder;
    }
    @Override
    public void pay(final SaleOrder saleOrder) {
        mSaleOrder=saleOrder;
    }

    @Override
    public void pay(final ShopOrder shopOrder) {

    }

    @Override
    public void createOrder(SaleOrder saleOrder,TextHttpResponseHandler responseHandler) {
        if(responseHandler == null)
        {
            Log.i("smile","responseHandler empty");
            return;
        }
        MPosApplication.getInstance().getMsgBuilder().buildCreateOrderMsg2(saleOrder.getShihouPrice().toString(),"1009","支付", UpLoadOrderHelper.genChoutoutOrderToJson(saleOrder)).send(responseHandler);
    }

    @Override
    public void createOrder(ShopOrder shopOrder,TextHttpResponseHandler responseHandler) {
      //  MPosApplication.getInstance().getMsgBuilder().buildCreateOrderMsg2()
    }

    @Override
    public void doRealPay() {

    }

  public   interface OnPayFinishListener{
       void onPaySuccess();
       void onPayFail(Pay_Fail_Type failtype, String reason);
    };
}
