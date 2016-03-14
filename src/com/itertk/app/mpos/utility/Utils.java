package com.itertk.app.mpos.utility;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.AddressDao;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.dbhelper.UserDao;
import com.itertk.app.mpos.trade.convenience.BalanceQueryActivity;
import com.itertk.app.mpos.trade.convenience.ConvenienceActivity;
import com.itertk.app.mpos.trade.convenience.PhoneChargeActivity;
import com.itertk.app.mpos.trade.convenience.applycreditcard.BankChoiceActivity;
import com.itertk.app.mpos.trade.more.MoreHomeActivity;
import com.itertk.app.mpos.trade.pos.BillActivity;
import com.itertk.app.mpos.trade.pos.PosActivity;
import com.itertk.app.mpos.trade.taobao.GoodsOrderActivity;

import org.apache.http.util.EncodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by smile_gao on 2015/3/12.
 * 工具类，集成了一些常用的函数
 */
public class Utils {
	private static HashMap<String, Class<?>> mClassMaps;
	private static HashMap<String, Integer> mBankIcon;

	public static HashMap<String, Class<?>> getPermissionClassMaps() {
		if (mClassMaps == null) {
			mClassMaps = new HashMap<String, Class<?>>();
			mClassMaps.put("超级收银台", PosActivity.class);
			mClassMaps.put("便民服务", ConvenienceActivity.class);
			mClassMaps.put("订货系统", GoodsOrderActivity.class);
			mClassMaps.put("更多服务", MoreHomeActivity.class);

			mClassMaps.put("手机充值", PhoneChargeActivity.class);
			mClassMaps.put("余额查询", BalanceQueryActivity.class);
			mClassMaps.put("收银", PosActivity.class);
			mClassMaps.put("台账", BillActivity.class);
			mClassMaps.put("信用卡申请", BankChoiceActivity.class);
		}
		return mClassMaps;
	}

	public static HashMap<String, Integer> getBankIconMaps() {
		if (mBankIcon == null) {
			mBankIcon = new HashMap<String, Integer>();
			mBankIcon.put("花旗银行", R.drawable.bankicon_huaqi);
			mBankIcon.put("包商银行", R.drawable.bankicon_baoshang);
			mBankIcon.put("中国银行", R.drawable.bankicon_zhongguo);
			mBankIcon.put("工商银行", R.drawable.bankicon_gongshang);
			mBankIcon.put("农业银行", R.drawable.bankicon_nongye);
			mBankIcon.put("北京银行", R.drawable.bankicon_beijing);
			mBankIcon.put("广发银行", R.drawable.bankicon_guangfa);
			mBankIcon.put("光大银行", R.drawable.bankicon_guangda);
			mBankIcon.put("华夏银行", R.drawable.bankicon_huaxia);
			mBankIcon.put("平安银行", R.drawable.bankicon_pingan);
			mBankIcon.put("民生银行", R.drawable.bankicon_minsheng);
			mBankIcon.put("招商银行", R.drawable.bankicon_zhaoshang);
			mBankIcon.put("浦发银行", R.drawable.bankicon_pufa);
			mBankIcon.put("中信银行", R.drawable.bankicon_zhongxin);
			mBankIcon.put("交通银行", R.drawable.bankicon_jiaotong);
			mBankIcon.put("兴业银行", R.drawable.bankicon_xingye);
			mBankIcon.put("建设银行", R.drawable.bankicon_jianshe);
		}
		return mBankIcon;
	}

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static  String formatPrice(Context context, String price){
        String strPrice = (new DecimalFormat("0.00")).format(Arith.newBigDecimal(price));
        return String.format(context.getResources().getString(R.string.product_price),strPrice);
    }
    public static  String formatPriceNoSympol(Context context, String price){
        String strPrice = (new DecimalFormat("0.00")).format(Arith.newBigDecimal(price));
        return String.format("%s",strPrice);
    }
    public static  String formatPriceNoSympol(Context context, float price){
        String strPrice = (new DecimalFormat("0.00")).format(price);
        return String.format("%s",strPrice);
    }

    public static boolean isLeapYear(int year)
    {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ;
    }

    public static  String formatPrice(Context context, float price){
        String strPrice = (new DecimalFormat("0.00")).format(price);
        return String.format(context.getResources().getString(R.string.product_price),strPrice);
    }

    public static String formatDate(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }






