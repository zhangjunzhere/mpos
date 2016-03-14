package com.itertk.app.mpos.card51dbhelper;

import android.content.Context;

public class LocationCard51Helper {
	private Context context;
	private static final String TAG = "LocationCard51Helper";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "lyj51card.db";

	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	private CityAreaDao cityareaManager;
	private CityDao cityManager;
	private ProvinceDao provinceManager;

	public LocationCard51Helper(Context context) {
		this.context = context;

		daoMaster = getDaoMaster();
		daoSession = getDaoSession();

		cityareaManager = daoSession.getCityAreaDao();
		cityManager = daoSession.getCityDao();
		provinceManager = daoSession.getProvinceDao();

	}

	private DaoMaster getDaoMaster() {
		if (daoMaster == null) {
			DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(
					LocationCard51Helper.this.context, DATABASE_NAME, null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	public DaoSession getDaoSession() {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster();
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

	public CityDao getCityManager() {
		return cityManager;
	}

	public CityAreaDao getCityAreaManager() {
		return cityareaManager;
	}

	public ProvinceDao getProvinceManager() {
		return provinceManager;
	}
}
