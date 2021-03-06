package com.itertk.app.mpos.card51dbhelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.itertk.app.mpos.R;

import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 1000): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
	public static final int SCHEMA_VERSION = 1000;

	/** Creates underlying database table using DAOs. */
	public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
		ProvinceDao.createTable(db, ifNotExists);
		CityDao.createTable(db, ifNotExists);
		CityAreaDao.createTable(db, ifNotExists);
	}

	/** Drops underlying database table using DAOs. */
	public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
		ProvinceDao.dropTable(db, ifExists);
		CityDao.dropTable(db, ifExists);
		CityAreaDao.dropTable(db, ifExists);
	}

	public static abstract class OpenHelper extends SQLiteOpenHelper {
		Context context;

		public OpenHelper(Context context, String name, CursorFactory factory) {
			super(context, name, factory, SCHEMA_VERSION);
			this.context = context;
			checkDbFile();
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i("greenDAO", "Creating tables for schema version "
					+ SCHEMA_VERSION);
			createAllTables(db, false);
		}

		private void checkDbFile() {
			File file = context.getDatabasePath("lyj51card.db");
			if (file.exists()) {
				Log.d("dbmaster",
						"lyj51card is exist................................................................................................................");
				return;
			} else {
				Log.d("dbmaster",
						"lyj51card not exist copy it!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				InputStream is = context.getResources().openRawResource(
						R.raw.lyj51card);
				FileOutputStream fileOutputStream = null;
				try {
					// file.createNewFile();
					fileOutputStream = new FileOutputStream(file);
					int len = is.available();
					byte[] data = new byte[len];
					is.read(data);
					fileOutputStream.write(data);
					fileOutputStream.flush();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
						if (fileOutputStream != null) {
							fileOutputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	/** WARNING: Drops all table on Upgrade! Use only during development. */
	public static class DevOpenHelper extends OpenHelper {
		public DevOpenHelper(Context context, String name, CursorFactory factory) {
			super(context, name, factory);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i("greenDAO", "Upgrading schema from version " + oldVersion
					+ " to " + newVersion + " by dropping all tables");
			dropAllTables(db, true);
			onCreate(db);
		}
	}

	public DaoMaster(SQLiteDatabase db) {
		super(db, SCHEMA_VERSION);
		registerDaoClass(ProvinceDao.class);
		registerDaoClass(CityDao.class);
		registerDaoClass(CityAreaDao.class);
	}

	public DaoSession newSession() {
		return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
	}

	public DaoSession newSession(IdentityScopeType type) {
		return new DaoSession(db, type, daoConfigMap);
	}

}
