package com.itertk.app.mpos.comm;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.itertk.app.mpos.dbhelper.Advertising;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.Permission;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.User;

import java.util.List;

/**
 * Created by Administrator on 2014/8/1.
 * 返回消息生成类
 */
public class LinkeaResponseMsgGenerator {
    static final String TAG = "LinkeaResponseMsgGenerator";


    public static LinkeaResponseMsg.ResponseMsg generateFiveImagesResponseMsg(String response){
        LinkeaResponseMsg.ResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).fiveimagesUploadResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.BindBankcardResponseMsg generateBindBankcardResponseMsg(String response){
        LinkeaResponseMsg.BindBankcardResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).bindBankcardResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.SignInResponseMsg generateSignInResponseMsg(String response){
        LinkeaResponseMsg.SignInResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).signInResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.CreateOrderResponseMsg generateCreateOrderResponseMsg(String response){
        LinkeaResponseMsg.CreateOrderResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).createOrderResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.ResponseMsg generatePayOrderResponseMsg(String response){
        LinkeaResponseMsg.ResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).payOrderResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.PaySignUploadResponseMsg generatePaySignResponseMsg(String response){
        LinkeaResponseMsg.PaySignUploadResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).paySignUploadResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.WechatOrderResponseMsg generateWechatOrderResponseMsg(String response){
        LinkeaResponseMsg.WechatOrderResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).wechatOrderResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.BalanceInfoGetResponseMsg generateBalanceInfoGetResponseMsg(String response){
        LinkeaResponseMsg.BalanceInfoGetResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).balanceInfoGetResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.PhoneChargeOrderCreateResponseMsg generatePhoneChargeOrderCreateResponseMsg(String response){
        LinkeaResponseMsg.PhoneChargeOrderCreateResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).phoneChargeOrderCreateResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.PhoneInfoGetResponseMsg generatePhoneInfoGetResponseMsg(String response){
        LinkeaResponseMsg.PhoneInfoGetResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).phoneInfoGetResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.TrainTicketQueryResponseMsg generateTrainTicketQueryResponseMsg(String response){
        LinkeaResponseMsg.TrainTicketQueryResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).trainTicketQueryResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.AirTicketQueryResponseMsg generateAirTicketQueryResponseMsg(String response){
        LinkeaResponseMsg.AirTicketQueryResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).airTicketQueryResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.ImageUploadsResponseMsg generateImageUploadsResponseMsg(String response){
        LinkeaResponseMsg.ImageUploadsResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).imageUploadsResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.UserImageUploadResponseMsg generateUserImageUploadResponseMsg(String response){
        LinkeaResponseMsg.UserImageUploadResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).userImageUploadResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.BusinessImageUploadResponseMsg generateBusinessImageUploadResponseMsg(String response){
        LinkeaResponseMsg.BusinessImageUploadResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).businessImageUploadResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.ClientUpdateCheckResponseMsg generateClientUpdateCheckResponseMsg(String response){
        LinkeaResponseMsg.ClientUpdateCheckResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).clientUpdateCheckResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.GetBankBranchResponseMsg generateGetBankBranchResponseMsg(String response){
        LinkeaResponseMsg.GetBankBranchResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        Log.d(TAG, response);

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).getBankBranchResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.UpdatePasswordResponseMsg generateUpdatePasswordResponseMsg(String response){
        LinkeaResponseMsg.UpdatePasswordResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).updatePasswordResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.ResetPasswordResponseMsg generateResetPasswordResponseMsg(String response){
        LinkeaResponseMsg.ResetPasswordResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).resetPasswordResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.GetSmsCodeResponseMsg generateGetSmsCodeResponseMsg(String response){
        LinkeaResponseMsg.GetSmsCodeResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).getSmsCodeResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.UserInfoResponseMsg generateUserInfoResponseMsg(String response){
        LinkeaResponseMsg.UserInfoResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).userInfoResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.LogoutResponseMsg generateLogoutResponseMsg(String response){
        LinkeaResponseMsg.LogoutResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).logoutResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.TerminalActivationResponseMsg generateTerminalActivationResponseMsg(String response){
        LinkeaResponseMsg.TerminalActivationResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).terminalActivationResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.UserActivateResponseMsg generateUserActivateResponseMsg(String response){
        LinkeaResponseMsg.UserActivateResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).userActivateResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.RegisterResponseMsg generateRegisterResponseMsg(String response){
        LinkeaResponseMsg.RegisterResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).registerResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.LoginResponseMsg generateLoginResponseMsg(String response){
        LinkeaResponseMsg.LoginResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).loginResponseMsg;
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }

    public static LinkeaResponseMsg.ProductListResponseMsg generateGetProductListResponseMsg(String response){
        LinkeaResponseMsg.ProductListResponseMsg responseMsg = null;

        if(response == null){
            Log.e(TAG, "response is null");
            return responseMsg;
        }

        try {
            responseMsg =  (new Gson()).fromJson(response, LinkeaResponseMsg.class).productListResponseMsg;

        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        return responseMsg;
    }


   
    public static LinkeaResponseMsg.AdvResponseMsg generateAdvResonseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).advResponseMsg;
    }

    public static LinkeaResponseMsg.ResponseMsg generateMemberBindResonseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).memberBindResponseMsg;
    }

    public static LinkeaResponseMsg.PermissionResponseMsg generatePermissionResonseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).permissionResponseMsg;
    }

    public static LinkeaResponseMsg.UserManagerResponseMsg generateUserAddResonseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).userManagerResponseMsg;
    }

    public static LinkeaResponseMsg.UserManagerResponseMsg generateUserUpdateResonseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).userUpdateResponseMsg;
    }

    public static LinkeaResponseMsg.UserManagerResponseMsg generateUserDeleteResonseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).userDeleteResponseMsg;
    }


    public static LinkeaResponseMsg.ProductCatalogMsg generateProductCatalogMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).productCatalogMsg;
    }

    public static LinkeaResponseMsg.ClerkQueryResponseMsg generateClerkQueryResponseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).clerkQueryResponseMsg;
    }

    public static LinkeaResponseMsg.ResponseMsg generateLocationResponseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).locationMsg;
    }

    public static LinkeaResponseMsg.CancelOrderResponseMsg generateCancelOrderResponseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).cancelOrderResponseMsg;
    }

    public static LinkeaResponseMsg.TradeOrderStatusResponseMsg generateTradeOrderStatusResponseMsg(String jsonStr){
        return new Gson().fromJson(jsonStr,LinkeaResponseMsg.class).tradeOrderStatusResponseMsg;
    }



}
