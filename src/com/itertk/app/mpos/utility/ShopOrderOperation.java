package com.itertk.app.mpos.utility;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaMsgBuilder;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.dbhelper.ShopOrderItem;
import com.itertk.app.mpos.trade.taobao.ShoppingOrderDialog;
import com.itertk.app.mpos.trade.taobao.TradeOrderStatusEnum;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jz on 2015/3/28.
 * 对订货订单操作类
 */
public class ShopOrderOperation {
//    public static   final int PAY_STATUS_UNPAY = TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue();
//    public static   final int PAY_STATUS_PAYED = ;
//    public static   final int PAY_STATUS_COMPLETE = 3;

    private static ShopOrderOperation mShopOrderOperation ;
    public static ShopOrderOperation getInstance(){
        if(mShopOrderOperation == null){
            mShopOrderOperation = new ShopOrderOperation();
        }
        return mShopOrderOperation;
    }

    private final static String TAG="ShopOrderOperation";

    public static  void createShopOrder(){

    }

    public   void cancelShopOrder(final Context context, final  ShopOrder shopOrder,final ShoppingOrderDialog.ShopCartListViewAdapter adapter){

        final LoadingDialog loadingDialog = new LoadingDialog(context, R.style.MyDialog);
        loadingDialog.show();

       int status = shopOrder.getStatus();
       int[] deleteDirectly = new int[]{

               TradeOrderStatusEnum.BUYER_PAY_FAILURE.getValue(),
               TradeOrderStatusEnum.WAIT_BUYER_PAY.getValue(),
               TradeOrderStatusEnum.TRADE_CLOSED_BY_LINKEA.getValue(),
               TradeOrderStatusEnum.TRADE_CLOSED.getValue(),
               TradeOrderStatusEnum.TRADE_FINISHED.getValue()
       };

        for(int direct : deleteDirectly){
            if(status == direct){
                adapter.deleteShopOrder(shopOrder);
                loadingDialog.dismiss();
                return ;
            }
        }

        final MPosApplication mPosApplication = MPosApplication.getInstance();
        String orderNo = shopOrder.getTrade_no();
        String order_detail_json = getOrderDetailJson(shopOrder);
        mPosApplication.getMsgBuilder().buildCancelOrderMsg(orderNo, order_detail_json).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());

                return;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                loadingDialog.dismiss();

                try {
                    LinkeaResponseMsg.CancelOrderResponseMsg msg =
                            LinkeaResponseMsgGenerator.generateCancelOrderResponseMsg(responseString);
                    if (msg.base_success) {
                        //only one order could pay
                        //remove local order from database
                        adapter.deleteShopOrder(shopOrder);

                        Toast.makeText(context, "购物订单取消成功", Toast.LENGTH_SHORT).show();
                    } else {
                        String erroInfo = msg.base_resultMsg;
                        Toast.makeText(context, erroInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, erroInfo);
                    }

                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            }
        });
    }

    private   String getOrderDetailJson(ShopOrder shopOrder ) {
        BigDecimal mTotolPrice = Arith.newZeroBigDecimal();
        LinkeaMsgBuilder.ShopOrderDetail shopOrderDetail = new LinkeaMsgBuilder.ShopOrderDetail();
        shopOrderDetail.supplier = new ArrayList<LinkeaMsgBuilder.ShopOrderDetail.Supplier>();
//        for(ShopOrder shopOrder : mShopOrders){
            LinkeaMsgBuilder.ShopOrderDetail.Supplier supplier  = new LinkeaMsgBuilder.ShopOrderDetail.Supplier();
            supplier.supplierId = shopOrder.getSupplierId();
            supplier.orderDate = Utils.formatDate();
            supplier.consigneeName = shopOrder.getConsigneeName();
            supplier.consigneeMobile = shopOrder.getConsigneePhone() ;
            supplier.consigneePhone =   shopOrder.getConsigneePhone() ;
            supplier.consigneeAddress =  shopOrder.getConsigneeAddress();
            supplier.paymentName = shopOrder.getPaymentName();
            supplier.transferName =  shopOrder.getTransferName();
            supplier.totalPrice =  shopOrder.getTotalPrice();
            supplier.remark = shopOrder.getRemark();

            mTotolPrice = mTotolPrice.add(Arith.newBigDecimal(supplier.totalPrice));

            supplier.items = new ArrayList<LinkeaMsgBuilder.ShopOrderDetail.Item>();
            List<ShopOrderItem> shopOrderItems = shopOrder.getShopOrderItemList();
            for(ShopOrderItem shopOrderItem : shopOrderItems){
                LinkeaMsgBuilder.ShopOrderDetail.Item item = new LinkeaMsgBuilder.ShopOrderDetail.Item();
                item.productID =  shopOrderItem.getProductId();
                item.productName = shopOrderItem.getProduct().getProd_name();
                item.price =  shopOrderItem.getPrice();
                item.quantity =  shopOrderItem.getQuantity();
                item.totalPrice = shopOrderItem.getTotalPrice();
                supplier.items.add(item);
            }
            shopOrderDetail.supplier.add(supplier);
//        }

        Log.v(TAG, shopOrderDetail.toString());
        return shopOrderDetail.toString();
    }
}
