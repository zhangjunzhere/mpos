package com.itertk.app.mpos.locationhelper;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.itertk.app.mpos.locationhelper.TrainCity;
import com.itertk.app.mpos.locationhelper.AirCity;
import com.itertk.app.mpos.locationhelper.Bank;

import com.itertk.app.mpos.locationhelper.TrainCityDao;
import com.itertk.app.mpos.locationhelper.AirCityDao;
import com.itertk.app.mpos.locationhelper.BankDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig trainCityDaoConfig;
    private final DaoConfig airCityDaoConfig;
    private final DaoConfig bankDaoConfig;

    private final TrainCityDao trainCityDao;
    private final AirCityDao airCityDao;
    private final BankDao bankDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        trainCityDaoConfig = daoConfigMap.get(TrainCityDao.class).clone();
        trainCityDaoConfig.initIdentityScope(type);

        airCityDaoConfig = daoConfigMap.get(AirCityDao.class).clone();
        airCityDaoConfig.initIdentityScope(type);

        bankDaoConfig = daoConfigMap.get(BankDao.class).clone();
        bankDaoConfig.initIdentityScope(type);

        trainCityDao = new TrainCityDao(trainCityDaoConfig, this);
        airCityDao = new AirCityDao(airCityDaoConfig, this);
        bankDao = new BankDao(bankDaoConfig, this);

        registerDao(TrainCity.class, trainCityDao);
        registerDao(AirCity.class, airCityDao);
        registerDao(Bank.class, bankDao);
    }
    
    public void clear() {
        trainCityDaoConfig.getIdentityScope().clear();
        airCityDaoConfig.getIdentityScope().clear();
        bankDaoConfig.getIdentityScope().clear();
    }

    public TrainCityDao getTrainCityDao() {
        return trainCityDao;
    }

    public AirCityDao getAirCityDao() {
        return airCityDao;
    }

    public BankDao getBankDao() {
        return bankDao;
    }

}
