package com.itertk.app.mpos.trade.pay;

import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * Created by smile_gao on 2015/4/3.
 */
public interface IPay {
    void pay(SaleOrder saleOrder);

    void pay(ShopOrder shopOrder);

    void createOrder(SaleOrder saleOrder,TextHttpResponseHandler responseHandler);

    void createOrder(ShopOrder shopOrder,TextHttpResponseHandler responseHandler);

    void doRealPay();
}
