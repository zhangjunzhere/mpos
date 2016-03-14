package com.itertk.app.mpos.config;

import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.login.LoginActivity;


/**
 * A simple {@link Fragment} subclass.
 *
 */

public class DeviceFragment extends Fragment {


    public DeviceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device, container, false);
    }
    

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPre = getActivity().getSharedPreferences(LoginActivity.terminalConfig, getActivity().MODE_PRIVATE);
        String posID = sharedPre.getString(LoginActivity.keyPosId, "");
        String terminalId = sharedPre.getString(LoginActivity.keyTerminalId, "");
        String terminalMac = sharedPre.getString(LoginActivity.keyTerminalMac, "");

        TextView textpOS = (TextView)view.findViewById(R.id.textposID);
        textpOS.setText(posID);
        TextView textTermID = (TextView)view.findViewById(R.id.textTermID);
        textTermID.setText(terminalId);
        TextView textTermMAC = (TextView)view.findViewById(R.id.texttermMAC);
        textTermMAC.setText(terminalMac);

    
        TextView textTerminalType = (TextView)view.findViewById(R.id.textTerminalType);
        textTerminalType.setText(DeviceInfo.getosVersion());
        
        TextView textProcessor = (TextView)view.findViewById(R.id.textProcessor);
        textProcessor.setText(DeviceInfo.getCpuXingHao());
        
        TextView textDisplayScreen = (TextView)view.findViewById(R.id.textDisplayScreen);
        textDisplayScreen.setText(DeviceInfo.getDisplayMetrics(getActivity()));
        
        TextView textInternalMemory = (TextView)view.findViewById(R.id.textInternalMemory);
        textInternalMemory.setText(DeviceInfo.getTotalMemory(getActivity()) );
        
        TextView textStorage = (TextView)view.findViewById(R.id.textStorage);
        textStorage.setText(DeviceInfo.getRomTotalSize(getActivity()) + "(内置闪存)");
        
        
      
        TextView textBlueTooth = (TextView)view.findViewById(R.id.textBlueTooth);
        String blueToothStr = DeviceInfo.isSupportBlueTooth() ?"支持蓝牙连接": "不支持蓝牙连接";
        textBlueTooth.setText(blueToothStr);
     
        TextView textCarema = (TextView)view.findViewById(R.id.textCarema);
        if(DeviceInfo.checkCameraHardware(getActivity())) {
            textCarema.setText(DeviceInfo.getCameraPixels(DeviceInfo.CAMERA_FACING_BACK));
        }else
        {
            textCarema.setText("设备无摄像头");
        }
        
        
    }
    
    public  void test(Context cx){
        //http://stackoverflow.com/questions/8624763/how-to-get-wifi-standard
        WifiManager wifiManager =  (WifiManager)cx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            Integer linkSpeed = wifiInfo.getLinkSpeed(); //measured using WifiInfo.LINK_SPEED_UNITS
        }
    }



}
