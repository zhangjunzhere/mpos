package com.itertk.app.mpos.dbhelper;

import java.util.List;
import com.itertk.app.mpos.dbhelper.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CATALOG.
 */
public class Catalog {

    private long category_id;
    /** Not-null value. */
    private String name;
    private String iconUrl;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CatalogDao myDao;

    private List<Product> productList;

    public Catalog() {
    }

    public Catalog(long category_id) {
        this.category_id = category_id;
    }

    public Catalog(long category_id, String name, String iconUrl) {
        this.category_id = category_id;
        this.name = name;
        this.iconUrl = iconUrl;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCatalogDao() : null;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Product> getProductList() {
        if (productList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductDao targetDao = daoSession.getProductDao();
            List<Product> productListNew = targetDao._queryCatalog_ProductList(category_id);
            synchronized (this) {
                if(productList == null) {
                    productList = productListNew;
                }
            }
        }
        return productList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetProductList() {
        productList = null;
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
