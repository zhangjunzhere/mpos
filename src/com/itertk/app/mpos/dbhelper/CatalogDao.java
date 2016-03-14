package com.itertk.app.mpos.dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.itertk.app.mpos.dbhelper.Catalog;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table CATALOG.
*/
public class CatalogDao extends AbstractDao<Catalog, Long> {

    public static final String TABLENAME = "CATALOG";

    /**
     * Properties of entity Catalog.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Category_id = new Property(0, long.class, "category_id", true, "CATEGORY_ID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property IconUrl = new Property(2, String.class, "iconUrl", false, "ICON_URL");
    };

    private DaoSession daoSession;


    public CatalogDao(DaoConfig config) {
        super(config);
    }
    
    public CatalogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'CATALOG' (" + //
                "'CATEGORY_ID' INTEGER PRIMARY KEY NOT NULL ," + // 0: category_id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'ICON_URL' TEXT);"); // 2: iconUrl
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'CATALOG'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Catalog entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getCategory_id());
        stmt.bindString(2, entity.getName());
 
        String iconUrl = entity.getIconUrl();
        if (iconUrl != null) {
            stmt.bindString(3, iconUrl);
        }
    }

    @Override
    protected void attachEntity(Catalog entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Catalog readEntity(Cursor cursor, int offset) {
        Catalog entity = new Catalog( //
            cursor.getLong(offset + 0), // category_id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // iconUrl
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Catalog entity, int offset) {
        entity.setCategory_id(cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setIconUrl(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Catalog entity, long rowId) {
        entity.setCategory_id(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Catalog entity) {
        if(entity != null) {
            return entity.getCategory_id();
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
