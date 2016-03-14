package com.itertk.app.mpos.trade.pay;

import android.util.Log;
import android.widget.Toast;

import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.trade.pos.PrintHelper;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by smile_gao on 2015/4/3.
 */
public class CashPay extends Pay {
    public final String TAG ="CashPay";
    public CashPay(OnPayFinishListener listener, boolean needcreateorder) {
        super(listener, needcreateorder);
    }

    @Override
    public void pay(final SaleOrder saleOrder) {
        if(mNeedCreateOrder)
        {
            createOrder(saleOrder,new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    saleOrder.setUpload(false);
                    saleOrder.update();
                    mPayFinishListener.onPayFail(Pay_Fail_Type.network,"网络无法连接");
                    doRealPay();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(TAG, responseString);
                    try {
                        LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                                LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                        if (createOrderResponseMsg.success) {
                            saleOrder.setSaleOrderNo(createOrderResponseMsg.order.trade_no);
                            saleOrder.update();
                        }
                        else
                        {
                            mPayFinishListener.onPayFail(Pay_Fail_Type.returnfail, createOrderResponseMsg.result_code_msg);
                        }

                    }catch (Exception e)
                    {
                        mPayFinishListener.onPayFail(Pay_Fail_Type.excetption,e.toString());
                    }
                   doRealPay();
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
        PrintHelper.openCashBox();
        mPayFinishListener.onPaySuccess();
    }
}
