package com.itertk.app.mpos.card51dbhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class CityBank implements Parcelable {
	@Override
	public String toString() {
		String str = "cityBank:" + "bankid=" + bankid + "name=" + bankname
				+ "applypeople：" + applypeople;

		return str;
	}

	public int bankid;
	public String bankname;
	public String applypeople;

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<CityBank> CREATOR = new Parcelable.Creator<CityBank>() {
		public CityBank createFromParcel(Parcel in) {
			return new CityBank(in);
		}

		public CityBank[] newArray(int size) {
			return new CityBank[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(bankid);
		out.writeString(bankname);
		out.writeString(applypeople);
	}

	public CityBank(Parcel in) {
		bankid = in.readInt();
		bankname = in.readString();
		applypeople = in.readString();
	}

	public CityBank() {

	}
}
