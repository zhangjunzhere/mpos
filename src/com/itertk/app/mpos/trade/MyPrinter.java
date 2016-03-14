package com.itertk.app.mpos.trade;

import android.content.Context;

import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.utility.preDefiniation;

import rego.printlib.export.regoPrinter;


/**
 * Created by smile_gao on 2015/3/18.
 * 打印机
 */
public class MyPrinter {
    private regoPrinter printer;
    private int myState=0;
    private String printName="RG-MTP58B";
    private preDefiniation.TransferMode printmode = preDefiniation.TransferMode.TM_NONE;
    // private boolean labelmark=false;
    private boolean labelmark = true;
    private boolean bConnect =false;
    public  MyPrinter(Context context)
    {
        setObject(context);
    }

    public regoPrinter getObject() {
        return printer;
    }

    public void setObject(Context context) {
        printer = new regoPrinter(context);
    }
    public boolean isConnected()
    {
        return  bConnect;
    }
    public void setConected(boolean connected)
    {
        bConnect = connected;
    }
    public String getName() {
        return printName;
    }

    public void setName(String name) {
        printName = name;
    }
    public void setState(int state) {
        myState = state;
    }

    public int getState() {
        return myState;
    }
    public void setPrintway(int printway) {

        switch (printway) {
            case 0:
                printmode = preDefiniation.TransferMode.TM_NONE;
                break;
            case 1:
                printmode = preDefiniation.TransferMode.TM_DT_V1;
                break;
            default:
                printmode = preDefiniation.TransferMode.TM_DT_V2;
                break;
        }

    }

    public int getPrintway() {
        return printmode.getValue();
    }


    public boolean getlabel() {
        return labelmark;
    }

    public void setlabel(boolean labelprint) {
        labelmark = labelprint;
    }


}
