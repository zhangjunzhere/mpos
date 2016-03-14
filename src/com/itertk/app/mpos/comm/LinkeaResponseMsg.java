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
 * 返回消息
 */
public class LinkeaResponseMsg {
    static final String TAG = "LinkeaResponseMsg";

    @SerializedName("linkea_mpos_user_oauth_login_response")
    public LoginResponseMsg loginResponseMsg;

    @SerializedName("linkea_mpos_member_register_response")
    public RegisterResponseMsg registerResponseMsg;

    @SerializedName("linkea_mpos_query_baseList_response")
    public ProductListResponseMsg productListResponseMsg;

    @SerializedName("linkea_user_activation_response")
    public UserActivateResponseMsg userActivateResponseMsg;

    @SerializedName("linkea_terminal_largeTermMposActivate_response")
    public TerminalActivationResponseMsg terminalActivationResponseMsg;

    @SerializedName("linkea_user_oauth_logout_response")
    public LogoutResponseMsg logoutResponseMsg;

    @SerializedName("linkea_user_oauth_get_response")
    public UserInfoResponseMsg userInfoResponseMsg;

    @SerializedName("linkea_user_password_forgot_smscode_get_response")
    public GetSmsCodeResponseMsg getSmsCodeResponseMsg;

    @SerializedName("linkea_user_password_forgot_reset_response")
    public ResetPasswordResponseMsg resetPasswordResponseMsg;

    @SerializedName("linkea_user_loginpassword_update_response")
    public UpdatePasswordResponseMsg updatePasswordResponseMsg;

    @SerializedName("linkea_util_bank_branch_get_response")
    public GetBankBranchResponseMsg getBankBranchResponseMsg;

    @SerializedName("linkea_terminal_client_update_check_response")
    public ClientUpdateCheckResponseMsg clientUpdateCheckResponseMsg;


    @SerializedName("linkea_user_auth_image_uploadBus_response")
    public BusinessImageUploadResponseMsg businessImageUploadResponseMsg;

    @SerializedName("linkea_user_auth_image_upload_response")
    public UserImageUploadResponseMsg userImageUploadResponseMsg;

    @SerializedName("linkea_user_auth_image_uploads_response")
    public ImageUploadsResponseMsg imageUploadsResponseMsg;

    @SerializedName("linkea_trade_order_create_response")
    public CreateOrderResponseMsg createOrderResponseMsg;

    @SerializedName("linkea_trade_order_pay_response")
    public ResponseMsg payOrderResponseMsg;

    @SerializedName("linkea_trade_order_fastpay_response")
    public WechatOrderResponseMsg wechatOrderResponseMsg;


    @SerializedName("linkea_jipiao_get_response")
    public AirTicketQueryResponseMsg airTicketQueryResponseMsg;

    @SerializedName("linkea_train_schedule_get_response")
    public TrainTicketQueryResponseMsg trainTicketQueryResponseMsg;

    @SerializedName("linkea_recharge_phone_home_get_response")
    public PhoneInfoGetResponseMsg phoneInfoGetResponseMsg;

    @SerializedName("linkea_recharge_phone_order_create_response")
    public PhoneChargeOrderCreateResponseMsg phoneChargeOrderCreateResponseMsg;

    @SerializedName("linkea_trade_all_getbalance_response")
    public BalanceInfoGetResponseMsg balanceInfoGetResponseMsg;


    @SerializedName("linkea_terminal_pos_signin_response")
    public SignInResponseMsg signInResponseMsg;

    @SerializedName("linkea_user_bankbind_update1_response")
    public BindBankcardResponseMsg bindBankcardResponseMsg;

    @SerializedName("linkea_mpos_adv_download_response")
    public AdvResponseMsg advResponseMsg;

    @SerializedName("linkea_mpos_get_paySgin_response")
    public PaySignUploadResponseMsg paySignUploadResponseMsg;

    @SerializedName("linkea_mpos_add_termId2Shop_response")
    public ResponseMsg memberBindResponseMsg;

    @SerializedName("linkea_mpos_images_upload_response")
    public ResponseMsg fiveimagesUploadResponseMsg;

    @SerializedName("linkea_terminal_query_permissions_response")
    PermissionResponseMsg permissionResponseMsg;

    @SerializedName("linkea_mpos_clerk_add_response")
    UserManagerResponseMsg userManagerResponseMsg;

    @SerializedName("linkea_mpos_clerk_update_response")
    UserManagerResponseMsg userUpdateResponseMsg;

