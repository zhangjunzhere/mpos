package com.itertk.app.mpos.trade.pos;

import com.itertk.app.mpos.utility.Arith;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by smile_gao on 2015/3/24.
 * 金钱显示辅助类
 */
public class DecimalHelper {

    public static String getWeixinDecimalString(String price)
    {
        return  (new DecimalFormat("0.00")).format(Arith.newBigDecimal(price)); //.multiply(new BigDecimal(100)
    }
    public static String getDecimalString(double price)
    {
        return  (new DecimalFormat("0.00")).format(price);
    }
    public static String getDecimalString(BigDecimal price)
    {
        return  (new DecimalFormat("0.00")).format(price);
    }
    public static String getDecimalString(String price)
    {
        return  (new DecimalFormat("0.00")).format(Arith.newBigDecimal(price));
    }
}
