package com.itertk.app.mpos.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.itertk.app.mpos.dbhelper.ShopOrder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SHOP_ORDER.
*/
public class ShopOrderDao extends AbstractDao<ShopOrder, Long> {

    public static final String TABLENAME = "SHOP_ORDER";

    /**
     * Properties of entity ShopOrder.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "ID");
        public final static Property Trade_no = new Property(1, String.class, "trade_no", false, "TRADE_NO");
        public final static Property SupplierId = new Property(2, String.class, "supplierId", false, "SUPPLIER_ID");
        public final static Property OrderDate = new Property(3, java.util.Date.class, "orderDate", false, "ORDER_DATE");
        public final static Property ConsigneeName = new Property(4, String.class, "consigneeName", false, "CONSIGNEE_NAME");
        public final static Property ConsigneePhone = new Property(5, String.class, "consigneePhone", false, "CONSIGNEE_PHONE");
        public final static Property ConsigneeAddress = new Property(6, String.class, "consigneeAddress", false, "CONSIGNEE_ADDRESS");
        public final static Property PaymentName = new Property(7, String.class, "paymentName", false, "PAYMENT_NAME");
        public final static Property TransferName = new Property(8, String.class, "transferName", false, "TRANSFER_NAME");
        public final static Property TotalPrice = new Property(9, String.class, "totalPrice", false, "TOTAL_PRICE");
        public final static Property TotalQuantity = new Property(10, Integer.class, "totalQuantity", false, "TOTAL_QUANTITY");
        public final static Property Status = new Property(11, Integer.class, "status", false, "STATUS");
        public final static Property Remark = new Property(12, String.class, "remark", false, "REMARK");
    };

    private DaoSession daoSession;


    public ShopOrderDao(DaoConfig config) {
        super(config);
    }
    
    public ShopOrderDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SHOP_ORDER' (" + //
                "'ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "'TRADE_NO' TEXT," + // 1: trade_no
                "'SUPPLIER_ID' TEXT NOT NULL ," + // 2: supplierId
                "'ORDER_DATE' INTEGER NOT NULL ," + // 3: orderDate
                "'CONSIGNEE_NAME' TEXT," + // 4: consigneeName
                "'CONSIGNEE_PHONE' TEXT," + // 5: consigneePhone
                "'CONSIGNEE_ADDRESS' TEXT," + // 6: consigneeAddress
                "'PAYMENT_NAME' TEXT," + // 7: paymentName
                "'TRANSFER_NAME' TEXT," + // 8: transferName
                "'TOTAL_PRICE' TEXT NOT NULL ," + // 9: totalPrice
                "'TOTAL_QUANTITY' INTEGER," + // 10: totalQuantity
                "'STATUS' INTEGER," + // 11: status
                "'REMARK' TEXT);"); // 12: remark
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SHOP_ORDER'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, ShopOrder entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String trade_no = entity.getTrade_no();
        if (trade_no != null) {
            stmt.bindString(2, trade_no);
        }
        stmt.bindString(3, entity.getSupplierId());
        stmt.bindLong(4, entity.getOrderDate().getTime());
 
        String consigneeName = entity.getConsigneeName();
        if (consigneeName != null) {
            stmt.bindString(5, consigneeName);
        }
 
        String consigneePhone = entity.getConsigneePhone();
        if (consigneePhone != null) {
            stmt.bindString(6, consigneePhone);
        }
 
        String consigneeAddress = entity.getConsigneeAddress();
        if (consigneeAddress != null) {
            stmt.bindString(7, consigneeAddress);
        }
 
        String paymentName = entity.getPaymentName();
        if (paymentName != null) {
            stmt.bindString(8, paymentName);
        }
 
        String transferName = entity.getTransferName();
        if (transferName != null) {
            stmt.bindString(9, transferName);
        }
        stmt.bindString(10, entity.getTotalPrice());
 
        Integer totalQuantity = entity.getTotalQuantity();
        if (totalQuantity != null) {
            stmt.bindLong(11, totalQuantity);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(12, status);
        }
 
        String remark = entity.getRemark();
        if (remark != null) {
            stmt.bindString(13, remark);
        }
    }

    @Override
    protected void attachEntity(ShopOrder entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public ShopOrder readEntity(Cursor cursor, int offset) {
        ShopOrder entity = new ShopOrder( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // trade_no
            cursor.getString(offset + 2), // supplierId
            new java.util.Date(cursor.getLong(offset + 3)), // orderDate
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // consigneeName
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // consigneePhone
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // consigneeAddress
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // paymentName
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // transferName
            cursor.getString(offset + 9), // totalPrice
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10), // totalQuantity
            cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11), // status
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // remark
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, ShopOrder entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTrade_no(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSupplierId(cursor.getString(offset + 2));
        entity.setOrderDate(new java.util.Date(cursor.getLong(offset + 3)));
        entity.setConsigneeName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setConsigneePhone(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setConsigneeAddress(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setPaymentName(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTransferName(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTotalPrice(cursor.getString(offset + 9));
        entity.setTotalQuantity(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
        entity.setStatus(cursor.isNull(offset + 11) ? null : cursor.getInt(offset + 11));
        entity.setRemark(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(ShopOrder entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(ShopOrder entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