    @SerializedName("linkea_mpos_clerk_delete_response")
    UserManagerResponseMsg userDeleteResponseMsg;

    @SerializedName("linkea_mpos_query_prodList_response")
    ProductCatalogMsg productCatalogMsg;

    @SerializedName("linkea_mpos_clerk_query_termId_response")
    ClerkQueryResponseMsg clerkQueryResponseMsg;

    @SerializedName("linkea_location_info_do_response")
    ResponseMsg locationMsg;

    @SerializedName("linkea_mpos_order_cancel_response")
    CancelOrderResponseMsg cancelOrderResponseMsg;

    @SerializedName("linkea_mpos_query_order_status_memberId_response")
    TradeOrderStatusResponseMsg tradeOrderStatusResponseMsg;

    //base response class
    public class ResponseMsg{
        public Boolean success;
        public int result_code;
        public String result_code_msg;
        public String sign;
    }

    public class LoginResponseMsg {
        public Token token;
        public Member member;
        public Authenticate authenticate;

        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            str += (token == null ? " token = null" : " token=" + token.toString());
            str += (member == null ? " member = null" : " member=" + member.toString());
            str += (authenticate == null ? " authenticate = null" : " authenticate=" + authenticate.toString());

            return str;
        }

        //public String
        public class Token {
            @Override
            public String toString() {
                return " access_token=" + access_token
                        + " expires_in=" + expires_in
                        + " token_type=" + token_type;
            }

            public String access_token;
            public String expires_in;
            public String token_type;
        }

        public class Member {
            @Override
            public String toString() {
                return " amount=" + amount
                        + " bank_name=" + bank_name
                        + " card_holder=" + card_holder
                        + " card_no=" + card_no
                        + " member_name=" + member_name
                        + " member_id=" + member_id
                        + " to_cash_amount=" + to_cash_amount
                        + " phone=" + phone
                        + " sex=" + sex;
            }

            public String amount;
            public String bank_name;
            public String card_holder;
            public String card_no;
            public String member_name;
            public String member_id;
            public String to_cash_amount;
            public  String phone;
            public String sex;
            public void setTo_cash_amount(String newToCashMount){
                to_cash_amount=newToCashMount;
            }
            public void setcash_amount(String newCashMount){
                amount=newCashMount;
            }
        }

        public class Authenticate {
            @Override
            public String toString() {
                return "status=" + status
                        + " status_msg=" + status_msg
                        + " bank_img_url=" + bank_image_url
                        + " bank_state=" + bank_state
                        + " bank_state_msg=" + bank_state_msg
                        + " head_img_url=" + head_image_url;
            }

