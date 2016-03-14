package com.itertk.app.mpos.card51comm;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

import com.itertk.app.mpos.card51dbhelper.ApplyCreditCardBean;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;

public class LinkeaMsg51CardBuilder {
	AsyncHttpClient client;

	public LinkeaMsg51CardBuilder() {
		client = new AsyncHttpClient();
	}

	public Msg51Card buildQueryProvinceMsg() {
		return new QueryProvinceMsg(client);
	}

	public Msg51Card buildQueryCityAreaMsg(String cityCode) {
		return new QueryCityAreaMsg(client, cityCode);
	}

	public Msg51Card buildQueryCityBankMsg(String cityCode) {
		return new QueryCityBankMsg(client, cityCode);
	}

	public Msg51Card buildApplyTradeMsg(ApplyCreditCardBean applyCreditCardBean) {
		return new ApplyTradeMsg(client, applyCreditCardBean.citycode,
				applyCreditCardBean.bankids, applyCreditCardBean.truename,
				applyCreditCardBean.address, applyCreditCardBean.mobile,
				applyCreditCardBean.idcard, applyCreditCardBean.areacode,
				applyCreditCardBean.cardgrade, applyCreditCardBean.occupation,
				applyCreditCardBean.fund, applyCreditCardBean.socialensure,
				applyCreditCardBean.jobprove, applyCreditCardBean.graduation,
				applyCreditCardBean.jobtype);
	}

	public QueryOrder buildQueryOrderMsg(String orderno) {
		return new QueryOrder(client, orderno);
	}

	class QueryProvinceMsg extends Msg51Card {

		public QueryProvinceMsg(AsyncHttpClient client) {
			super(client);
			sendType = QUERY_PROVINCE;
		}
	}

	class QueryCityAreaMsg extends Msg51Card {

		public QueryCityAreaMsg(AsyncHttpClient client, String cityCode) {
			super(client);
			sendType = QUERY_CITYAREA;
			params.put("citycode", cityCode);
		}
	}

	class QueryCityBankMsg extends Msg51Card {

		public QueryCityBankMsg(AsyncHttpClient client, String cityCode) {
			super(client);
			sendType = QUERY_CITYBANK;
			params.put("citycode", cityCode);
		}
	}

	class ApplyTradeMsg extends Msg51Card {

		public ApplyTradeMsg(AsyncHttpClient client, String cityCode,
				String bankids, String truename, String address, String mobile,
				String idcard, String areacode, String cardgrade,
				String occupation, String fund, String socialensure,
				String jobprove, String graduation, String jobtype) {
			super(client);

			sendType = APPLY_TRADE;
			String idcardase = "";
			String mobilease = "";
			try {
				idcardase = Encrypt(idcard, Msg51Card.ASEKEY);
				mobilease = Encrypt(mobile, Msg51Card.ASEKEY);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			params.put("citycode", cityCode);
			params.put("bankids", bankids);
			params.put("truename", truename);
			params.put("address", address);
			params.put("mobile", mobilease);
			params.put("idcard", idcardase);
			
			Log.e("mobile", mobilease);
			params.put("areacode", areacode);
			params.put("cardgrade", cardgrade);
			params.put("occupation", occupation);
			params.put("fund", fund);
			params.put("socialensure", socialensure);
			params.put("jobprove", jobprove);
			params.put("graduation", graduation);
			params.put("jobtype", jobtype);
		}
	}

	class QueryOrder extends Msg51Card {

		public QueryOrder(AsyncHttpClient client, String orderno) {
			super(client);
			sendType = QUERY_ORDER;
			params.put("orderno", orderno);
		}
	}

	/**
	 * AES加密
	 * 
	 * @param str
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String Encrypt(String str, String key) throws Exception {
		if (str == null || key == null)
			return null;

		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		byte[] bytes = cipher.doFinal(str.getBytes("utf-8"));
		return Base64.encodeToString(bytes, Base64.DEFAULT);
	}

	public static String Decrypt(String str, String key) throws Exception {
		if (str == null || key == null)
			return null;

		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		byte[] bytes = Base64.decode(str, Base64.DEFAULT);
		bytes = cipher.doFinal(bytes);
		return new String(bytes, "utf-8");
	}
}
