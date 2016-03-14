package com.itertk.app.mpos.dbhelper;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import com.itertk.app.mpos.dbhelper.SaleOrderItem;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SALE_ORDER_ITEM.
*/
public class SaleOrderItemDao extends AbstractDao<SaleOrderItem, Long> {

    public static final String TABLENAME = "SALE_ORDER_ITEM";

    /**
     * Properties of entity SaleOrderItem.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property SaleOrderItemId = new Property(0, Long.class, "saleOrderItemId", true, "SALE_ORDER_ITEM_ID");
        public final static Property Price = new Property(1, String.class, "price", false, "PRICE");
        public final static Property OnePrice = new Property(2, String.class, "onePrice", false, "ONE_PRICE");
        public final static Property CountProduct = new Property(3, long.class, "countProduct", false, "COUNT_PRODUCT");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property Bar_code = new Property(5, String.class, "bar_code", false, "BAR_CODE");
        public final static Property SaleOrderId = new Property(6, long.class, "saleOrderId", false, "SALE_ORDER_ID");
        public final static Property ProductId = new Property(7, Long.class, "productId", false, "PRODUCT_ID");
    };

    private DaoSession daoSession;

    private Query<SaleOrderItem> saleOrder_SaleOrderItemListQuery;

    public SaleOrderItemDao(DaoConfig config) {
        super(config);
    }
    
    public SaleOrderItemDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SALE_ORDER_ITEM' (" + //
                "'SALE_ORDER_ITEM_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: saleOrderItemId
                "'PRICE' TEXT NOT NULL ," + // 1: price
                "'ONE_PRICE' TEXT NOT NULL ," + // 2: onePrice
                "'COUNT_PRODUCT' INTEGER NOT NULL ," + // 3: countProduct
                "'NAME' TEXT," + // 4: name
                "'BAR_CODE' TEXT," + // 5: bar_code
                "'SALE_ORDER_ID' INTEGER NOT NULL ," + // 6: saleOrderId
                "'PRODUCT_ID' INTEGER);"); // 7: productId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SALE_ORDER_ITEM'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SaleOrderItem entity) {
        stmt.clearBindings();
 
        Long saleOrderItemId = entity.getSaleOrderItemId();
        if (saleOrderItemId != null) {
            stmt.bindLong(1, saleOrderItemId);
        }
        stmt.bindString(2, entity.getPrice());
        stmt.bindString(3, entity.getOnePrice());
        stmt.bindLong(4, entity.getCountProduct());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String bar_code = entity.getBar_code();
        if (bar_code != null) {
            stmt.bindString(6, bar_code);
        }
        stmt.bindLong(7, entity.getSaleOrderId());
 
        Long productId = entity.getProductId();
        if (productId != null) {
            stmt.bindLong(8, productId);
        }
    }

    @Override
    protected void attachEntity(SaleOrderItem entity) {
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
    public SaleOrderItem readEntity(Cursor cursor, int offset) {
        SaleOrderItem entity = new SaleOrderItem( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // saleOrderItemId
            cursor.getString(offset + 1), // price
            cursor.getString(offset + 2), // onePrice
            cursor.getLong(offset + 3), // countProduct
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // bar_code
            cursor.getLong(offset + 6), // saleOrderId
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7) // productId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SaleOrderItem entity, int offset) {
        entity.setSaleOrderItemId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPrice(cursor.getString(offset + 1));
        entity.setOnePrice(cursor.getString(offset + 2));
        entity.setCountProduct(cursor.getLong(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBar_code(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setSaleOrderId(cursor.getLong(offset + 6));
        entity.setProductId(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SaleOrderItem entity, long rowId) {
        entity.setSaleOrderItemId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SaleOrderItem entity) {
        if(entity != null) {
            return entity.getSaleOrderItemId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "saleOrderItemList" to-many relationship of SaleOrder. */
    public List<SaleOrderItem> _querySaleOrder_SaleOrderItemList(long saleOrderId) {
        synchronized (this) {
            if (saleOrder_SaleOrderItemListQuery == null) {
                QueryBuilder<SaleOrderItem> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.SaleOrderId.eq(null));
                saleOrder_SaleOrderItemListQuery = queryBuilder.build();
            }
        }
        Query<SaleOrderItem> query = saleOrder_SaleOrderItemListQuery.forCurrentThread();
        query.setParameter(0, saleOrderId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getSaleOrderDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getProductDao().getAllColumns());
            builder.append(" FROM SALE_ORDER_ITEM T");
            builder.append(" LEFT JOIN SALE_ORDER T0 ON T.'SALE_ORDER_ID'=T0.'SALE_ORDER_ID'");
            builder.append(" LEFT JOIN PRODUCT T1 ON T.'PRODUCT_ID'=T1.'ID'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected SaleOrderItem loadCurrentDeep(Cursor cursor, boolean lock) {
        SaleOrderItem entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        SaleOrder saleOrder = loadCurrentOther(daoSession.getSaleOrderDao(), cursor, offset);
         if(saleOrder != null) {
            entity.setSaleOrder(saleOrder);
        }
        offset += daoSession.getSaleOrderDao().getAllColumns().length;

        Product product = loadCurrentOther(daoSession.getProductDao(), cursor, offset);
        entity.setProduct(product);

        return entity;    
    }

    public SaleOrderItem loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<SaleOrderItem> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<SaleOrderItem> list = new ArrayList<SaleOrderItem>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<SaleOrderItem> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<SaleOrderItem> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
