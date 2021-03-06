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

import com.itertk.app.mpos.dbhelper.SaleOrderItemReduce;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table SALE_ORDER_ITEM_REDUCE.
*/
public class SaleOrderItemReduceDao extends AbstractDao<SaleOrderItemReduce, Long> {

    public static final String TABLENAME = "SALE_ORDER_ITEM_REDUCE";

    /**
     * Properties of entity SaleOrderItemReduce.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property SaleOrderItemReduceId = new Property(0, Long.class, "saleOrderItemReduceId", true, "SALE_ORDER_ITEM_REDUCE_ID");
        public final static Property SaleOrderItemId = new Property(1, Long.class, "saleOrderItemId", false, "SALE_ORDER_ITEM_ID");
        public final static Property ReduceId = new Property(2, Long.class, "reduceId", false, "REDUCE_ID");
    };

    private DaoSession daoSession;

    private Query<SaleOrderItemReduce> saleOrderItem_SaleOrderItemReduceListQuery;

    public SaleOrderItemReduceDao(DaoConfig config) {
        super(config);
    }
    
    public SaleOrderItemReduceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'SALE_ORDER_ITEM_REDUCE' (" + //
                "'SALE_ORDER_ITEM_REDUCE_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: saleOrderItemReduceId
                "'SALE_ORDER_ITEM_ID' INTEGER," + // 1: saleOrderItemId
                "'REDUCE_ID' INTEGER);"); // 2: reduceId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'SALE_ORDER_ITEM_REDUCE'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, SaleOrderItemReduce entity) {
        stmt.clearBindings();
 
        Long saleOrderItemReduceId = entity.getSaleOrderItemReduceId();
        if (saleOrderItemReduceId != null) {
            stmt.bindLong(1, saleOrderItemReduceId);
        }
 
        Long saleOrderItemId = entity.getSaleOrderItemId();
        if (saleOrderItemId != null) {
            stmt.bindLong(2, saleOrderItemId);
        }
 
        Long reduceId = entity.getReduceId();
        if (reduceId != null) {
            stmt.bindLong(3, reduceId);
        }
    }

    @Override
    protected void attachEntity(SaleOrderItemReduce entity) {
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
    public SaleOrderItemReduce readEntity(Cursor cursor, int offset) {
        SaleOrderItemReduce entity = new SaleOrderItemReduce( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // saleOrderItemReduceId
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // saleOrderItemId
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // reduceId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, SaleOrderItemReduce entity, int offset) {
        entity.setSaleOrderItemReduceId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSaleOrderItemId(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setReduceId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(SaleOrderItemReduce entity, long rowId) {
        entity.setSaleOrderItemReduceId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(SaleOrderItemReduce entity) {
        if(entity != null) {
            return entity.getSaleOrderItemReduceId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "saleOrderItemReduceList" to-many relationship of SaleOrderItem. */
    public List<SaleOrderItemReduce> _querySaleOrderItem_SaleOrderItemReduceList(Long saleOrderItemId) {
        synchronized (this) {
            if (saleOrderItem_SaleOrderItemReduceListQuery == null) {
                QueryBuilder<SaleOrderItemReduce> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.SaleOrderItemId.eq(null));
                saleOrderItem_SaleOrderItemReduceListQuery = queryBuilder.build();
            }
        }
        Query<SaleOrderItemReduce> query = saleOrderItem_SaleOrderItemReduceListQuery.forCurrentThread();
        query.setParameter(0, saleOrderItemId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getSaleOrderItemDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getReduceDao().getAllColumns());
            builder.append(" FROM SALE_ORDER_ITEM_REDUCE T");
            builder.append(" LEFT JOIN SALE_ORDER_ITEM T0 ON T.'SALE_ORDER_ITEM_ID'=T0.'SALE_ORDER_ITEM_ID'");
            builder.append(" LEFT JOIN REDUCE T1 ON T.'REDUCE_ID'=T1.'REDUCE_ID'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected SaleOrderItemReduce loadCurrentDeep(Cursor cursor, boolean lock) {
        SaleOrderItemReduce entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        SaleOrderItem saleOrderItem = loadCurrentOther(daoSession.getSaleOrderItemDao(), cursor, offset);
        entity.setSaleOrderItem(saleOrderItem);
        offset += daoSession.getSaleOrderItemDao().getAllColumns().length;

        Reduce reduce = loadCurrentOther(daoSession.getReduceDao(), cursor, offset);
        entity.setReduce(reduce);

        return entity;    
    }

    public SaleOrderItemReduce loadDeep(Long key) {
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
    public List<SaleOrderItemReduce> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<SaleOrderItemReduce> list = new ArrayList<SaleOrderItemReduce>(count);
        
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
    
    protected List<SaleOrderItemReduce> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<SaleOrderItemReduce> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
