package com.itertk.app.mpos.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.dbhelper.AddressDao;
import com.itertk.app.mpos.dbhelper.GpsLocation;
import com.itertk.app.mpos.dbhelper.GpsLocationDao;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * ��Application
 */
public class BaiduLocation  {
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
    private Boolean located = false;

	public void onCreate(Context context) {
		mLocationClient = new LocationClient(context);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);

        requestLocation();
        loadLastGpsCoordinate();
	}



	public void onTerminate(){
//        if(!located) {
//            located = true;
//            mLocationClient.unRegisterLocationListener(mMyLocationListener);
//            mLocationClient.stop();
//        }
	}
	private void requestLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);
		option.setCoorType("gcj02");		
		option.setScanSpan(10*60*1000); //10 minutes
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

    private void  loadLastGpsCoordinate(){
        MPosApplication mPosApplication = MPosApplication.getInstance();
        List<GpsLocation> location = mPosApplication.getDataHelper().getDaoSession().getGpsLocationDao().loadAll();

        if(location.size() > 0){
            mPosApplication.getMsgBuilder().setLongitude(location.get(0).getLontitude());
            mPosApplication.getMsgBuilder().setLatitude(location.get(0).getLatitude());
        }
    }

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
            if(location.getLocType() != BDLocation.TypeServerError&&location.getAddrStr()!=null){
                // save gps location
                MPosApplication mPosApplication = MPosApplication.getInstance();
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                String addrStr = location.getAddrStr();

                GpsLocation gpsLocation = new GpsLocation(latitude,longitude, location.getTime(),location.getAddrStr());
                GpsLocationDao  gpsLocationDao =MPosApplication.getInstance().getDataHelper().getDaoSession().getGpsLocationDao();
                gpsLocationDao.deleteAll();
                gpsLocationDao.insert(gpsLocation);

                AddressDao addressDao = mPosApplication.getDataHelper().getDaoSession().getAddressDao();
                List<com.itertk.app.mpos.dbhelper.Address> addresses = addressDao.queryBuilder().where(AddressDao.Properties.Address1.eq(addrStr)).list();

                if(addresses.size() <= 0){
                    com.itertk.app.mpos.dbhelper.Address address = new com.itertk.app.mpos.dbhelper.Address(null,addrStr,false);
                    addressDao.insert(address);
                }
                //save gps into memory
                mPosApplication.getMsgBuilder().setLatitude(latitude);
                mPosApplication.getMsgBuilder().setLongitude(longitude);
                onTerminate();
                Log.i("BaiduLocationApiDem", new Gson().toJson(gpsLocation));
            }else {
                //Receive Location
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                sb.append(location.getTime());
                sb.append("\nerror code : ");
                sb.append(location.getLocType());
                sb.append("\nlatitude : ");
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");
                sb.append(location.getLongitude());
                sb.append("\nradius : ");
                sb.append(location.getRadius());
                if (location.getLocType() == BDLocation.TypeGpsLocation){
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());
                    sb.append("\ndirection : ");
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());
                    sb.append(location.getDirection());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                    sb.append("\naddr : ");
                    sb.append(location.getAddrStr());

                    sb.append("\noperationers : ");
                    sb.append(location.getOperators());
                }
                Log.i("BaiduLocationApiDem", sb.toString());
            }
		}
	}
}
