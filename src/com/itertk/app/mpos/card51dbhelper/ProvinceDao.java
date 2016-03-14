package com.itertk.app.mpos.card51dbhelper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * DAO for table PROVINCE.
 */
public class ProvinceDao extends AbstractDao<Province, Long> {

	public static final String TABLENAME = "PROVINCE";

	/**
	 * Properties of entity Province.<br/>
	 * Can be used for QueryBuilder and for referencing column names.
	 */
	public static class Properties {
		public final static Property ProvinceId = new Property(0, Long.class,
				"provinceId", true, "PROVINCE_ID");
		public final static Property Code = new Property(1, String.class,
				"code", false, "CODE");
		public final static Property Name = new Property(2, String.class,
				"name", false, "NAME");
	};

	private DaoSession daoSession;

	public ProvinceDao(DaoConfig config) {
		super(config);
	}

	public ProvinceDao(DaoConfig config, DaoSession daoSession) {
		super(config, daoSession);
		this.daoSession = daoSession;
	}

	/** Creates the underlying database table. */
	public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
		String constraint = ifNotExists ? "IF NOT EXISTS " : "";
		db.execSQL("CREATE TABLE " + constraint + "'PROVINCE' (" + //
				"'PROVINCE_ID' INTEGER PRIMARY KEY ," + // 0: provinceId
				"'CODE' TEXT NOT NULL ," + // 1: code
				"'NAME' TEXT NOT NULL );"); // 2: name
	}

	/** Drops the underlying database table. */
	public static void dropTable(SQLiteDatabase db, boolean ifExists) {
		String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "")
				+ "'PROVINCE'";
		db.execSQL(sql);
	}

	/** @inheritdoc */
	@Override
	protected void bindValues(SQLiteStatement stmt, Province entity) {
		stmt.clearBindings();

		Long provinceId = entity.getProvinceId();
		if (provinceId != null) {
			stmt.bindLong(1, provinceId);
		}
		stmt.bindString(2, entity.getCode());
		stmt.bindString(3, entity.getName());
	}

	@Override
	protected void attachEntity(Province entity) {
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
	public Province readEntity(Cursor cursor, int offset) {
		Province entity = new Province( //
				cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // provinceId
				cursor.getString(offset + 1), // code
				cursor.getString(offset + 2) // name
		);
		return entity;
	}

	/** @inheritdoc */
	@Override
	public void readEntity(Cursor cursor, Province entity, int offset) {
		entity.setProvinceId(cursor.isNull(offset + 0) ? null : cursor
				.getLong(offset + 0));
		entity.setCode(cursor.getString(offset + 1));
		entity.setName(cursor.getString(offset + 2));
	}

	/** @inheritdoc */
	@Override
	protected Long updateKeyAfterInsert(Province entity, long rowId) {
		entity.setProvinceId(rowId);
		return rowId;
	}

	/** @inheritdoc */
	@Override
	public Long getKey(Province entity) {
		if (entity != null) {
			return entity.getProvinceId();
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
