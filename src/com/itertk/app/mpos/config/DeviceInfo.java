package com.itertk.app.mpos.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
/*
* 设备信息
* */
public class DeviceInfo {
    
    public static final int CAMERA_FACING_BACK = 0;
    public static final int CAMERA_FACING_FRONT = 1;
    public static final int CAMERA_NONE = 2;


    /**
     * 查询手机内非系统应用
     */
    public static List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = (PackageInfo) paklist.get(i);
            // 判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    // 获取device的os version
    public static String getosVersion() {
        String version = android.os.Build.VERSION.RELEASE;
        return "Andrid " + version;
    }
    
    public static String getAppVersionName(Context context) {    
        String versionName = "";    
       try {    
            // ---get the package info---    
           PackageManager pm = context.getPackageManager();    
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);    
            versionName = pi.versionName;    
            //versioncode = pi.versionCode;  
            if (versionName == null || versionName.length() <= 0) {    
                return "";    
            }    
        } catch (Exception e) {    
            Log.e("VersionInfo", "Exception", e);    
        }    
        return versionName;    
    }  
    
    
    private static String[] getCpuInfo() {  
        String str1 = "/proc/cpuinfo";  
        String str2="";  
        String[] cpuInfo={"",""};  
        String[] arrayOfString;  
        try {  
            FileReader fr = new FileReader(str1);  
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);  
            str2 = localBufferedReader.readLine();  
            arrayOfString = str2.split("\\s+");  
            for (int i = 2; i < arrayOfString.length; i++) {  
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";  
            }  
            str2 = localBufferedReader.readLine();  
            arrayOfString = str2.split("\\s+");  
            cpuInfo[1] += arrayOfString[2];  
            localBufferedReader.close();  
        } catch (IOException e) { 
            e.printStackTrace();
        }  
        return cpuInfo;  
    }  
    
    public static String getCpuXingHao(){
        String[] cpuInfo={"",""};
        cpuInfo= getCpuInfo();
        return cpuInfo[0];
    }
    
    public static String getCpuPingLv(){
        String[] cpuInfo={"",""};
        cpuInfo= getCpuInfo();
        return cpuInfo[1];
    }


  
    public static String getTotalMemory(Context mContext) {  
        String str1 = "/proc/meminfo";// 系统内存信息文件  
        String str2;  
        String[] arrayOfString;  
        String memString = "";
        long initial_memory = 0;  
  
        try {  
            FileReader localFileReader = new FileReader(str1);  
            BufferedReader localBufferedReader = new BufferedReader(  
                    localFileReader, 8192);  
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小  
  
            //arrayOfString =str2.split(":");// str2.split("//s+");  
           /* if (arrayOfString.length == 1) {
                arrayOfString = str2.split(":");
            }*/
            
           
            String str="\\d*";
            Pattern pattern=Pattern.compile(str);
            Matcher matcher=pattern.matcher(str2);
            while(matcher.find()){
                if (matcher.group() != null && matcher.group().length() > 0) {
                    memString =matcher.group();
                    break;
                }
            }
  
            initial_memory = Integer.valueOf(memString) * 1024;// 获得系统总内存，单位是KB 
            localBufferedReader.close();  
  
        } catch (IOException e) {  
            e.printStackTrace();
        } catch (Exception e) {
           e.printStackTrace();
        } 
        return Formatter.formatFileSize(mContext, initial_memory);// Byte转换为KB或者MB，内存大小规格化  
    }  
    
    /** 
     * 获得机身内存总大小 
     *  
     * @return 
     */  
    public static String getRomTotalSize(Context cx) {  
        File path = Environment.getDataDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return Formatter.formatFileSize(cx, blockSize * totalBlocks);  
    } 
    
    /** 
     * 获得SD卡总大小 
     *  
     * @return 
     */  
    public static String getSDTotalSize(Context cx) {  
        File path = Environment.getExternalStorageDirectory();  
        StatFs stat = new StatFs(path.getPath());  
        long blockSize = stat.getBlockSize();  
        long totalBlocks = stat.getBlockCount();  
        return Formatter.formatFileSize(cx, blockSize * totalBlocks);  
    }  

    
    public static boolean isSupportBlueTooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        adapter = null;
        return true;
    }
    

    /**
     * 检测摄像头是否存在
     * 
     * @param context
     * @return
     */
    public static boolean checkCameraHardware(Context context) {
/*        if (null != context) {
            return context.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_ANY);
        }
        return false;*/
        Camera localCamera=null;
        try {
            localCamera=Camera.open();
        }catch (Exception e) {
            return  false;
        }finally {
            if(localCamera!=null){
                localCamera.release();
            }
        }
        return true;
        
    }
    
    //Carmera
    public static boolean HasBackCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACING_BACK) {
                return true;
            }
        }
        return false;
    }

    public static boolean HasFrontCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CAMERA_FACING_FRONT) {
                return true;
            }
        }
        return false;
    }
    
    public static String getCameraPixels(int paramInt) {
        if (paramInt == 2)
            return "无";
        if(!HasBackCamera()){
            if(!HasFrontCamera()){
                return "无";
            }else
            {
                paramInt=CAMERA_FACING_FRONT;
            }
        }
        Camera localCamera = Camera.open(paramInt);
        Camera.Parameters localParameters = localCamera.getParameters();
        localParameters.set("camera-id", 1);
        List<Size> localList = localParameters.getSupportedPictureSizes();
        
        String focusModeStr = "";
        List<String> focusMode = localParameters.getSupportedFocusModes();
        if (focusMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            focusModeStr = "自动对焦";
        }
        if (localList != null) {
            int heights[] = new int[localList.size()];
            int widths[] = new int[localList.size()];
            for (int i = 0; i < localList.size(); i++) {
                Size size = (Size) localList.get(i);
                int sizehieght = size.height;
                int sizewidth = size.width;
                heights[i] = sizehieght;
                widths[i] = sizewidth;
            }
            
            int pixels = getMaxNumber(heights) * getMaxNumber(widths);
            localCamera.release();
            return String.valueOf(pixels / 10000) + "万像素" + focusModeStr;

        } else
            return "无";

    }

    private static int getMaxNumber(int[] paramArray) {
        int temp = paramArray[0];
        for (int i = 0; i < paramArray.length; i++) {
            if (temp < paramArray[i]) {
                temp = paramArray[i];
            }
        }
        return temp;
    }
    
    //屏幕
    public static String getDisplayMetrics(Context cx) {
        String str = "";
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
        double screenSize = diagonalPixels / (160 * dm.density); 
        str += (int)Math.ceil(screenSize) + "英寸 分辨率："; 
        str +=  String.valueOf(screenWidth) + "*";
        str +=  String.valueOf(screenHeight);
        return str;
    }
}
