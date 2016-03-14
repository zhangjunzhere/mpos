package com.itertk.app.mpos.trade.pay;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.trade.pos.BankcardOrderActivity;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.HashMap;

/**
 * Created by smile_gao on 2015/4/3.
 */
public class CardPay extends Pay {
    public final String TAG ="CashPay";
    HashMap<String, String> card;
    public CardPay(OnPayFinishListener listener, boolean needcreateorder, HashMap<String,String> cardinfo) {
        super(listener, needcreateorder);
        card = cardinfo;
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
        String track2 = card.get("track2");
        String icdata = "";
        boolean isiccard =Boolean.valueOf(card.get("isiccard"));
        Log.i("smile","isiccard "+isiccard);
        if(isiccard)
        {
            icdata =  card.get("icdata");
            // track2 = "";
        }
        Log.i("customerSign", "posidddd: " + MPosApplication.getInstance().getPosId());
        MPosApplication.getInstance().getMsgBuilder().buildPayOrderMsg(
                "PAY_MPOS", mSaleOrder.getSaleOrderNo(), 	MPosApplication.getInstance().getPosId(), card.get("no"),card.get("pin"),
                card.get("track1"), icdata, card.get("cardseqno"), track2, card.get("track3"), "trackkey",
                MPosApplication.getInstance().getMacKey(),"pinkey"
        ).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mPayFinishListener.onPayFail(Pay_Fail_Type.network, "网络无法连接");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                try {
                    Log.d("smile", "onSuccess " + responseString);
                    LinkeaResponseMsg.ResponseMsg payOrderResponseMsg =
                            LinkeaResponseMsgGenerator.generatePayOrderResponseMsg(responseString);
                    if (payOrderResponseMsg.success) {
                       mPayFinishListener.onPaySuccess();
                    } else {

                        String erroInfo = payOrderResponseMsg.result_code_msg;
                        mPayFinishListener.onPayFail(Pay_Fail_Type.returnfail,erroInfo);
                    }
                } catch (Exception e) {
                     mPayFinishListener.onPayFail(Pay_Fail_Type.excetption,"解析错误");

                }
            }
        });
    }
}
