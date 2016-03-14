package com.itertk.app.mpos.card51comm;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 51信用卡通讯
 * @author zyf
 * 
 */
public class Msg51Card {

	static final String QUERY_PROVINCE = "province.ashx";
	static final String QUERY_CITYAREA = "cityarea.ashx";
	static final String QUERY_CITYBANK = "citybank.ashx";
	static final String APPLY_TRADE = "appytrade.ashx";
	static final String QUERY_ORDER = "query.ashx";

	static final String CHANNEL_ID = "BCA76TD7BF0A334D312F5F958EAFDE89";
	static final String SECRET = "51liangyj";
	static final String ASEKEY = "4BB97A76D2496F80";

	static final String encoding = HTTP.UTF_8;

	AsyncHttpClient client;
	//测试的
//	static final String baseUrl = "http://cs.51zhangdan.cc/banka/thirdparty/";
	//生产的
	static final String baseUrl ="http://www.51zhangdan.com/bkservicev2/thirdparty/";
	protected Map<String, String> params;
	String sendType;

	public Msg51Card(AsyncHttpClient client) {
		this.client = client;
		params = new HashMap<String, String>();
	}

	public void send(AsyncHttpResponseHandler asyncHttpResponseHandler) {
		String url = baseUrl + sendType;
		Log.d("send",
				AsyncHttpClient.getUrlWithQueryString(true, url, buildParams()));
		client.setTimeout(20000);
		client.post(url, buildParams(), asyncHttpResponseHandler);
	}

	public void send(AsyncHttpResponseHandler asyncHttpResponseHandler,
			long cityId) {
		String url = baseUrl + sendType;
		Log.d("send",
				AsyncHttpClient.getUrlWithQueryString(true, url, buildParams()));
//		client.setTimeout(20000);
		client.post(url, buildParams(), asyncHttpResponseHandler);
	}

	private RequestParams buildParams() {
		RequestParams requestParams = new RequestParams();
		requestParams.setContentEncoding(encoding);
		params.put("channelId", CHANNEL_ID);
		params.put("secret", SECRET);
		List<Map.Entry<String, String>> paramsList = new ArrayList<Map.Entry<String, String>>(
				params.entrySet());

		Collections.sort(paramsList,
				new Comparator<Map.Entry<String, String>>() {
					public int compare(Map.Entry<String, String> o1,
							Map.Entry<String, String> o2) {
						return (o1.getKey()).toString().compareTo(o2.getKey());
					}
				});

		String signContent = "";
		StringBuilder sb = new StringBuilder();
		for (Map.Entry item : paramsList) {
			sb.append(item.getKey().toString() + "="
					+ item.getValue().toString().trim() + "&");
			if (!item.getKey().toString().equals("secret")) {
				requestParams.add(item.getKey().toString(), item.getValue()
						.toString());
			}
		}
		signContent = sb.toString();
		signContent = signContent.substring(0, signContent.length() - 1);
		Log.d("sign msg=", signContent);

		String sign = getMD5Digest(signContent);
		requestParams.add("sign", sign);
		Log.d("requestparam=", requestParams.toString().trim());
		return requestParams;
	}

	private String getMD5Digest(String signContent) {
		byte[] digest = null;
		try {
			digest = MessageDigest.getInstance("MD5").digest(
					signContent.getBytes("UTF-8"));
		} catch (Exception e) {
			Log.e("md5", e.toString());
		}

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < digest.length; i++) {
			if (Integer.toHexString(0xFF & digest[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & digest[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & digest[i]));
		}
		Log.d("md5", md5StrBuff.toString());
		return md5StrBuff.toString();
	}
}
