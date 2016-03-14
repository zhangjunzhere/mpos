package com.itertk.app.mpos.utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.account.InputPasswordDialog;
import com.itertk.app.mpos.comm.LinkeaMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.config.UpdateManager;
import com.itertk.app.mpos.config.UserListFragment;
import com.itertk.app.mpos.dbhelper.AdvertisingDao;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.PermissionDao;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ShopOrder;
import com.itertk.app.mpos.dbhelper.ShopOrderDao;
import com.itertk.app.mpos.dbhelper.ShopOrderItem;
import com.itertk.app.mpos.dbhelper.ShopOrderItemDao;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.dbhelper.UserDao;
import com.itertk.app.mpos.login.LoginActivity;
import com.itertk.app.mpos.trade.pos.BankcardOrderActivity;
import com.itertk.app.mpos.trade.pos.PosDialog;
import com.itertk.app.mpos.trade.taobao.PayShopOrderActivity;
import com.itertk.app.mpos.trade.taobao.WechatPayDialog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by jz on 2015/3/31.
 * 对linkea接口封装
 */
public class LinkeaRequest {

    public final static String KEY_CARD_INFO = "card"; //
    public final static String KEY_TRADE_PRICE  = "key_trade_price";
    public final static String KEY_TRADE_NO = "key_trade_no";
    public  final static String KEY_ORDER_ID="orderid";

    public interface OnRequestResultListener{
        public  void  onSuccess();
        public void onFailure();
    }

