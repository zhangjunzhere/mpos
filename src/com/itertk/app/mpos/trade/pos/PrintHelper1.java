package com.itertk.app.mpos.trade.pos;

import com.centerm.PrinterService;
import com.centerm.moneybox.MoneyBoxService;

/**
 * Created by smile_gao on 2015/4/17.
 * 打印机帮助类，打开钱箱
 */
public class PrintHelper1{

    public static void printString(String str) {
        try {
            PrinterService.getService().printString(str, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void openCashBox() {
        MoneyBoxService.getInstance().openMoneyBox(true);
    }
}