    public static String convertToMD5(String strSource) {
        // Log.d(TAG, "Wifi Mac Address: " + strSource);
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] byteArray = strSource.getBytes();
        byte[] md5Bytes = md5.digest(byteArray);;

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    public static  Uri setOutImageUri() {
        // Store image in dcim
        String dateStr=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File file = new File("/sdcard/mpos/","mpos_"+dateStr+ ".jpg");
        if(!file.exists()){
            File parentFile=file.getParentFile();
            parentFile.mkdirs();
        }
        Uri uriMyImage = Uri.fromFile(file);
        tempUri=uriMyImage;

        return uriMyImage;
    }
    public static Uri tempUri;

    public static void launchCamera(Activity activity,int code,boolean Camera){
        tempUri=null;
        if(Camera) {
            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setOutImageUri());
            activity.startActivityForResult(intent, code);

        }else {
            Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
            getAlbum.setType("image/*");
            try {
                activity.startActivityForResult(Intent.createChooser(getAlbum, "请选择一个要上传的文件"), code);
            }catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(activity, "请安装文件管理器", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    public static void launchCamera(Fragment fragment,int code,boolean Camera){
        tempUri=null;
        if(Camera) {
            final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setOutImageUri());
            fragment.startActivityForResult(intent, code);

        }else {
            Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
            getAlbum.setType("image/*");
            try {
                fragment.startActivityForResult(Intent.createChooser(getAlbum, "请选择一个要上传的文件"), code);
            }catch (android.content.ActivityNotFoundException ex){
                Toast.makeText(fragment.getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public static Bitmap getBmFromData(Activity activity,Intent data,int requestCode) {
        Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = activity.getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个


            try {
                Uri originalUri;
                if (data != null) {
                    originalUri = data.getData();        //获得图片的uri
                } else {
                    originalUri = tempUri;
                }
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);

            } catch (IOException e) {

                Log.e("TAG-->Error", e.toString());

            }
        return bm;
    }

    public  static  Uri getDataUri(Intent data,int requestCode){
        Uri originalUri=null;
        if (data != null) {
            originalUri = data.getData();        //获得图片的uri
        }else {
            originalUri = tempUri;
        }
        return  originalUri;
    }

    public static void saveInfo(Context context,String key,boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static boolean getInfo(Context context,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean info =prefs.getBoolean(key,false);
        return info;
    }

    public static void saveStringInfo(Context context,String key,String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getStringInfo(Context context,String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String info =prefs.getString(key,"");
        return info;
    }

    public static void setPricePoint(final EditText editText) {
        editText.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s+"00";
                    editText.setText(s);
                    editText.setSelection(s.length());
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });

    }

    public static Long getMemberId( ){
        final MPosApplication mPosApplication  = MPosApplication.getInstance();
        UserDao userDao = mPosApplication.getDataHelper().getUserManager();
        List<User> member = userDao.queryBuilder().where(UserDao.Properties.Type.eq(0L)).list();

        return member.get(0).getClerkNo();
    }
    public static boolean phoneRegex(String text){
        String telRegex = "^1\\d{10}$";
        return text.matches(telRegex);
    }
    public static InputFilter[] getLengthFilters(final  Context context, final int max_len)
    {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(max_len){
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                int destLen = dest.length();
                int sourceLen = source.length();
                if(destLen + sourceLen > max_len){
                   // ToastHelper.showToast(context, "长度不能超过11位");
                    return "";
                }
                return source;
            }
        };
        return filters;
    }
    public static String IDCardValidate(String IDStr) throws ParseException {
        String errorInfo = "";// 记录错误信息
        String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4",
                "3", "2" };
        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2" };
        String Ai = "";
        // ================ 号码的长度 15位或18位 ================
        if (IDStr.length() != 15 && IDStr.length() != 18) {
            errorInfo = "身份证号码长度应该为15位或18位。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 数字 除最后以为都为数字 ================
        if (IDStr.length() == 18) {
            Ai = IDStr.substring(0, 17);
        } else if (IDStr.length() == 15) {
            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
        }
        if (isNumeric(Ai) == false) {
            errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
            return errorInfo;
        }
        // =======================(end)========================

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);// 年份
        String strMonth = Ai.substring(10, 12);// 月份
        String strDay = Ai.substring(12, 14);// 月份
        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {
            errorInfo = "身份证生日无效。";
            return errorInfo;
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
                || (gc.getTime().getTime() - s.parse(
                strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
            errorInfo = "身份证生日不在有效范围。";
            return errorInfo;
        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
            errorInfo = "身份证月份无效";
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = "身份证日期无效";
            return errorInfo;
        }
        // =====================(end)=====================

        // ================ 地区码时候有效 ================
        Hashtable h = GetAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = "身份证地区编码错误。";
            return errorInfo;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IDStr.length() == 18) {
            if (Ai.equals(IDStr) == false) {
                errorInfo = "身份证无效，不是合法的身份证号码";
                return errorInfo;
            }
        } else {
            return "";
        }
        // =====================(end)=====================
        return "";
    }

    /**
     * 功能：判断字符串是否为数字
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：设置地区编码
     * @return Hashtable 对象
     */
    private static Hashtable GetAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    /**验证日期字符串是否是YYYY-MM-DD格式
     * @param str
     * @return
     */
    public static boolean isDataFormat(String str){
        boolean flag=false;
        //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
        String regxStr="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
        Pattern pattern1=Pattern.compile(regxStr);
        Matcher isNo=pattern1.matcher(str);
        if(isNo.matches()){
            flag=true;
        }
        return flag;
    }


    //smile_gao add for get wifi ip
    public static  String getWifiIp(Context context)
    {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return  ip;
    }
    private  static String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }
    //end smile_gao

    public static String getConsignAddress(){
        MPosApplication mPosApplication = MPosApplication.getInstance();
        DataHelper dbHelper = mPosApplication.getDataHelper();

        String addressStr = "";
        AddressDao addressDao = dbHelper.getDaoSession().getAddressDao();
        List<com.itertk.app.mpos.dbhelper.Address> addresses = addressDao.queryBuilder().where(AddressDao.Properties.Moren.eq(true)).list();
        if(addresses.size() <= 0){
            addresses = addressDao.loadAll();
        }
        if(addresses.size() > 0){
            addressStr = addresses.get(0).getAddress1();
        }
        return addressStr;
    }

    public static User getMemberFromDB(Context context){
        final MPosApplication mPosApplication = MPosApplication.getInstance();
        UserDao userDao = mPosApplication.getDataHelper().getUserManager();
        List<User> users = userDao.queryBuilder().where(UserDao.Properties.Type.eq(DataHelper.USER_TPYE_MEMBER)).list();

        User member = null;
        if(users.size()  > 0 ){
            member = users.get(0);
            member.setLoginPwd(AESEncryptor.getInstance(context).decryption(member.getLoginPwd()));
        }

        return member;
    }

    public static Boolean isMemberLogin(){
        final MPosApplication mPosApplication = MPosApplication.getInstance();
        return mPosApplication.getMember() != null;
    }

    public static long getTime()
    {
      return   (new Date()).getTime();
    }


    public static int getVersionCode(Context context){
        int versionCode  = 2013031401;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前应用的版本名称
            String versionName = info.versionName;
            // 当前版本的版本号
             versionCode = info.versionCode;
            // 当前版本的包名
            String packageNames = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static Date loadPosSignDate(Context context) {
        SharedPreferences sharedPre = context.getSharedPreferences("possign", Context.MODE_PRIVATE);
        long date = sharedPre.getLong("date", 0);
        Date dt = new Date();
        dt.setTime(date);
        return dt;
        // dt.setTime(date%1000*1000);
        // Date dt1 = new Date(dt.getYear(),dt.getMonth(),dt.getDay());
    }

    public static void setPosSignDate(Context context, long dt) {
        SharedPreferences sharedPre = context.getSharedPreferences("possign", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putLong("date", dt);
        editor.commit();
        //(new Date()).getTime();
    }
   public static String genOrderTradeNo()
   {
       String dateStr=new SimpleDateFormat("yyMMddHHmmss").format(new Date());
       String termid =  MPosApplication.getInstance().getMsgBuilder().getTerm_id();
       if(termid.length()>0)
       {
           termid = termid.substring(1);
       }
       String tradeno = dateStr+termid;
       return tradeno;
   }
   public static String convertBimapToBase64(Bitmap bitmap)
   {
       String string = null;
       try {
           ByteArrayOutputStream bStream = new ByteArrayOutputStream();
           bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bStream);
           byte[] bytes = bStream.toByteArray();
           string = Base64.encodeToString(bytes, Base64.DEFAULT);
       } catch (Exception e) {
           Log.e("convertBimapToBase64", e.toString());
       }
       return string;
   }

    public static  Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }


    //写数据
    public static void writeFile(Context context, String fileName,String writestr){
        String TAG = "writeFile";
        try{
            FileOutputStream fout =context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte [] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    //读数据
    public static String readFile(Context context, String fileName) {
        String TAG = "readFile";
        String res="";
        try{
            FileInputStream fin = context.openFileInput(fileName);
            int length = fin.available();
            byte [] buffer = new byte[length];
            fin.read(buffer);
//            res = EncodingUtils.getString(buffer, "UTF-8");
            res = new String(buffer) ;
            fin.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }


}
