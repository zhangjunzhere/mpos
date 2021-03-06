package com.itertk.app.mpos.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.itertk.app.mpos.dbhelper.Address;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table ADDRESS.
*/
public class AddressDao extends AbstractDao<Address, Long> {

    public static final String TABLENAME = "ADDRESS";

    /**
     * Properties of entity Address.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Addr_id = new Property(0, Long.class, "addr_id", true, "ADDR_ID");
        public final static Property Address1 = new Property(1, String.class, "address1", false, "ADDRESS1");
        public final static Property Moren = new Property(2, boolean.class, "moren", false, "MOREN");
    };


    public AddressDao(DaoConfig config) {
        super(config);
    }
    
    public AddressDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'ADDRESS' (" + //
                "'ADDR_ID' INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: addr_id
                "'ADDRESS1' TEXT," + // 1: address1
                "'MOREN' INTEGER NOT NULL );"); // 2: moren
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'ADDRESS'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Address entity) {
        stmt.clearBindings();
 
        Long addr_id = entity.getAddr_id();
        if (addr_id != null) {
            stmt.bindLong(1, addr_id);
        }
 
        String address1 = entity.getAddress1();
        if (address1 != null) {
            stmt.bindString(2, address1);
        }
        stmt.bindLong(3, entity.getMoren() ? 1l: 0l);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Address readEntity(Cursor cursor, int offset) {
        Address entity = new Address( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // addr_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // address1
            cursor.getShort(offset + 2) != 0 // moren
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Address entity, int offset) {
        entity.setAddr_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAddress1(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMoren(cursor.getShort(offset + 2) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Address entity, long rowId) {
        entity.setAddr_id(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Address entity) {
        if(entity != null) {
            return entity.getAddr_id();
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
