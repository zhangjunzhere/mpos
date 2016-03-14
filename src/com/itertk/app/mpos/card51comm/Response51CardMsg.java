package com.itertk.app.mpos.card51comm;

import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.itertk.app.mpos.card51dbhelper.CityArea;
import com.itertk.app.mpos.card51dbhelper.CityBank;
import com.itertk.app.mpos.card51dbhelper.Province;

public class Response51CardMsg {
	static final String TAG = "Response51CardMsg";

	public static ProvinceResponseMsg generateProvinceResponseMsg(
			String response) {
		ProvinceResponseMsg responseMsg = null;

		if (response == null) {
			Log.e(TAG, "response is null");
			return responseMsg;
		}

		try {
			responseMsg = (new Gson()).fromJson(response,
					ProvinceResponseMsg.class);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return responseMsg;
	}

	public static CityAreaResponseMsg generateCityAreaResponseMsg(
			String response) {
		CityAreaResponseMsg responseMsg = null;

		if (response == null) {
			Log.e(TAG, "response is null");
			return responseMsg;
		}

		try {
			responseMsg = (new Gson()).fromJson(response,
					CityAreaResponseMsg.class);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return responseMsg;
	}

	public static CityBankResponseMsg generateCityBankResponseMsg(
			String response) {
		CityBankResponseMsg responseMsg = null;

		if (response == null) {
			Log.e(TAG, "response is null");
			return responseMsg;
		}

		try {
			responseMsg = (new Gson()).fromJson(response,
					CityBankResponseMsg.class);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return responseMsg;
	}

	public static ApplyTradeResponseMsg generateApplyTradeResponseMsg(
			String response) {
		ApplyTradeResponseMsg responseMsg = null;

		if (response == null) {
			Log.e(TAG, "response is null");
			return responseMsg;
		}

		try {
			responseMsg = (new Gson()).fromJson(response,
					ApplyTradeResponseMsg.class);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return responseMsg;
	}

	public static QueryOrderResponseMsg generateQueryOrderResponseMsg(
			String response) {
		QueryOrderResponseMsg responseMsg = null;

		if (response == null) {
			Log.e(TAG, "response is null");
			return responseMsg;
		}

		try {
			responseMsg = (new Gson()).fromJson(response,
					QueryOrderResponseMsg.class);
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return responseMsg;
	}

	public class ProvinceResponseMsg {
		public int code;
		public String msg;
		public List<Province> data;

		public boolean isSuccess() {
			if (code == 0)
				return true;
			return false;
		}

		@Override
		public String toString() {
			String str = "result_code=" + code + " result_code_msg=" + msg;
			for (Province pro : data) {
				str += pro.toString();
			}

			return str;
		}

		// public String
//		public class Province {
//			@Override
//			public String toString() {
//				String str = "code=" + code + "name=" + name;
//
//				for (City c : city) {
//					str += c.toString();
//				}
//
//				return str;
//			}
//
//			public String code;
//			public String name;
//			public List<City> city;
//
//			public class City {
//				public String code;
//				public String name;
//
//				public String toString() {
//					return "city:" + "code=" + code + "name=" + name;
//				}
//			}
//		}
	}

	public class CityAreaResponseMsg {
		public int code;
		public String msg;
		public List<CityArea> data;

		public boolean isSuccess() {
			if (code == 0)
				return true;
			return false;
		}

		@Override
		public String toString() {
			String str = "result_code=" + code + " result_code_msg=" + msg;
			for (CityArea cityArea : data) {
				str += cityArea.toString();
			}

			return str;
		}

//		// public String
//		public class CityArea {
//			@Override
//			public String toString() {
//				String str = "cityArea:" + "code=" + code + "name=" + name;
//
//				return str;
//			}
//
//			public String code;
//			public String name;
//		}
	}

	public class CityBankResponseMsg {
		public int code;
		public String msg;
		public List<CityBank> data;

		public boolean isSuccess() {
			if (code == 0)
				return true;
			return false;
		}

		@Override
		public String toString() {
			String str = "result_code=" + code + " result_code_msg=" + msg;
			for (CityBank cityBank : data) {
				str += cityBank.toString();
			}

			return str;
		}

		// // public String
		// public class CityBank implements Parcelable {
		// @Override
		// public String toString() {
		// String str = "cityBank:" + "bankid=" + bankid + "name="
		// + bankname + "applypeople：" + applypeople;
		//
		// return str;
		// }
		//
		// public int bankid;
		// public String bankname;
		// public String applypeople;
		//
		// @Override
		// public int describeContents() {
		// return 0;
		// }
		//
		// public Parcelable.Creator<CityBank> CREATOR = new
		// Parcelable.Creator<CityBank>() {
		// public CityBank createFromParcel(Parcel in) {
		// return new CityBank(in);
		// }
		//
		// public CityBank[] newArray(int size) {
		// return new CityBank[size];
		// }
		// };
		//
		// @Override
		// public void writeToParcel(Parcel out, int flags) {
		// out.writeInt(bankid);
		// out.writeString(bankname);
		// out.writeString(applypeople);
		// }
		//
		// public CityBank(Parcel in) {
		// bankid = in.readInt();
		// bankname = in.readString();
		// applypeople = in.readString();
		// }
		//
		// public CityBank() {
		//
		// }
		// }
	}

	public class ApplyTradeResponseMsg {
		public int code;
		public String msg;
		public List<ApplyTrade> datas;

		public boolean isSuccess() {
			if (code == 0)
				return true;
			return false;
		}

		@Override
		public String toString() {
			String str = "result_code=" + code + " result_code_msg=" + msg;
			for (ApplyTrade applyTrade : datas) {
				str += applyTrade.toString();
			}

			return str;
		}

		// public String
		public class ApplyTrade {
			@Override
			public String toString() {
				String str = "applyTrade:" + "bankid=" + bankid + "orderno="
						+ orderno + "reason" + reason;

				return str;
			}

			public int bankid;
			public long orderno;
			public String reason;
		}
	}

	public class QueryOrderResponseMsg {
		public int code;
		public String msg;
		public List<QueryOrder> datas;

		public boolean isSuccess() {
			if (code == 0)
				return true;
			return false;
		}

		@Override
		public String toString() {
			String str = "result_code=" + code + " result_code_msg=" + msg;
			for (QueryOrder queryOrder : datas) {
				str += queryOrder.toString();
			}

			return str;
		}

		// public String
		public class QueryOrder {
			@Override
			public String toString() {
				String str = "queryOrder:" + "orderno=" + orderno + "status="
						+ status;

				return str;
			}

			public long orderno;
			public int status;
		}
	}

}
