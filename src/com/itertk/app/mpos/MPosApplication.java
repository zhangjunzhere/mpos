package com.itertk.app.mpos;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.display.DisplayManager;
import android.hardware.input.InputManager;
import android.location.Address;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.itertk.app.mpos.card51comm.LinkeaMsg51CardBuilder;
import com.itertk.app.mpos.card51dbhelper.LocationCard51Helper;
import com.itertk.app.mpos.comm.LinkeaMsgBuilder;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.locationhelper.LocationHelper;
import com.itertk.app.mpos.service.OnlineService;
import com.itertk.app.mpos.trade.MyPrinter;
import com.nostra13.universalimageloader.sample.UILApplication;


/**
 * Created by Administrator on 2014/7/2.
 * 主程序
 */
@SuppressLint("NewApi")
public class MPosApplication extends Application implements InputManager.InputDeviceListener{
    private static final String TAG = "MPosApplication";
    private Intent onlineService;
    private boolean onlineState = true;


    private static MPosApplication sMPosApplication;

    private LocationHelper locationHelper;
    private LocationCard51Helper locationCard51Helper;
    private DataHelper dataHelper;
    private LinkeaMsgBuilder linkeaMsgBuider;
    private LinkeaMsg51CardBuilder linkeaMsg51CardBuilder;
    private LinkeaResponseMsg.LoginResponseMsg member;

    private String posId;
    private String trackKey;
    //smile
    private String macKey;
    private String pinKey;
    private String mainKey;

    private ExternalPos me30;
    private ItertkPos itertkPos;
    private BBPos bbPos;
    private MyTTS myTTS;
    private Display externDisplay;

    //smile
    MyPrinter printer;
    private Address mAddress;

    private User mCurrentUsr;
    private MyActivityManager myActivityManager;
    public static MPosApplication getInstance(){
        return sMPosApplication;
    }

    public DataHelper getDataHelper(){ return dataHelper;}

    public LocationHelper getLocationHelper(){
        if(locationHelper == null) locationHelper = new LocationHelper(this);
        return locationHelper;
    }
	public LocationCard51Helper getLocationCard51Helper() {
		if (locationCard51Helper == null) {
			locationCard51Helper = new LocationCard51Helper(this);
		}
		return locationCard51Helper;
	}
	public LinkeaMsg51CardBuilder get51CardMsgBuilder() {
		return linkeaMsg51CardBuilder;
	}
    public LinkeaMsgBuilder getMsgBuilder(){ return linkeaMsgBuider;}

    private String serialNum;

    InputManager inputManager;
	// zyf 这里写成空字符串窜
	private String cityName = "";
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

    //MyPresentation myPresentation;

//    private  String getCPUSerial() {
//        String str = "", strCPU = "", cpuAddress = "0000000000000000";
//        try {
//            //读取CPU信息
//            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
//            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
//            LineNumberReader input = new LineNumberReader(ir);
//            //查找CPU序列
//            for (int i = 1; i < 100; i++) {
//                str = input.readLine();
//                if (str != null) {
//                    //查找到序列号
//                    if (str.indexOf("Serial") > -1) {
//                        //提取序列
//                        strCPU = str.substring(str.indexOf(":") + 1,
//                                str.length());
//                        //去空
//                        cpuAddress = strCPU.trim();
//                        break;
//                    }
//                } else {
//                    //文件结尾
//                    break;
//                }
//            }
//        } catch (Exception ex) {
//            //赋予默认
//            ex.printStackTrace();
//        }
//        return cpuAddress;
//    }

    private void loadPosId(){
        SharedPreferences sharedPre = getSharedPreferences("terminalconfig", MODE_PRIVATE);
        posId = sharedPre.getString("posid", "");
    }

