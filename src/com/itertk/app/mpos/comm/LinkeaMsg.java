package com.itertk.app.mpos.comm;

import android.util.Log;

import com.itertk.app.mpos.dbhelper.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.System;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/7/30.
 *消息基类
 */
public class LinkeaMsg {
    AsyncHttpClient client;
    static final String encoding = HTTP.UTF_8;

    //��for debug
//    static final String url = "http://183.129.252.30:7002/opengw/router/rest.htm";
//    static final String secretKey = "d5b9853a-7f21-486d-ab17-82f21e8893e7";
//    final String app_key = "100050";

    //���for�product release
    static final String url = "http://gateway.ringou.cn/opengw/router/rest.htm";
    static final String secretKey = "444962e9-e290-47aa-a1cf-ac1a7dbe7c01";
    final String app_key = "100002";

    //final String pos_id = "90500000000012";

    protected Map<String, String> params;

    public LinkeaMsg(AsyncHttpClient client) {
        this.client = client;
        params = new HashMap<String, String>();
        params.put("v", "1.1");
        params.put("format", "json");
        params.put("app_key", app_key);
        params.put("sign_method", "md5");
        params.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

    @Override
    public String toString() {
        return buildParams().toString();
    }

    public void send(AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams requestParams = buildParams();
        Log.d("send---->",  requestParams.toString());
        Log.d("send---->", client.getUrlWithQueryString(true, url, requestParams));

        client.post(url, requestParams, asyncHttpResponseHandler);
    }

    private RequestParams buildParams() {
        RequestParams requestParams = new RequestParams();
        //requestParams.setContentEncoding(encoding);

        List<Map.Entry<String, String>> paramsList = new ArrayList<Map.Entry<String, String>>(params.entrySet());

        Collections.sort(paramsList, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        String signContent = secretKey;

        for (Map.Entry item : paramsList) {

//            if(item.getValue() ==null)
//            {
//                Log.d("smile","key: "+item.getKey().toString());
//                continue;
//            }

            requestParams.add(item.getKey().toString(), item.getValue()==null?"":item.getValue().toString());
            String keyString = item.getKey().toString();
            if (keyString.equals("business_img")
                    || keyString.equals("id_img")
                    || keyString.equals("head_img")
                    || keyString.equals("account_img")
                    || keyString.equals("org_img")
                    || keyString.equals("tax_img")
                    || keyString.equals("accountImg")
                    || keyString.equals("businessImg")
                    || keyString.equals("headImg")
                    || keyString.equals("idImg")
                    || keyString.equals("orgImg")
                    || keyString.equals("taxImg")
                    || keyString.equals("bankImg")
                    || keyString.equals("paySignImg")
                    )
                continue;
            String val = item.getValue()==null?"":item.getValue().toString();
            signContent += (item.getKey().toString() + val);
            Log.d("sign msg=", item.getKey().toString());
        }

        signContent += secretKey;




        String sign = getMD5Digest(signContent);

        requestParams.add("sign", sign);


        //Log.d("requestparam=", requestParams.toString());

        return requestParams;
    }



    private String getMD5Digest(String signContent) {
        byte[] digest = null;
        try {
           // Log.d("signcontent", signContent);
            digest = MessageDigest.getInstance("MD5").digest(signContent.getBytes(encoding));
        } catch (Exception e) {
            Log.e("md5", e.toString());
        }


        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < digest.length; i++) {
            if (Integer.toHexString(0xFF & digest[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & digest[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & digest[i]));
        }
        Log.d("md5", md5StrBuff.toString().toUpperCase());
        return md5StrBuff.toString().toUpperCase();
    }


}
