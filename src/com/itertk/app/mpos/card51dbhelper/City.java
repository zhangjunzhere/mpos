package com.itertk.app.mpos.card51dbhelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CITY.
 */
public class City {

    private Long cityId;
    /** Not-null value. */
    private String code;
    /** Not-null value. */
    private String name;
    private long provinceId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CityDao myDao;

    private Province province;
    private Long province__resolvedKey;

    private List<CityArea> data;

    public City() {
    }

    public City(Long cityId) {
        this.cityId = cityId;
    }

    public City(Long cityId, String code, String name, long provinceId) {
        this.cityId = cityId;
        this.code = code;
        this.name = name;
        this.provinceId = provinceId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCityDao() : null;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    /** Not-null value. */
    public String getCode() {
        return code;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCode(String code) {
        this.code = code;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    /** To-one relationship, resolved on first access. */
    public Province getProvince() {
        long __key = this.provinceId;
        if (province__resolvedKey == null || !province__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProvinceDao targetDao = daoSession.getProvinceDao();
            Province provinceNew = targetDao.load(__key);
            synchronized (this) {
                province = provinceNew;
            	province__resolvedKey = __key;
            }
        }
        return province;
    }

    public void setProvince(Province province) {
        if (province == null) {
            throw new DaoException("To-one property 'provinceId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.province = province;
            provinceId = province.getProvinceId();
            province__resolvedKey = provinceId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<CityArea> getData() {
        if (data == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CityAreaDao targetDao = daoSession.getCityAreaDao();
            List<CityArea> dataNew = targetDao._queryCity_Data(cityId);
            synchronized (this) {
                if(data == null) {
                    data = dataNew;
                }
            }
        }
        return data;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetData() {
        data = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
