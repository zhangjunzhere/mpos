package com.itertk.app.mpos.trade.pay;

import android.util.Log;

import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by smile_gao on 2015/4/3.
 */
public class WechatPay extends  Pay {
    public WechatPay(OnPayFinishListener listener, boolean needcreateorder) {
        super(listener, needcreateorder);
    }

    @Override
    public void pay(final  SaleOrder saleOrder) {
        super.pay(saleOrder);
        if(mNeedCreateOrder)
        {
            createOrder(mSaleOrder,new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    mPayFinishListener.onPayFail(Pay_Fail_Type.network, "网络无法连接");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG, responseString);
                    try {
                        LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                                LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                        if (createOrderResponseMsg.success) {
                            mSaleOrder.setSaleOrderNo(createOrderResponseMsg.order.trade_no);
                            mSaleOrder.update();
                            doRealPay();
                        }
                        else
                        {
                            mPayFinishListener.onPayFail(Pay_Fail_Type.returnfail, createOrderResponseMsg.result_code_msg);
                        }

                    }catch (Exception e)
                    {
                        mPayFinishListener.onPayFail(Pay_Fail_Type.excetption,e.toString());
                    }

                }
            });
        }
        else
        {
            doRealPay();
        }

    }

    @Override
    public void doRealPay() {
        super.doRealPay();
    }
}