    public static  void memberLogin(final Context context, final TextHttpResponseHandler listener){
        final String TAG = "memberLogin";
        final MPosApplication mPosApplication = MPosApplication.getInstance();
        UserDao userDao = mPosApplication.getDataHelper().getUserManager();
        List<User> users = userDao.queryBuilder().where(UserDao.Properties.Type.eq(DataHelper.USER_TPYE_MEMBER)).list();

        if(users.size()  <= 0 ){
            Toast.makeText(context, "需要店主成功登陆过本机一次", Toast.LENGTH_SHORT).show();
            return;
        }

        User member = users.get(0);
        final String userName = member.getClerkName();
        final String password =  AESEncryptor.getInstance(context).encryption(member.getLoginPwd());
        mPosApplication.getMsgBuilder().buildLoginMsg(userName,password).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(TAG, throwable.toString());
                if(listener != null) {
                    listener.onFailure(statusCode, headers, responseString, throwable);
                }
            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                Boolean success = false;
                try {
                    LinkeaResponseMsg.LoginResponseMsg loginResponseMsg = LinkeaResponseMsgGenerator.generateLoginResponseMsg(responseString);
                    success = loginResponseMsg.isSuccess();
                    if (success) {
                        //login success
                        mPosApplication.getMsgBuilder().setAccessToken(loginResponseMsg.token.access_token);
                        mPosApplication.getMsgBuilder().setMember_id(userName);
                        mPosApplication.setOnlineState(true);
                        mPosApplication.setMember(loginResponseMsg);
                    } else {
                        String erroInfo = loginResponseMsg.result_code_msg;
//                        Toast.makeText(context, erroInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, erroInfo);
                    }
                } catch (Exception e) {
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }

                if(listener != null) {
                    if (success) {
                        listener.onSuccess(statusCode, headers, responseString);
                    } else {
                        listener.onFailure(statusCode, headers, responseString, null);
                    }
                }
            }
        });
    }

    public static void getShopOrderStatusMsgResponse(final Context context, final Semaphore semaphore){
        final String TAG = "getShopOrderStatusMsgResponse";
        final MPosApplication mPosApplication = MPosApplication.getInstance();
        mPosApplication.getMsgBuilder().buildShopOrderStatusMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
                semaphore.release();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);

                try{
                    LinkeaResponseMsg.TradeOrderStatusResponseMsg msg = LinkeaResponseMsgGenerator.generateTradeOrderStatusResponseMsg(responseString);
                    if(msg.success){
                        List<LinkeaResponseMsg.TradeOrderStatusResponseMsg.OrderStatus> orderStatuses = msg.mposOrders;
                        ShopOrderDao shopOrderDao = mPosApplication.getDataHelper().getDaoSession().getShopOrderDao();

                        HashMap<String,Integer> statusMap = new HashMap<String,Integer>();
                        for(LinkeaResponseMsg.TradeOrderStatusResponseMsg.OrderStatus orderStatus : msg.mposOrders){
                            statusMap.put(orderStatus.orderNo,orderStatus.status);
                        }
                        List<ShopOrder> items = shopOrderDao.queryBuilder().where(ShopOrderDao.Properties.Trade_no.in(statusMap.keySet())).list();
                        for(ShopOrder item: items){
                            item.setStatus(statusMap.get(item.getTrade_no()));
                        }
                        shopOrderDao.updateInTx(items);
                    }
                }catch (Exception e){
                    if(e != null) {
                        Log.e(TAG,e.toString());
                    }
                }
                semaphore.release();
            }
        });
    }

    public static void getAdvMsgResponse(final Context context, final Semaphore semaphore){
        final String TAG = "getAdvMsgResponse";
        MPosApplication mPosApplication = MPosApplication.getInstance();
        mPosApplication.getMsgBuilder().buildAdvMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
                semaphore.release();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);

                try{
                    LinkeaResponseMsg.AdvResponseMsg advResponseMsg = LinkeaResponseMsgGenerator.generateAdvResonseMsg(responseString);
                    if(advResponseMsg.success) {
                        AdvertisingDao advertisingDao = MPosApplication.getInstance().getDataHelper().getDaoSession().getAdvertisingDao();
                        advertisingDao.insertOrReplaceInTx(advResponseMsg.advertisings);
                    }
                }catch (Exception e){
                    if(e != null) {
                        Log.e(TAG,e.toString());
                    }
                }
                semaphore.release();
            }
        });
    }

    public  static void   getPermissionMsgResponse(final Context context,final Semaphore semaphore){
        final String TAG = "getPermissionMsgResponse";
        MPosApplication mPosApplication = MPosApplication.getInstance();

        mPosApplication.getMsgBuilder().buildPermissionMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
                semaphore.release();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                try{
                    LinkeaResponseMsg.PermissionResponseMsg permissionResponseMsg = LinkeaResponseMsgGenerator.generatePermissionResonseMsg(responseString);
                    if(permissionResponseMsg.success) {
                        PermissionDao permissionDao = MPosApplication.getInstance().getDataHelper().getDaoSession().getPermissionDao();
                        permissionDao.deleteAll();
                        permissionDao.insertOrReplaceInTx(permissionResponseMsg.permissionModel);
                    }
                }catch (Exception e){
                    if(e != null){
                        Log.e(TAG,e.toString());
                    }
                }
                semaphore.release();
            }
        });
    }


    public  static void downloadProductList(final Context context, final  Semaphore semaphore){
        final String TAG = "downloadProductList";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();
        mPosApplication.getMsgBuilder().buildGetProductList().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                semaphore.release();
                Log.e(TAG,  throwable.toString() +" semaphore.availablePermits() " +  semaphore.availablePermits());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString );
                try {
                    LinkeaResponseMsg.ProductListResponseMsg productListResponseMsg =
                            LinkeaResponseMsgGenerator.generateGetProductListResponseMsg(responseString);
                    if(productListResponseMsg.success) {
                        List<Product> prods = productListResponseMsg.getProducts();
                        MPosApplication.getInstance().getDataHelper().getProductManager().insertOrReplaceInTx(prods);
                    }

                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
                semaphore.release();
            }
        });
    }


    public  static void downloadProductCatalog(final Context context, final  Semaphore semaphore){
        final String TAG = "downloadProductCatalog";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();
        mPosApplication.getMsgBuilder().buildProductCategoryMsg().send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                semaphore.release();
                Log.e(TAG, throwable.toString() + " semaphore.availablePermits() " + semaphore.availablePermits());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                try {
                    LinkeaResponseMsg.ProductCatalogMsg productCatalogMsg =
                            LinkeaResponseMsgGenerator.generateProductCatalogMsg(responseString);
                    if(productCatalogMsg.success) {
                        List<Catalog> prods = productCatalogMsg.prods;
                        MPosApplication.getInstance().getDataHelper().getCatalogManager().insertOrReplaceInTx(prods);
                    }

                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
                semaphore.release();
            }
        });
    }

    public static void queryUserListResponse(final Context context,final Semaphore semaphore){
        final String TAG = "queryUserRequest";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();

        LinkeaMsg msg = mPosApplication.getMsgBuilder().buildClerkQueryMsg();
        msg.send(new TextHttpResponseHandler() {
                     @Override
                     public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                         Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                         Log.e(TAG, throwable.toString());
                         semaphore.release();
                     }

                     @Override
                     public void onSuccess(int statusCode, Header[] headers, String responseString) {
                         Log.d(TAG, responseString);
                         try {
                             LinkeaResponseMsg.ClerkQueryResponseMsg msg=  LinkeaResponseMsgGenerator.generateClerkQueryResponseMsg(responseString);
                             if(msg.success) {
                                 UserDao userDao =  MPosApplication.getInstance().getDataHelper().getUserManager();
                                 userDao.queryBuilder().where(UserDao.Properties.Type.notEq(DataHelper.USER_TPYE_MEMBER)).buildDelete().executeDeleteWithoutDetachingEntities(); //remove none administer clerks first
                                 List<User> remoteUsers = msg.clerk_list;
                                 if(remoteUsers != null) {
                                     userDao.insertOrReplaceInTx(remoteUsers);
                                 }
                             }
                         } catch (Exception e) {
                             Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                             Log.d(TAG, e.toString());
                         }
                         semaphore.release();
                     }
                 }
        );
    }

    public static void addUserRequest(final Context context,final User user, final UserListFragment.UserAdapter userAdapter){
        final String TAG = "addUserRequest";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();

        final LoadingDialog dialog = new LoadingDialog(context, R.style.MyDialog);
        dialog.show();

        String member_no = String.valueOf(Utils.getMemberId());
        LinkeaMsg msg = mPosApplication.getMsgBuilder().buildClerkAddMsg(user, member_no);
        msg.send(new TextHttpResponseHandler() {
                     @Override
                     public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                         Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                         Log.e(TAG, throwable.toString());
                         dialog.dismiss();
                     }

                     @Override
                     public void onSuccess(int statusCode, Header[] headers, String responseString) {
                         Log.d(TAG, responseString);
                         dialog.dismiss();
                         try {
                             LinkeaResponseMsg.UserManagerResponseMsg msg =
                                     LinkeaResponseMsgGenerator.generateUserAddResonseMsg(responseString);
                             if (msg.success) {
                                 Toast.makeText(context,"用户添加成功", Toast.LENGTH_SHORT).show();
                                 user.setClerkNo(msg.clerk.getClerkNo());
                                 user.setLoginPwd(AESEncryptor.convertToMD5(user.getLoginPwd()));
                                 userAdapter.addUser(user);
                             }else{
                                 Toast.makeText(context,msg.result_code_msg, Toast.LENGTH_SHORT).show();
                             }
                         } catch (Exception e) {
                             Log.d(TAG, e.toString());
                         }
                     }
                 }

        );
    }

    public static void modifyUserRequest(final Context context, final  User user,final UserListFragment.UserAdapter userAdapter){
        final LoadingDialog dialog = new LoadingDialog(context,R.style.MyDialog);
        dialog.show();

        final String TAG = "modifyUserRequest";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();

        //String member_no = "18069878734";
        LinkeaMsg msg = mPosApplication.getMsgBuilder().buildClerkModMsg(user,user.getPhone());
        msg.send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG,  throwable.toString()) ;
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString );
                dialog.dismiss();

                LinkeaResponseMsg.UserManagerResponseMsg msg =
                        LinkeaResponseMsgGenerator.generateUserUpdateResonseMsg(responseString);
                if (msg.success) {
                    Toast.makeText(context,"数据修改成功", Toast.LENGTH_SHORT).show();
                    user.setLoginPwd(AESEncryptor.convertToMD5(user.getLoginPwd()));
                    userAdapter.modUser(user);
                }else{
                    Toast.makeText(context,msg.result_code_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static void deleteUserRequest(final Context context, final User user, final UserListFragment.UserAdapter userAdapter){
        final String TAG = "deleteUserRequest";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();

        final LoadingDialog dialog = new LoadingDialog(context,R.style.MyDialog);
        dialog.show();

        LinkeaMsg msg = mPosApplication.getMsgBuilder().buildClerkDelMsg(user);
        msg. send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG,  throwable.toString() );
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString );
                dialog.dismiss();
                try {
                    LinkeaResponseMsg.UserManagerResponseMsg msg =
                            LinkeaResponseMsgGenerator.generateUserDeleteResonseMsg(responseString);
                    if (msg.success) {
                        userAdapter.delUser(user);
                    } else {
                        Toast.makeText(context, msg.result_code_msg, Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e(TAG,e.toString());
                }
            }
        });
    }


    public static void GpsMsgUpload(final Activity context, String latitude, String longitude ){
        final String TAG = "GpsMsgUpload";
        final MPosApplication mPosApplication  = MPosApplication.getInstance();

        final LoadingDialog dialog = new LoadingDialog(context,R.style.MyDialog);
        dialog.show();

        LinkeaMsg msg = mPosApplication.getMsgBuilder().buildGpsMsg(latitude,longitude);
        msg. send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(context, "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG,  throwable.toString() );
                dialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString );
                dialog.dismiss();
                try {
                    LinkeaResponseMsg.ResponseMsg  msg =
                            LinkeaResponseMsgGenerator.generateLocationResponseMsg(responseString);

                    if(msg.success){
                        context.startActivity(new Intent(context, LoginActivity.class));
                        SharedPreferences sharedPre = context.getSharedPreferences(LoginActivity.terminalConfig, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPre.edit();
                        editor.putString(LoginActivity.KeyGPSFlag, "UPLOADED");
                        editor.commit();
                        context.finish();
                    }else{
                        Toast.makeText(context, msg.result_code_msg, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            }
        });

    }

    public static void onMemberPay(final Context context, final String trade_no, final LinkeaRequest.OnRequestResultListener   listener){
        final String TAG  ="onMemberPay";
        final InputPasswordDialog inputPasswordDialog=new InputPasswordDialog(context,R.style.MyDialog, null);
        inputPasswordDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Boolean success = inputPasswordDialog.verifySuccess();
                if(success){
                    final   LoadingDialog  loadingDialog = new LoadingDialog(context,R.style.MyDialog, "会员支付中");
                    loadingDialog.show();
                    final String ip = Utils.getWifiIp(context);
                    Log.i("smile", "ip: " + ip);

                    User member = Utils.getMemberFromDB(context);
                    if(member == null){
                        Toast.makeText(context,"没有会员登录过",Toast.LENGTH_SHORT);
                        return;
                    }
                    String memberNo = String.valueOf(member.getClerkNo());
                    String password = Utils.convertToMD5(AESEncryptor.getInstance(context).decryption(member.getLoginPwd()));

                    MPosApplication.getInstance().getMsgBuilder().buildMemberPayOrderMsg(trade_no, memberNo, password).send(
                            new TextHttpResponseHandler() {
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d(TAG, throwable.toString());
                                    Toast.makeText(context, throwable.toString(), Toast.LENGTH_LONG).show();
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                                    Log.d(TAG, responseString);
                                    loadingDialog.dismiss();
                                    try {
                                        LinkeaResponseMsg.ResponseMsg response =
                                                LinkeaResponseMsgGenerator.generatePayOrderResponseMsg(responseString);
                                        if (response.success) {
                                            if (listener != null) {
                                                listener.onSuccess();
                                            }
                                        } else {
                                            String erroInfo = response.result_code_msg;
                                            Toast.makeText(context, erroInfo, Toast.LENGTH_LONG).show();
                                            Log.e(TAG, erroInfo);
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                        Log.d(TAG, e.toString());
                                    }
                                }
                            }
                    );
                }
            }
        });
        inputPasswordDialog.show();


    }

    public static void onWechatPay(Context context, String tradeNo, String totalPrice, String tradeDetail, final LinkeaRequest.OnRequestResultListener listener){
        final WechatPayDialog dialog = new WechatPayDialog(context,R.style.MyWeixinDialog, tradeNo, totalPrice, tradeDetail );
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Boolean success  =  dialog.getPayResult();
                if(success){
                    if(listener != null){
                        listener.onSuccess();
                    }
                }
            }
        });
        dialog.show();
    }

    public static  void onCardPay(final Context context, final String trade_no, final String totalPrice, final long orderId)
    {
        final String TAG  = "onCardPay";

        if(Arith.newBigDecimal(totalPrice).compareTo(Arith.newZeroBigDecimal())<0)
        {
            Toast.makeText(context,"金额不能为零",Toast.LENGTH_LONG).show();
            return;
        }

//        final Intent it = new Intent(context, BankcardOrderActivity.class);
//        Bundle bundle = new Bundle();
////        bundle.putSerializable("card", posDialog.cardInfo);
//        bundle.putString(LinkeaRequest.KEY_TRADE_PRICE, totalPrice);
//        bundle.putString(LinkeaRequest.KEY_TRADE_NO,String.valueOf(trade_no));
//        bundle.putLong(LinkeaRequest.KEY_ORDER_ID, orderId);
//        it.putExtras(bundle);
//        context.startActivity(it);


        final PosDialog posDialog = new PosDialog(context, R.style.MyDialog,Arith.newBigDecimal(totalPrice));
        posDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (posDialog.cardInfo == null) {
                    Log.d(TAG, "获取磁卡信息失败");
                    return;
                } else {
                    Log.d(TAG, posDialog.cardInfo.toString());
                }

                if (posDialog.pin1 == null || posDialog.pin2 == null) {
                    Log.d(TAG, "获取密码失败");
                    return;
                } else {
                    Log.d(TAG, "pin1=" + posDialog.pin1 + " pin2=" + posDialog.pin2);
                }

                posDialog.cardInfo.put("pin", posDialog.pin1);
                posDialog.cardInfo.put("ksn", posDialog.pin2);

                final Intent it = new Intent(context, PayShopOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("card", posDialog.cardInfo);
                bundle.putString(LinkeaRequest.KEY_TRADE_PRICE, totalPrice);
                bundle.putString(LinkeaRequest.KEY_TRADE_NO,String.valueOf(trade_no));
                bundle.putLong(LinkeaRequest.KEY_ORDER_ID, orderId);
                it.putExtras(bundle);
                context.startActivity(it);
            }
        });
        posDialog.show();
    }

    //check update


    public static void checkApplicationUpdate(final Context context, final OnRequestResultListener listener) {
        final String TAG = "checkApplicationUpdate";
        MPosApplication mPosApplication = MPosApplication.getInstance();
        mPosApplication.getMsgBuilder().buildClientUpdateCheck("0.0").send(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d("response", response);

                Boolean waitUpdate = false;

                LinkeaResponseMsg.ClientUpdateCheckResponseMsg msg = LinkeaResponseMsgGenerator.generateClientUpdateCheckResponseMsg(response);
                if(msg.success){
                    try {
                        int remoteVersion = Integer.parseInt(msg.memo);
                        int curVersion = Utils.getVersionCode(context);
                        if (remoteVersion > curVersion ) {
                            Log.d(TAG, "need to update " + remoteVersion + " > " + curVersion);

                            waitUpdate = true;

                            UpdateManager manager = new UpdateManager(context, msg.download_url,  "mpos.apk");
                            manager.setOnUpdateListener(listener);
                            manager.showNoticeDialog();
                        } else {
                            Log.d(TAG, "no need update " + remoteVersion + " <= " + curVersion);
                            Toast.makeText(context, "已经是最新版本", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if(listener != null && !waitUpdate){
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, error.toString());
                if(listener != null){
                    listener.onFailure();
                }
            }
        });

    }

}




