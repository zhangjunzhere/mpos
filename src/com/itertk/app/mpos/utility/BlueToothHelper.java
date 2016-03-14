package com.itertk.app.mpos.utility;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by jz on 2015/3/30.
 * 蓝牙帮助类
 */
public class BlueToothHelper {
    public static   void enableBlueTooth()
    {
        BluetoothAdapter bta  =BluetoothAdapter.getDefaultAdapter();
        if(!bta.isEnabled())
        {
            bta.enable();
        }

    }
    public static   void disableBlueTooth()
    {
        BluetoothAdapter bta  =BluetoothAdapter.getDefaultAdapter();
        if(bta.isEnabled())
        {
            bta.disable();
        }

    }
    public static void enableBlueToothOpenSetting(Activity context)
    {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivityForResult(intent, Activity.RESULT_OK);
    }
    public  static  boolean blueToothEnable()
    {
        BluetoothAdapter bta  =BluetoothAdapter.getDefaultAdapter();
        return bta.isEnabled();
    }

    public static   boolean isME30Connected()
    {
        BluetoothAdapter bta  =BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = bta.getBondedDevices();
        for(BluetoothDevice b: devices)
        {

            Log.i("smile", b.getName() + "    " + b.getAddress()+" type: "+b.getType());
            if(b.getName().toLowerCase().contains("me30"))
            {
               // connect(b);
                    return true;
            }
        }

        return false;
    }
    public  static  void getConectedDevice(Context context)
    {
        BluetoothManager bm = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
       List<BluetoothDevice> list=  bm.getConnectedDevices(BluetoothProfile.GATT);
        Log.i("Smile","getConectedDevice: "+list.size());
    }

    public static  void getProfile(Context context,BluetoothProfile.ServiceListener serviceListener)
    {
        BluetoothAdapter bta  =BluetoothAdapter.getDefaultAdapter();
        bta.getProfileProxy(context,serviceListener,BluetoothProfile.HEADSET);

    }

    public static   boolean connectToME30()
    {
        BluetoothAdapter bta  =BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = bta.getBondedDevices();
        for(BluetoothDevice b: devices)
        {
            Log.i("smile", b.getName() + "    " + b.getAddress());
            if(b.getName().equals("ME30"))
            {
                //connect(b);
                return  false;
            }
        }

        return false;
    }
    public  static  void connect(BluetoothDevice device)
    {
        final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
        UUID uuid = UUID.fromString(SPP_UUID);
        try {
        BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);

            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
