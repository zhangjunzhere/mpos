package com.itertk.app.mpos.trade.pos;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.comm.LinkeaMsgBuilder;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by smile_gao on 2015/3/16.
 * 上传订单辅助类
 */
public class UpLoadOrderHelper {
    static AsyncTask uploadTask;

    public static String genChoutoutOrderToJson(SaleOrder saleorder) {

        LinkeaMsgBuilder.CheckoutOrderDetail checkoutOrder = new LinkeaMsgBuilder.CheckoutOrderDetail();
        checkoutOrder.goods = new ArrayList<LinkeaMsgBuilder.CheckoutOrderDetail.Good>();
        for (SaleOrderItem si : saleorder.getSaleOrderItemList()) {

            LinkeaMsgBuilder.CheckoutOrderDetail.Good good = new LinkeaMsgBuilder.CheckoutOrderDetail.Good();
            good.goodsId = si.getProductId();
            good.goodsName = si.getName();
            good.goodsPrice = si.getPrice();
            good.goodsItem = si.getCountProduct();
            good.barCode = si.getBar_code();
            good.memberNo = String.valueOf(Utils.getMemberId());

            checkoutOrder.goods.add(good);
        }

        Log.d("smile", "checkout to json :" + checkoutOrder.toString());
        return checkoutOrder.toString();
    }

      public static void uploadOrder(final SaleOrder saleOrder) {


        MPosApplication.getInstance().getMsgBuilder().buildCreateOrderMsg2(saleOrder.getShihouPrice().toString(), "1009", "支付-" + MPosApplication.getInstance().getMember().member.member_id, UpLoadOrderHelper.genChoutoutOrderToJson(saleOrder)
        ).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("smile", "upload order fail");

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.i("smile", "upload order response "+responseString);
                try {
                    LinkeaResponseMsg.CreateOrderResponseMsg createOrderResponseMsg =
                            LinkeaResponseMsgGenerator.generateCreateOrderResponseMsg(responseString);
                    if (createOrderResponseMsg.success) {

                        Log.i("smile", "upload order success");
                        saleOrder.setUpload(true);
                        saleOrder.setSaleOrderNo(createOrderResponseMsg.order.trade_no);
                        saleOrder.update();
                    }
                } catch (Exception e) {

                }
                Log.i("smile", "upload order finish");
            }
        });

//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.i("smile", "index " + System.currentTimeMillis());
//                semp.release();
//            }
//        });
//        t.start();
        // semp.acquire();

    }
}