            public String status;
            public String status_msg;
            public String bank_image_url;
            public String bank_state;
            public String bank_state_msg;
            public String head_image_url;
            public String buss_image_url;
            public String id_image_url;
        }

    }

    public class RegisterResponseMsg {
        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("15")||success.equals("success"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }



    public class UserActivateResponseMsg {
        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }

    public class TerminalActivationResponseMsg{
        public String result_code;
        public String result_code_msg;
        public boolean success;
        public String sign;
        public Terminal terminal;


        public class Terminal {
            @Override
            public String toString() {
                return " terminal_id="+term_id + " term_mac=" + term_mac;
            }
            public String term_id;
            public String term_mac;
        }
    }

    public class LogoutResponseMsg{
        String result_code;
        String result_code_msg;
        String success;
        String sign;

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }

    public class UserInfoResponseMsg{
        String result_code;
        public String result_code_msg;
        public boolean success;
        String sign;
        public Member member;

        public class Member {
            @Override
            public String toString() {
                return " amount=" + amount
                        + " bank_name=" + bank_name
                        + " card_holder=" + card_holder
                        + " card_no=" + card_no
                        + " member_name=" + member_name
                        + " member_id=" + member_id
                        + " to_cash_amount=" + to_cash_amount
                        + " phone=" + phone
                        + " sex=" + sex;
            }

            public String amount;
            public String bank_name;
            public String card_holder;
            public String card_no;
            public String member_name;
            public String member_id;
            public String to_cash_amount;
            public String phone;
            public String sex;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }

    public class GetSmsCodeResponseMsg{
        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }

    public class ResetPasswordResponseMsg{
        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }

    public class UpdatePasswordResponseMsg{
        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;
            return str;
        }
    }

    public class GetBankBranchResponseMsg{
        public String result_code;
        public String result_code_msg;
        public String success;
        public String sign;
        public Paginator paginator;
        public List<Branch> branchs;

        public class Branch{
            public Branch(){}
            public String code;
            public String name;

            @Override
            public String toString() {
                return " code=" + code
                        + " name=" + name;
            }
        }

        public class Paginator{
            public String items;
            public String page_size;
            public String page;

            @Override
            public String toString() {
                return " items=" + items
                        + " page_size=" + page_size
                        + " page=" + page;
            }
        }

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;

            for(Branch branch: branchs){
                str += branch.toString();
            }

            str += paginator.toString();

            return str;
        }

    }

    public class ClientUpdateCheckResponseMsg{
        public String download_url;
        public String final_version;
        public boolean is_enforce_update;
        public boolean is_final_version;
        public String memo;
        public Integer result_code;
        public boolean success;

        public boolean isSuccess(){
            return success;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " success = " + success
                    + " download_url = " + download_url
                    + " final_version = " + final_version
                    + " is_enforce_update = " + is_enforce_update
                    + " is_final_version = " + is_final_version
                    + " memo = " + memo;
            return str;
        }
    }

    public class BusinessImageUploadResponseMsg{
        String result_code;
        String success;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " success = " + success;
            return str;
        }
    }

    public class UserImageUploadResponseMsg{
        String result_code;
        String success;

        public boolean isSuccess(){
            if (result_code != null
                    && result_code.equals("00"))
                return true;
            return false;
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " success = " + success;
            return str;
        }
    }

    public class BindBankcardResponseMsg{
        public int autu_state;
        public String autu_status;
        public int bank_state;
        public String bank_status;
        public int result_code;
        public boolean success;
        public String result_code_msg;

        @Override
        public String toString() {
            String str = "autu_state=" + autu_state
                    + " autu_status = " + autu_status
                    + " bank_state = " + bank_state
                    + " bank_status = " + bank_status
                    + " result_code = " + result_code
                    + " success = " + success;
            return str;
        }
    }

    public class ImageUploadsResponseMsg{
        String result_code;
        String success;

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " success = " + success;
            return str;
        }
    }

    public class CreateOrderResponseMsg{
        public Integer result_code;
        public Boolean success;
        public String sign;
        public String result_code_msg;
        public Order order;

        public class Order{
            public Amount amount;
            public String trade_no;
            public String biz_code;
            public String ext_order_no;
            public String status_msg;
            public Integer status;

            public String toString(){
                String str = "amount=" + amount.toString()
                        + " trade_no" + trade_no
                        + " biz_code" + biz_code
                        + " ext_order_no" + ext_order_no
                        + " status_msg" + status_msg
                        + " status" + status;

                return str;
            }

            public class Amount{
                public String amount;
                public String cent;

                public String toString(){
                    return "amount="+ amount + "cent=" + cent;
                }
            }
        }

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg = " + result_code_msg
                    + " sign = " + sign
                    + " order=" + order.toString()
                    + " success = " + success;
            return str;
        }
    }
    public class WechatOrderResponseMsg{
        public String result_code;
        public Boolean success;
        public String result_code_msg;

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " success = " + success;
            return str;
        }
    }
