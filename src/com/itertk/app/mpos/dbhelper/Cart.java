package com.itertk.app.mpos.dbhelper;

import java.util.List;
import com.itertk.app.mpos.dbhelper.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CART.
 */
public class Cart {

    /** Not-null value. */
    private String supplier;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CartDao myDao;

    private List<CartItem> cartItemList;

    public Cart() {
    }

    public Cart(String supplier) {
        this.supplier = supplier;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCartDao() : null;
    }

    /** Not-null value. */
    public String getSupplier() {
        return supplier;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<CartItem> getCartItemList() {
        if (cartItemList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CartItemDao targetDao = daoSession.getCartItemDao();
            List<CartItem> cartItemListNew = targetDao._queryCart_CartItemList(supplier);
            synchronized (this) {
                if(cartItemList == null) {
                    cartItemList = cartItemListNew;
                }
            }
        }
        return cartItemList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetCartItemList() {
        cartItemList = null;
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
