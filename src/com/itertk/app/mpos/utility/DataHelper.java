package com.itertk.app.mpos.utility;

import android.content.Context;

import com.itertk.app.mpos.dbhelper.AttributeDao;
import com.itertk.app.mpos.dbhelper.AttributeValueDao;
import com.itertk.app.mpos.dbhelper.BackupDao;
import com.itertk.app.mpos.dbhelper.CartDao;
import com.itertk.app.mpos.dbhelper.CatalogDao;
import com.itertk.app.mpos.dbhelper.DaoMaster;
import com.itertk.app.mpos.dbhelper.DaoSession;
import com.itertk.app.mpos.dbhelper.ProductAttributeDao;
import com.itertk.app.mpos.dbhelper.ProductDao;
import com.itertk.app.mpos.dbhelper.ReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemAttributeValueDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItemReduceDao;
import com.itertk.app.mpos.dbhelper.SaleOrderReduceDao;
import com.itertk.app.mpos.dbhelper.ShopDao;
import com.itertk.app.mpos.dbhelper.SystemDao;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.dbhelper.UserDao;

import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Administrator on 2014/7/21.
 * 对mpos dbhelper的封装类
 */
public class DataHelper {
    private Context context;
    private static final String TAG = "DatabaseHelper";

    public static  final String LOCAL_SUPPLIER=""; // local product added
    public static  final long  USER_TPYE_MEMBER=0L; //会员
    public static  final long USER_TPYE_ADMIN=1L; // 店长
    public static  final long USER_TPYE__CLERK=2L; //店员


    private String DATABASE_NAME = "mpos.db";

    private DaoMaster daoMaster;
    private DaoSession daoSession;


    public User userLogin(Context context, String username, String password){
        String passwordMd5 = AESEncryptor.convertToMD5(password);
        String passwordAES = AESEncryptor.getInstance(context).encryption(password);

        QueryBuilder qb = getUserManager().queryBuilder();
        qb.where(qb.or(UserDao.Properties.LoginPwd.eq(password), UserDao.Properties.LoginPwd.eq(passwordAES), UserDao.Properties.LoginPwd.eq(passwordMd5)),qb.or(UserDao.Properties.ClerkName.eq(username), UserDao.Properties.Phone.eq(username) ));
        List<User> userList = qb.list();

       if (userList.size() == 0) return null;
        else return userList.get(0);
    }

    public void userLogout(){

    }

    public DataHelper(Context context, String database){
        DATABASE_NAME = database;
        this.context = context;
        daoMaster = getDaoMaster();
        daoSession = getDaoSession();
    }


    public DataHelper(Context context){
        this.context = context;
        daoMaster = getDaoMaster();
        daoSession = getDaoSession();
    }

    private  DaoMaster getDaoMaster()
    {
        if (daoMaster == null)
        {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(DataHelper.this.context, DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public  DaoSession getDaoSession()
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public UserDao getUserManager(){
        return daoSession.getUserDao();
    }

    public ShopDao getShopManager() {
        return daoSession.getShopDao();
    }

    public SystemDao getSystemManager() {
        return daoSession.getSystemDao();
    }

    public BackupDao getBackupManager() {
        return daoSession.getBackupDao();
    }

    public CatalogDao getCatalogManager() {
        return daoSession.getCatalogDao();
    }

    public ProductDao getProductManager() {
        return daoSession.getProductDao();
    }

    public AttributeDao getAttributeManager() {
        return daoSession.getAttributeDao();
    }

    public ReduceDao getReduceManager() {
        return daoSession.getReduceDao();
    }

    public AttributeValueDao getAttributeValueManager() {
        return daoSession.getAttributeValueDao();
    }

    public ProductAttributeDao getProductAttributeManager() {
        return daoSession.getProductAttributeDao();
    }

    public SaleOrderDao getSaleOrderManager() {
        return daoSession.getSaleOrderDao();
    }

    public SaleOrderItemDao getSaleOrderItemManager() {
        return daoSession.getSaleOrderItemDao();
    }

    public SaleOrderItemReduceDao getSaleOrderItemReduceManager() {
        return daoSession.getSaleOrderItemReduceDao();
    }

    public SaleOrderItemAttributeValueDao getSaleOrderItemAttributeValueManager() {
        return daoSession.getSaleOrderItemAttributeValueDao();
    }

    public SaleOrderReduceDao getSaleOrderReduceManager() {
        return daoSession.getSaleOrderReduceDao();
    }

    public CartDao getCartManager() {
        return daoSession.getCartDao();
    }

}