//    public class PayOrderResponseMsg{
//        public String result_code;
//        public Boolean success;
//        public String result_code_msg;
//
//        @Override
//        public String toString() {
//            String str = "result_code=" + result_code
//                    + " success = " + success;
//            return str;
//        }
//    }

    public class AirTicketQueryResponseMsg{
        public String result_code;
        public Boolean success;
        public List<FlightInfo> flightInfos;

        public boolean isSuccess(){
           return success;
        }

        public String toString(){
            String str = "result_code=" + result_code
                    + "success=" + success;

            for (FlightInfo flightInfo:flightInfos){
                str += flightInfo.toString();
            }

            return str;
        }

        public class FlightInfo{
            public String airline;
            public String airportTax;
            public String arrival;
            public String arrivalTime;
            public String costFare;
            public String departure;
            public String departureTime;
            public String flightNo;
            public String fuelTax;
            public String isCodeShare;
            public String isFood;
            public String planeType;
            public String priceFare;
            public String stopNo;
            public List<CabinInfo> cabinInfos;

            public String toString(){
                String str ="airline=" + airline
                        + "airportTax=" + airportTax
                        + "arrival=" + arrival
                        + "costFare=" + costFare
                        + "departure=" + departure
                        + "departureTime=" + departureTime
                        + "flightNo=" + flightNo
                        + "fuelTax=" + fuelTax
                        + "isCodeShare=" + isCodeShare
                        + "isFood=" + isFood
                        + "planeType=" + planeType
                        + "priceFare=" + priceFare
                        + "stopNo=" + stopNo
                        + "arrivalTime=" + arrivalTime;

                for (CabinInfo cabinInfo: cabinInfos){
                    str += cabinInfo.toString();
                }

                return str;
            }

            public class CabinInfo{
                public String airportFee;
                public String baseCabin;
                public String cabinInfo;
                public String cabinName;
                public String cost;
                public String discountRate;
                public String fare;
                public String oilFee;
                public String policyCode;

                public String toString(){
                    return "airportFee=" + airportFee
                            + "baseCabin=" + baseCabin
                            + "cabinInfo=" + cabinInfo
                            + "cabinName=" + cabinName
                            + "cost=" + cost
                            + "discountRate=" + discountRate
                            + "fare=" + fare
                            + "oilFee=" + oilFee
                            + "policyCode=" + policyCode;
                }
            }

        }
    }

    public class TrainTicketQueryResponseMsg{
        public Integer result_code;
        public Boolean success;
        public String result_code_msg;
    }

    public class PhoneInfoGetResponseMsg{
        public String area;  //1586880|浙江杭州|移动
        public Integer result_code;
        public String result_code_msg;
        public Boolean success;
        public String sign;


        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " sign=" + sign
                    + " area=" + area
                    + " success = " + success;
            return str;
        }
    }

    public class PhoneChargeOrderCreateResponseMsg{
        public String result_code;
        public String result_code_msg;
        public Boolean success;
        public String sign;
        public String trade_no;


        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " sign=" + sign
                    + " trade_no=" + trade_no
                    + " success = " + success;
            return str;
        }
    }

    public class BalanceInfoGetResponseMsg{
        public int result_code;
        public String result_code_msg;
        public Boolean success;
        public String sign;
        public Balance balance;

        public class Balance{
            public String amount;
            public String cent;

            public String toString(){
                return "amount=" + amount + " cent=" + cent;
            }
        }


        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " sign=" + sign
                    + " area=" + balance.toString()
                    + " success = " + success;
            return str;
        }
    }

    public class SignInResponseMsg{
        public Integer result_code;
        public String result_code_msg;
        public Boolean success;
        public String sign;
        public String tmkMacKey;
        public String tmkPinKey;
        public String tmkTakKey;
        public String trackCheckValue;
        public String macCheckValue;
        public String pinCheckValue;


        public boolean isSuccess(){ return success;}

        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " sign=" + sign
                    + " tmkMacKey=" + tmkMacKey
                    + " tmkPinKey=" + tmkPinKey
                    + " tmkTakKey=" + tmkTakKey
                    + " trackCheckValue=" + trackCheckValue
                    + " macCheckValue=" + macCheckValue
                    + " pinCheckValue=" + pinCheckValue
                    + " success = " + success;
            return str;
        }
    }

    public class ProductListResponseMsg{
        public String result_code;
        public String result_code_msg;
        public Boolean success;
        public String sign;

        public List<Product> prods;
        public List<Product> getProducts(){
        	return prods;
        }
        
        @Override
        public String toString() {
            String str = "result_code=" + result_code
                    + " result_code_msg=" + result_code_msg
                    + " success = " + success
                    + " sign = " + sign;

            for(Product product: prods){
                str += product.toString();
            }
            return str;
        }
    }

    public static class AdvResponseMsg{
        public Boolean success;
        public List<Advertising> advertisings;
        @Override
        public String toString(){
            return new Gson().toJson(this);
        }
    }

    public static class PaySignUploadResponseMsg{
        public Boolean success;
        public String result_code;
        public String signImagePath;
        @Override
        public String toString(){
            return new Gson().toJson(this);
        }
    }



    public  class PermissionResponseMsg{
        public boolean success;
        public List<Permission> permissionModel;
    }


    public  class UserManagerResponseMsg{
        public boolean success;
        public String result_code_msg;
        public User clerk;
    }



    public class ProductCatalogMsg{
        public String resultCode;
        public Boolean success;
        public List<Catalog> prods;

    }

    public class ClerkQueryResponseMsg{
        public Boolean success;
        public String result_code_msg;
        public List<User> clerk_list;
    }

    public class CancelOrderResponseMsg{
        public Boolean base_success;
        public String base_resultCode;
        public String base_resultMsg;
    }

    public class TradeOrderStatusResponseMsg extends ResponseMsg{
        public List<OrderStatus> mposOrders;
        public class OrderStatus{
            public String orderNo;
            public String supplierId;
            public int status;
        }
    }

}
