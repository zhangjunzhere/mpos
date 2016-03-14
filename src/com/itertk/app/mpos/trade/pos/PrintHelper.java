package com.itertk.app.mpos.trade.pos;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderReduce;
import com.itertk.app.mpos.trade.MyPrinter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jz on 2015/3/15.
 * 打印机帮助类
 */
public class PrintHelper {
    static AsyncTask  printerTask;
  //  static  MyPrinter myPrinter;


    private static void getSaleOrderPaperTitle(SaleOrder saleOrder,List<String> printlist) {

        printlist.add("              MPOS\n");
        printlist.add("订单号：" + saleOrder.getSaleOrderNo() + "\n");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        printlist.add("支付时间：" + simpleDateFormat.format(saleOrder.getPayedTime()) + "\n");
        printlist.add("名称       单价    数量   金额\n");
        printlist.add("================================");
//
//
//        stringBuffer.setLength(0);

    }
    private static void getYinShouShishou(SaleOrder saleOrder,String pay,String oddchange,List<String> printlist)
    {
            String yinshoushishou= leftFill("应收：" + (new DecimalFormat("0.00")).format(saleOrder.getShihouPrice()) + "元", 8)
                    + " " + leftFill("实收："+(new DecimalFormat("0.00")).format(Arith.newBigDecimal(pay)), 8)
                    + " " + leftFill("找零："+(new DecimalFormat("0.00")).format(Arith.newBigDecimal(oddchange)), 8);
        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append("\n");
        stringBuffer.append("\n");
        stringBuffer.append("\n");
        stringBuffer.append("\n");
            printlist.add(yinshoushishou);
            printlist.add((stringBuffer.toString()));
    }
    private static void getSaleOrderPaperContent(SaleOrder saleOrder,List<String> printlist) {
        StringBuffer stringBuffer = new StringBuffer();
        List<SaleOrderItem> soilist = saleOrder.getSaleOrderItemList();

        for (SaleOrderItem saleOrderItem : soilist) {
          //  saleOrderItem.setName("我們都是好朋友拉拉sfhehe拉");
            int length = saleOrderItem.getName().length();
            if(length>5)
            {
                printlist.add(  rightFill(saleOrderItem.getName(), 16));
                stringBuffer.append(
                        rightFill("         ", 7)
                                + " " + leftFill((new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getOnePrice())), 9)
                                + " " + leftFill("" + saleOrderItem.getCountProduct(), 3)
                                + " " + leftFill((new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getPrice())), 9)
                                + "\n");
            }
            else {
                stringBuffer.append(
                        rightFill(saleOrderItem.getName(), 5)
                                + " " + leftFill((new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getOnePrice())), 9)
                                + " " + leftFill("" + saleOrderItem.getCountProduct(), 3)
                                + " " + leftFill((new DecimalFormat("0.00")).format(Arith.newBigDecimal(saleOrderItem.getPrice())), 9)
                                + "\n");
            }

            printlist.add(stringBuffer.toString());

            stringBuffer.setLength(0);
        }
        List<SaleOrderReduce> list= saleOrder.getSaleOrderReduceList();
        if(list != null &&list.size()>0)
        {
            Reduce  reduce = list.get(0).getReduce();
            stringBuffer.append(
                    rightFill(reduce.getName(), 5)
                            + " " + leftFill("-"+DecimalHelper.getDecimalString(reduce.getValue()), 9)
                            + "\n"); 
		  try {
              printlist.add(stringBuffer.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            stringBuffer.setLength(0);
        }

        stringBuffer.append("================================");
        stringBuffer.append("总计：" + (new DecimalFormat("0.00")).format(saleOrder.getShihouPrice()) + "元");

        stringBuffer.append("\n");


        printlist.add(stringBuffer.toString());
        stringBuffer.setLength(0);
    }

    public static String leftFill(String src, int length) {
        String dest = new String();

        dest = String.format("%1$" + length + "s", src);

        return dest;
    }

    public static String rightFill(String src, int length) {
        String dest = new String();

        if (src.length() <= length) {
            dest = String.format("%1$-" + length + "s", src);
        } else
            dest = src.substring(0, length);

        return dest;
    }
    /*
    public  static  int queryState()
    {
       // regoPrinter printer;
        MyPrinter printer= MPosApplication.getInstance().getPrinter();
        printer.getObject().CON_PageStart(printer.getState(),false,0,0);
        int state = printer.getObject().CON_QueryStatus(printer.getState());
        Log.i("smile printer","query state: "+state);
        if(state == 0)
        {
            Log.i("smile printer","state ok");
        }
        else if (state == 3)
        {

            Log.i("smile printer","state pe");
        }
        else
        {
            printer.setConected(false);
            Log.i("smile printer","state net");
        }
        printer.getObject().CON_PageEnd(printer.getState(),
                printer.getPrintway());
        return state;
    }
*/
    public  static void printExternalOrder(final  Context context,final SaleOrder saleOrder,final  String pay,final  String oddchange)
    {

//        if( myPrinter ==null )
//        {
//            connectToPrinter();
//            ToastHelper.showToast(context,"打印机未初始化");
//            return;
//        }
//        int state=    queryState();
//        if(state ==3)
//        {
//            ToastHelper.showToast(context,"打印机缺纸");
//            return;
//        }
        if(printerTask != null)
        {
            printerTask.cancel(true);

        }
        final List<String> printlist = new ArrayList<String>();
        getSaleOrderPaperTitle(saleOrder,printlist);
        getSaleOrderPaperContent(saleOrder,printlist);
        if(pay != null)
        {
            getYinShouShishou(saleOrder,pay,oddchange,printlist);
        }
        printerTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {



                try{
//                    if(!myPrinter.isConnected())
//                    {
//                        if(!connectToPrinter())
//                        {
//                            return null;
//                        }
//                    }
                    if(pay !=null)
                            openCashBox();
                   Log.i("smile","list cout"+printlist.size());
                   for(String str : printlist)
                   {
                       Log.i("smileprint",str);
                       printExternal(str);
                   }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
//                printExternal(" ");
//                printExternal(" ");
//                printExternal(" ");
//                printExternal(" ");
//                printExternal(" ");
                return null;
            }
        };
        printerTask.execute();

    }
    public static  void printExternal(String text)
    {
        try {
            PrintHelper1.printString(text);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }
      /*
    public static  void printExternal(String text)
    {
        MyPrinter printer= MPosApplication.getInstance().getPrinter();


        printer.getObject().CON_PageStart(printer.getState(), false,0,0);
       int  state=printer.getObject().CON_QueryStatus(printer.getState());
        Log.i("smile printhelper","state: "+state);
        printer.getObject().CON_PageEnd(printer.getState(),
                printer.getPrintway());
        printer.getObject().CON_PageStart(printer.getState(), false,
                60,
               40);


            int alignType = 0;
            // ���뷽ʽ



            printer.getObject().ASCII_CtrlOppositeColor(printer.getState(),false);
            printer.getObject().ASCII_CtrlAlignType(printer.getState(),alignType);
            //P58A����������ӡ��һ��
            //if(printer.getName().equalsIgnoreCase("RG-P58A"))
            {
                printer.getObject().ASCII_PrintString(printer.getState(),
                        1, 1,
                        1, 0,
                        1, text, "gb2312");
            }

            printer.getObject().ASCII_CtrlFeedLines(printer.getState(), 1);
            printer.getObject().ASCII_CtrlPrintCRLF(printer.getState(), 1);

        printer.getObject().CON_PageEnd(printer.getState(),
                printer.getPrintway());
    }

    public static   void connectDevices(final  Context context)
    {
       initPrinter(context);
        if(myPrinter.isConnected())
        {
            Log.i("smile","device already connected");
            return;
        }
        if(printerTask != null)
        {
            printerTask.cancel(true);

        }
       printerTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                   boolean ret =connectToPrinter();
                    return ret;
                }

           @Override
           protected void onPostExecute(Object o) {
               super.onPostExecute(o);
               if((!Boolean.parseBoolean(o.toString())))
               {
                   ToastHelper.showToast(context,"蓝牙打印机未连接");
               }
           }
       };
            printerTask.execute();
//        connectToPrinter(context);


//      int state=    printer.getObject().CON_ConnectDevices(printer.getName(),getbtMax.get(0),200);
//        if (state > 0) {
//            Log.i("smile","yes bt printer conected");
//            printer.setConected(true);
//            printer.setState(state);
//        }
//        else
//        {
//            printer.setConected(false);
//            Log.i("smile","yes bt printer failed");
//        }
        //  ToastHelper.showToast(context,"蓝牙打印机未连接");
  }

    private  static boolean connectToPrinter()
    {
        MyPrinter printer= myPrinter;
        if(printer==null)
        {
            return false;
        }
        ArrayList<String> getbtNM = (ArrayList<String>) printer.getObject().CON_GetWirelessDevices(0);
        ArrayList<String> getbtName =  new ArrayList<String>();
        ArrayList<String> getbtMax = new ArrayList<String>();
        // �Ի�õ�������ַ����ƽ��в���Զ��Ž��в��
        String name=null;
        String mac = null;
        for (int i = 0; i < getbtNM.size(); i++) {
            String tempName = getbtNM.get(i).split(",")[0];
            if(tempName.toLowerCase().equals("rg-p58a"))
            {

                name = tempName;
                mac = getbtNM.get(i).split(",")[1].substring(0, 17);
            }
         //   getbtName.add();
          //  getbtMax.add(getbtNM.get(i).split(",")[1].substring(0, 17));
        }
        if(mac == null || name ==null)
        {

            Log.i("smile","name: size 0 ");
            return false;
        }
        Log.i("smile","name: "+mac+"  "+name);
        printer.setName(name);
      int state=    printer.getObject().CON_ConnectDevices(printer.getName(),mac,200);
        if (state > 0) {
            Log.i("smile","yes bt printer conected");
            printer.setConected(true);
            printer.setState(state);
            return  true;
        }
        else
        {
            printer.setConected(false);
            Log.i("smile","yes bt printer failed");
            return  false;
        }
    }
    */
//    public   static  void initPrinter(Context context )
//    {
//        if(myPrinter == null)
//            myPrinter= ((MPosApplication) context.getApplicationContext()).getPrinter();
//    }

//    public static  void disConectDevice(Context context)
//    {
//        initPrinter(context);
//        if(myPrinter.isConnected())
//        {
//            myPrinter.getObject().CON_CloseDevices(myPrinter.getState());
//            myPrinter.setConected(false);
//        }
//    }
    public static void openCashBox() {
//        if(myPrinter == null)
//        {
//           return;
//        }
//        myPrinter.getObject().CON_PageStart(myPrinter.getState(), false, 60, 40);
//        myPrinter.getObject().ASCII_CtrlCashDraw(myPrinter.getState());
//        myPrinter.getObject().CON_PageEnd(myPrinter.getState(),
//        myPrinter.getPrintway());
        PrintHelper1.openCashBox();
    }

}