    private void loadTrackKey(){
        SharedPreferences sharedPre = getSharedPreferences("key", MODE_PRIVATE);
        trackKey = sharedPre.getString("track", "");
        macKey = sharedPre.getString("mac", "");
        pinKey = sharedPre.getString("pin", "");

    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        sMPosApplication = this;
        SDKInitializer.initialize(this);

        UILApplication uilApplication = new UILApplication();
        uilApplication.onCreate(this);



        loadPosId();
        loadTrackKey();


        dataHelper = new DataHelper(this);
        dataHelper.getDaoSession();
        //locationHelper = new LocationHelper(this);

		linkeaMsg51CardBuilder = new LinkeaMsg51CardBuilder();
        linkeaMsgBuider = new LinkeaMsgBuilder();
        onlineService = new Intent(this,  OnlineService.class);
        onlineService.setAction("com.itertk.app.mpos.action.CheckOnline");
        //startService(onlineService);


        me30 = new NewLandME30(this);
        //itertkPos = new  ItertkPos("/dev/ttyS3", 9600);

        bbPos = new BBPos(this);

        serialNum = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        myTTS = new MyTTS(this);

        inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        inputManager.registerInputDeviceListener(this, null);

        //smile_gao add for activity manager
        myActivityManager = new MyActivityManager();
        //end

        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

        Log.d(TAG, "" + presentationDisplays.length);

        if (presentationDisplays.length > 0) {
            // If there is more than one suitable presentation display, then we could consider
            // giving the user a choice.  For this example, we simply choose the first display
            // which is the one the system recommends as the preferred presentation display.
            externDisplay = presentationDisplays[0];
        }
    }

    public MyPresentation showExternalAd(Context context){
        if(externDisplay != null){
            MyPresentation myPresentation  = new MyPresentation(context, externDisplay);
            myPresentation.randShow();
            return myPresentation;
        }else{
            return null;
        }

    }

    public void switchExternalAD(MyPresentation myPresentation){
        if(myPresentation != null){
            myPresentation.randShow();
        }
    }

    public void destroyExternalAD(MyPresentation myPresentation){
        if (myPresentation != null){
            myPresentation.cancel();
        }
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate");
        stopService(onlineService);

        super.onTerminate();
    }

    public boolean isOnline() {
        return onlineState;
    }

    public void setOnlineState(boolean onlineState) {
        Log.d(TAG, onlineState? "online" : "offline");
        this.onlineState = onlineState;
    }

    public LinkeaResponseMsg.LoginResponseMsg getMember() {
        return member;
    }

    public void setMember(LinkeaResponseMsg.LoginResponseMsg member) {
        this.member = member;
    }


    public BBPos getBbPos() {
        return bbPos;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public MyTTS getMyTTS() {
        return myTTS;
    }

    public void setMyTTS(MyTTS myTTS) {
        this.myTTS = myTTS;
    }

    @Override
    public void onInputDeviceAdded(int deviceId) {
        Log.d(TAG,  "device add " + deviceId);

        if(inputManager == null) return;
        InputDevice inputDevice =inputManager.getInputDevice(deviceId);
        if(inputDevice == null) return;
        Log.d(TAG,  "inputDevice name " + inputDevice.getName());
        if(inputDevice.getName().contains("HID")){
                Log.d(TAG, "gun load " + inputDevice.toString());
            Toast.makeText(this, "检测到扫描枪，请关闭物理键盘", Toast.LENGTH_LONG).show();
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showInputMethodPicker();

        }
    }

    @Override
    public void onInputDeviceRemoved(int deviceId) {
        Log.d(TAG,  "device remove " + deviceId);
    }

    @Override
    public void onInputDeviceChanged(int deviceId) {

    }

    public Display getExternDisplay() {
        return externDisplay;
    }


    public ItertkPos getItertkPos() {
        return itertkPos;
    }

    public ExternalPos getPos() {
        return me30;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getTrackKey() {
        return trackKey;
    }

    public void setTrackKey(String trackKey) {
        this.trackKey = trackKey;
    }
    //smile
    public String getMacKey() {
        return macKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }
    public String getPinKey() {
        return pinKey;
    }

    public void setPinKey(String pinKey) {
        this.pinKey = pinKey;
    }

    public void setMainKey(String mainKey)
    {
        this.macKey = mainKey;
    }
    public  String getMainKey()
    {
        return macKey;
    }

    public MyPrinter getPrinter()
    {
        return printer;
    }
    public  void initPrinter()
    {
        if(printer == null)
              printer = new MyPrinter(this);
    }

    public void setAddress(Address address){mAddress = address;}
    public Address getAddress(){return  mAddress;}

    public void setCurUser(User user){mCurrentUsr=user;}
    public User getCurUser(){return mCurrentUsr;}

    public  MyActivityManager getMyActivityManager()
    {
        return myActivityManager;
    }
}
