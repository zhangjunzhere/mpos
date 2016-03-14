package com.itertk.app.mpos.dbhelper;

import java.util.List;
import com.itertk.app.mpos.dbhelper.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table SALE_ORDER_ITEM.
 */
public class SaleOrderItem {

    private Long saleOrderItemId;
    /** Not-null value. */
    private String price;
    /** Not-null value. */
    private String onePrice;
    private long countProduct;
    private String name;
    private String bar_code;
    private long saleOrderId;
    private Long productId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient SaleOrderItemDao myDao;

    private SaleOrder saleOrder;
    private Long saleOrder__resolvedKey;

    private Product product;
    private Long product__resolvedKey;

    private List<SaleOrderItemAttributeValue> saleOrderItemAttributeValueList;
    private List<SaleOrderItemReduce> saleOrderItemReduceList;

    public SaleOrderItem() {
    }

    public SaleOrderItem(Long saleOrderItemId) {
        this.saleOrderItemId = saleOrderItemId;
    }

    public SaleOrderItem(Long saleOrderItemId, String price, String onePrice, long countProduct, String name, String bar_code, long saleOrderId, Long productId) {
        this.saleOrderItemId = saleOrderItemId;
        this.price = price;
        this.onePrice = onePrice;
        this.countProduct = countProduct;
        this.name = name;
        this.bar_code = bar_code;
        this.saleOrderId = saleOrderId;
        this.productId = productId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSaleOrderItemDao() : null;
    }

    public Long getSaleOrderItemId() {
        return saleOrderItemId;
    }

    public void setSaleOrderItemId(Long saleOrderItemId) {
        this.saleOrderItemId = saleOrderItemId;
    }

    /** Not-null value. */
    public String getPrice() {
        return price;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setPrice(String price) {
        this.price = price;
    }

    /** Not-null value. */
    public String getOnePrice() {
        return onePrice;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setOnePrice(String onePrice) {
        this.onePrice = onePrice;
    }

    public long getCountProduct() {
        return countProduct;
    }

    public void setCountProduct(long countProduct) {
        this.countProduct = countProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    public long getSaleOrderId() {
        return saleOrderId;
    }

    public void setSaleOrderId(long saleOrderId) {
        this.saleOrderId = saleOrderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /** To-one relationship, resolved on first access. */
    public SaleOrder getSaleOrder() {
        long __key = this.saleOrderId;
        if (saleOrder__resolvedKey == null || !saleOrder__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SaleOrderDao targetDao = daoSession.getSaleOrderDao();
            SaleOrder saleOrderNew = targetDao.load(__key);
            synchronized (this) {
                saleOrder = saleOrderNew;
            	saleOrder__resolvedKey = __key;
            }
        }
        return saleOrder;
    }

    public void setSaleOrder(SaleOrder saleOrder) {
        if (saleOrder == null) {
            throw new DaoException("To-one property 'saleOrderId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.saleOrder = saleOrder;
            saleOrderId = saleOrder.getSaleOrderId();
            saleOrder__resolvedKey = saleOrderId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public Product getProduct() {
        Long __key = this.productId;
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
        synchronized (this) {
            this.product = product;
            productId = product == null ? null : product.getId();
            product__resolvedKey = productId;
        }
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<SaleOrderItemAttributeValue> getSaleOrderItemAttributeValueList() {
        if (saleOrderItemAttributeValueList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SaleOrderItemAttributeValueDao targetDao = daoSession.getSaleOrderItemAttributeValueDao();
            List<SaleOrderItemAttributeValue> saleOrderItemAttributeValueListNew = targetDao._querySaleOrderItem_SaleOrderItemAttributeValueList(saleOrderItemId);
            synchronized (this) {
                if(saleOrderItemAttributeValueList == null) {
                    saleOrderItemAttributeValueList = saleOrderItemAttributeValueListNew;
                }
            }
        }
        return saleOrderItemAttributeValueList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetSaleOrderItemAttributeValueList() {
        saleOrderItemAttributeValueList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<SaleOrderItemReduce> getSaleOrderItemReduceList() {
        if (saleOrderItemReduceList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SaleOrderItemReduceDao targetDao = daoSession.getSaleOrderItemReduceDao();
            List<SaleOrderItemReduce> saleOrderItemReduceListNew = targetDao._querySaleOrderItem_SaleOrderItemReduceList(saleOrderItemId);
            synchronized (this) {
                if(saleOrderItemReduceList == null) {
                    saleOrderItemReduceList = saleOrderItemReduceListNew;
                }
            }
        }
        return saleOrderItemReduceList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetSaleOrderItemReduceList() {
        saleOrderItemReduceList = null;
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