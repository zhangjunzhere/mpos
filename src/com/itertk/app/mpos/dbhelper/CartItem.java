package com.itertk.app.mpos.dbhelper;

import com.itertk.app.mpos.dbhelper.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table CART_ITEM.
 */
public class CartItem {

    private Long _id;
    /** Not-null value. */
    private String supplier;
    private long productId;
    private long productQuantity;
    /** Not-null value. */
    private String price;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient CartItemDao myDao;

    private Cart cart;
    private String cart__resolvedKey;

    private Product product;
    private Long product__resolvedKey;


    public CartItem() {
    }

    public CartItem(Long _id) {
        this._id = _id;
    }

    public CartItem(Long _id, String supplier, long productId, long productQuantity, String price) {
        this._id = _id;
        this.supplier = supplier;
        this.productId = productId;
        this.productQuantity = productQuantity;
        this.price = price;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCartItemDao() : null;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    /** Not-null value. */
    public String getSupplier() {
        return supplier;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(long productQuantity) {
        this.productQuantity = productQuantity;
    }

    /** Not-null value. */
    public String getPrice() {
        return price;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPrice(String price) {
        this.price = price;
    }

    /** To-one relationship, resolved on first access. */
    public Cart getCart() {
        String __key = this.supplier;
        if (cart__resolvedKey == null || cart__resolvedKey != __key) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CartDao targetDao = daoSession.getCartDao();
            Cart cartNew = targetDao.load(__key);
            synchronized (this) {
                cart = cartNew;
            	cart__resolvedKey = __key;
            }
        }
        return cart;
    }

    public void setCart(Cart cart) {
        if (cart == null) {
            throw new DaoException("To-one property 'supplier' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.cart = cart;
            supplier = cart.getSupplier();
            cart__resolvedKey = supplier;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Product getProduct() {
        long __key = this.productId;
        if (product__resolvedKey == null || !product__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ProductDao targetDao = daoSession.getProductDao();
            Product productNew = targetDao.load(__key);
            synchronized (this) {
                product = productNew;
            	product__resolvedKey = __key;
            }
        }
        return product;
    }

    public void setProduct(Product product) {
        if (product == null) {
            throw new DaoException("To-one property 'productId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.product = product;
            productId = product.getId();
            product__resolvedKey = productId;
        }
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