package com.itertk.app.mpos.locationhelper;

import android.content.Context;
import android.util.Log;


import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Administrator on 2014/7/21.
 */
public class LocationHelper {
    private Context context;
    private static final String TAG = "LocationHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "lyjinfo.db";


    private static DaoMaster daoMaster;
    private static DaoSession daoSession;


    private AirCityDao airCityManager;
    private TrainCityDao trainCityManager;
    private BankDao bankManager;



    public LocationHelper(Context context){
        this.context = context;


        daoMaster = getDaoMaster();
        daoSession = getDaoSession();

        airCityManager = daoSession.getAirCityDao();
        trainCityManager = daoSession.getTrainCityDao();
        bankManager = daoSession.getBankDao();

    }



    private DaoMaster getDaoMaster()
    {
        if (daoMaster == null)
        {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(LocationHelper.this.context, DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public  DaoSession getDaoSession()
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }



    public AirCityDao getAirCityManager() {
        return airCityManager;
    }

    public TrainCityDao getTrainCityManager() {
        return trainCityManager;
    }

    public BankDao getBankManager() {
        return bankManager;
    }
}
