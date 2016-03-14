package com.itertk.app.mpos.card51dbhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class ApplyCreditCardBean implements Parcelable {
	public String citycode;
	public String bankids;
	public String truename;
	public String address;
	public String mobile;
	public String idcard;
	public String areacode;
	public String cardgrade;
	public String occupation;
	public String fund;
	public String socialensure;
	public String jobprove;
	public String graduation;
	public String jobtype;

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<ApplyCreditCardBean> CREATOR = new Parcelable.Creator<ApplyCreditCardBean>() {
		public ApplyCreditCardBean createFromParcel(Parcel in) {
			return new ApplyCreditCardBean(in);
		}

		public ApplyCreditCardBean[] newArray(int size) {
			return new ApplyCreditCardBean[size];
		}
	};

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(citycode);
		out.writeString(bankids);
		out.writeString(truename);
		out.writeString(address);
		out.writeString(mobile);
		out.writeString(idcard);
		out.writeString(areacode);
		out.writeString(cardgrade);
		out.writeString(occupation);
		out.writeString(fund);
		out.writeString(socialensure);
		out.writeString(jobprove);
		out.writeString(graduation);
		out.writeString(jobtype);

	}

	public ApplyCreditCardBean(Parcel in) {
		citycode = in.readString();
		bankids = in.readString();
		truename = in.readString();
		address = in.readString();
		mobile = in.readString();
		idcard = in.readString();
		areacode = in.readString();
		cardgrade = in.readString();
		occupation = in.readString();
		fund = in.readString();
		socialensure = in.readString();
		jobprove = in.readString();
		graduation = in.readString();
		jobtype = in.readString();
	}

	public ApplyCreditCardBean() {

	}

	@Override
	public String toString() {
		return "citycode:" + citycode + "   bankids:" + bankids + "  truename:"
				+ truename + "  address:" + address + "  mobile:" + mobile
				+ "  idcard:" + idcard + "  areacode:" + areacode
				+ "  cardgrade:" + cardgrade + "  occupation:" + occupation
				+ "  fund:" + fund + "  socialensure:" + socialensure
				+ "  jobprove:" + jobprove + "  graduation:" + graduation
				+ "  jobtype:" + jobtype;
	}
}
