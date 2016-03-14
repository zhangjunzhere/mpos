package com.itertk.app.mpos.comm;

import android.location.Address;

import com.google.gson.Gson;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.utility.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2014/7/30.
 * 请求消息
 */


public class LinkeaMsgBuilder {
    AsyncHttpClient client;
    AsyncHttpClient syncClient;
    private String access_token = "mpos";
    private String term_id = "T0128662";
    private String term_mac = "D7C89CFAE8BF41E4AB069D4D42EEA8B7";
    private String member_id = "";
    private String   longitude = "";
    private String   latitude = "";

    public LinkeaMsgBuilder() {
        client = new AsyncHttpClient();
        //client.setTimeout(3000);
        syncClient = new SyncHttpClient();

        client.setTimeout(20);
        //client.setMaxRetriesAndTimeout(4, 4);
    }

    public LinkeaMsg buildSignInMsg(){
        return new SignInMsg(client);
    }

    public LinkeaMsg buildLoginMsg(String userName, String password) {
        return new LoginMsg(client, userName, password);
    }

    public LinkeaMsg buildRegisterMsg(String phone,String password, String userName, String shop_adr,String shop_name,String shop_type,String term_ip,String cardId) {
        return new RegisterMsg(client, phone,password, userName, shop_adr,shop_name,shop_type,term_ip,cardId);
    }

    public LinkeaMsg buildUserActivateMsg(String memberID, String code) {
        return new UserActivateMsg(client, memberID, code);
    }

    public LinkeaMsg buildTerminalActivationMsg(String device_id, String pos_id) {
        return new TerminalActivation(client, device_id, pos_id);
    }

    public LinkeaMsg buildLogoutMsg() {
        return new LogoutMsg(client);
    }



    public LinkeaMsg buildUserInfoMsg() {
        return new UserInfoMsg(client);
    }

    public LinkeaMsg buildGetSmsCodeMsg() {
        return new GetSmsCodeMsg(client);
    }

    public LinkeaMsg buildResetPasswordMsg(String code, String password) {
        return new ResetPasswordMsg(client, code, password);
    }

    public LinkeaMsg buildUpdatePasswordMsg(String oldPassword, String newPassword) {
        return new UpdatePasswordMsg(client, oldPassword, newPassword);
    }

    public LinkeaMsg buildGetBankBranch(String bankCode, String keyWord, String page, String pageSize) {
        return new GetBankBranch(client, bankCode, keyWord, page, pageSize);
    }

    public LinkeaMsg buildBindBankcardMsg(String authStatus, String bankStatus, String bankImagData, String bankImgName, String bankName, String cardHolder, String cardNo) {
        return new BindBankcardMsg(client, authStatus, bankStatus, bankImagData, bankImgName, bankName, cardHolder, cardNo);
    }

    public LinkeaMsg buildClientUpdateCheck(String version) {
        return new ClientUpdateCheck(client, version);
    }

    public LinkeaMsg buildSyncClientUpdateCheck(String version) {
        return new ClientUpdateCheck(syncClient, version);
    }

    public LinkeaMsg buildMPosImagesUpload(String accountImgName, String accountImgData,String idImgName, String idImgData,
                                           String businessImgName, String businessImgData, String taxImgName, String taxImgData,
                                           String orgImgName, String orgImgData, String headImgName, String headImgData,String certNum){
        return new MPosImagesUpload(client, accountImgName, accountImgData, idImgName, idImgData,
                businessImgName, businessImgData, taxImgName, taxImgData, orgImgName, orgImgData, headImgName, headImgData,certNum);
    }

    public LinkeaMsg buildBusinessImageUpload(String businessImageName, String businessImageData) {
        return new BusinessImageUpload(client, businessImageName, businessImageData);
    }

    public LinkeaMsg buildUserImageUpload(String idImageName, String idImageData) {
        return new UserImageUpload(client, idImageName, idImageData);
    }

    public LinkeaMsg buildImageUploads(String accountImageName, String accoutImageData, String orgImageName, String orgImageData, String taxImageName, String taxImageData) {
        return new ImageUploads(client, accountImageName, accoutImageData, orgImageName, orgImageData, taxImageName, taxImageData);
    }

    public LinkeaMsg buildCreateOrderMsg(String amount, String biz_code, String toCardNo, String phone, String summary){
        return new CreateOrderMsg(client, amount, biz_code, toCardNo, phone, summary);
    }

    public LinkeaMsg buildCreateOrderMsg2(String amount, String biz_code, String summary, String order_detail_json ){
        return new CreateOrderMsg2(client, amount, biz_code, summary, order_detail_json);
    }

    public LinkeaMsg buildPayOrderMsg(String pay_channel, String trade_no,
                                      String ksn, String cardNo, String encPin, String track, String icdata, String cardSeqNo, String encTrack2, String encTrack3, String encTrackKey,
                                      String encMacKey,String encPinKey) {
        return new PayOrderMsg(client,  pay_channel,  trade_no,
                ksn,  cardNo,  encPin,  track,  icdata,  cardSeqNo,  encTrack2,  encTrack3,  encTrackKey,encMacKey,encPinKey);
    }


    public LinkeaMsg buildAirTicketQuery(String arrival, String departure, String departure_date) {
        return new AirTicketQuery(client, arrival, departure, departure_date);
    }


    public LinkeaMsg buildTrainTicketQuery(String arrival, String departure, String departure_date) {
        return new TrainTicketQuery(client, arrival, departure, departure_date);
    }

    public LinkeaMsg buildPhoneInfoGet(String phone) {
        return new PhoneInfoGet(client, phone);
    }

    public LinkeaMsg buildPhoneChargeOrderCreate(String amount, String mobile_area, String operator, String phone) {
        return new PhoneChargeOrderCreate(client, amount, mobile_area, operator, phone);
    }


    public LinkeaMsg buildBalanceInfoGet(String ksn, String cardNo, String encPin, String cardSeqNo,String encTrack2, String encTrack3,
                                          String encWorkingKey,String icdata) {
        return new BalanceInfoGet(client, ksn, cardNo, encPin,cardSeqNo, encTrack2, encTrack3, encWorkingKey, icdata);
    }

    public LinkeaMsg buildMemberBind(String memberNo) {
        return new MemberBindMsg(client,memberNo);
    }

    public LinkeaMsg buildPermissionMsg() {
        return new PermissionMsg(client);
    }

    public LinkeaMsg buildAdvMsg() {
        return new AdvMsg(client);
    }

    public LinkeaMsg buildPaySignMsg(String tradeno,String sign) {
        return new PaySignMsg(client,tradeno,sign);
    }


    public LinkeaMsg buildGetProductList() {
        return new ProductListMsg(client);
    }

    public void setAccessToken(String accessToken) {
        access_token = accessToken;
    }

    public void setTerm_id(String term_id) {
        this.term_id = term_id;
    }

   public String getTerm_id()
   {
        return this.term_id;
   }
    public void setTerm_mac(String term_mac) {
        this.term_mac = term_mac;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(String longitude){ this.longitude = longitude; }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }


    class SignInMsg extends LinkeaMsg {
        static final String method = "linkea.terminal.pos.signin";

        public SignInMsg(AsyncHttpClient client) {
            super(client);
            params.put("access_token", access_token);
            params.put("method", method);

            params.put("term_id", term_id);

        }
    }

    class LoginMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.user.oauth.login";

        public LoginMsg(AsyncHttpClient client, String userName, String password) {
            super(client);
            params.put("access_token", access_token);
            params.put("method", method);

            params.put("member_id", userName);
            params.put("password", password);
            params.put("longitude", longitude);
            params.put("latitude",latitude);

            params.put("term_id",term_id);
            params.put("term_mac",term_mac);
        }
    }

    class RegisterMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.member.register";

        public RegisterMsg(AsyncHttpClient client,  String phone,String password, String userName, String shop_adr,String shop_name,String shop_type,String term_ip,String cardId) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);


            params.put("appkey", "100050");
            params.put("login_psw", password);
            params.put("member_no", phone);
            params.put("member_name", userName);
            params.put("people_id", cardId);
            params.put("shop_addr", shop_adr);
            params.put("shop_name", shop_name);
            params.put("shop_type", shop_type);

            params.put("term_id", term_id);
            params.put("term_ip", term_ip);
            params.put("term_mac", term_mac);
        }
    }

    class UserActivateMsg extends LinkeaMsg {
        static final String method = "linkea.user.activation";

        public UserActivateMsg(AsyncHttpClient client, String memberID, String code) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);


            params.put("code", code);
            params.put("member_id", memberID);

        }
    }

    class TerminalActivation extends LinkeaMsg {
        static final String method = "linkea.terminal.largeTermMposActivate";

        public TerminalActivation(AsyncHttpClient client, String device_id, String pos_id) {
            super(client);
            params.put("access_token", access_token);
            params.put("method", method);

            //params.put("device_id", device_id);
            params.put("pos_id", pos_id);

        }
    }

    class LogoutMsg extends LinkeaMsg {
        static final String method = "linkea.user.oauth.logout";

        public LogoutMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);
        }
    }

    class UserInfoMsg extends LinkeaMsg {
        static final String method = "linkea.user.oauth.get";

        public UserInfoMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("term_id", term_id);
            params.put("term_mac", term_mac);

        }
    }

    class ProductListMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.query.baseList";

        public ProductListMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);
            //params.put("p_type", "1");

            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
        }
    }

    public LinkeaMsg buildProductCategoryMsg(){
        return new ProductCategoryMsg(client);
    }
    class ProductCategoryMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.query.prodList";
        public ProductCategoryMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);
            //params.put("p_type", "1");

            params.put("term_id", term_id);
            params.put("term_mac", term_mac);

        }
    }

    class BindBankcardMsg extends LinkeaMsg {
        static final String method = "linkea.user.bankbind.update";

        public BindBankcardMsg(AsyncHttpClient client, String authStatus, String bankStatus, String bankImagData, String bankImgName, String bankName, String cardHolder, String cardNo) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("auth_status", authStatus);
            params.put("bankImg", bankImagData);
            params.put("bankImgName", bankImgName);
            params.put("bank_name", bankName);
            params.put("bank_status", bankStatus);
            params.put("card_holder", cardHolder);
            params.put("card_no", cardNo);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
        }
    }

    class GetBankBranch extends LinkeaMsg {
        static final String method = "linkea.util.bank.branch.get";

        public GetBankBranch(AsyncHttpClient client, String bankCode, String keyWord, String page, String pageSize) {
            super(client);

            params.put("method", method);
            params.put("access_token", access_token);

            params.put("bank_code", bankCode);
            params.put("keyword", keyWord);
            params.put("page", page);
            params.put("page_size", pageSize);
        }
    }

    class UpdatePasswordMsg extends LinkeaMsg {
        static final String method = "linkea.user.loginpassword.update";

        public UpdatePasswordMsg(AsyncHttpClient client, String oldPassword, String newPassword) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("login_id", member_id);
            params.put("new_login_pwd", newPassword);
            params.put("old_login_pwd", oldPassword);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);

        }
    }

    class GetSmsCodeMsg extends LinkeaMsg {
        static final String method = "linkea.user.password.forgot.smscode.get";

        public GetSmsCodeMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("member_id", member_id);
        }
    }

    class ResetPasswordMsg extends LinkeaMsg {
        static final String method = "linkea.user.password.forgot.reset";

        public ResetPasswordMsg(AsyncHttpClient client, String code, String password) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("code", code);
            params.put("member_id", member_id);
            params.put("password", password);
        }
    }


    class CreateOrderMsg2 extends LinkeaMsg {
        static final String method = "linkea.trade.order.create";

        public CreateOrderMsg2(AsyncHttpClient client, String amount, String biz_code, String summary, String order_detail_json) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("amount", amount);
            params.put("login_user_id", member_id);

            params.put("biz_code", biz_code);
            params.put("order_detail_json", order_detail_json);
            params.put("summary", summary);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
        }
    }

    class CancelOrderMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.order.cancel";

        public CancelOrderMsg(AsyncHttpClient client, String orderNo, String order_detail) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);

            params.put("orderNo", orderNo);
            params.put("order_detail", order_detail);
        }
    }

    public  LinkeaMsg buildCancelOrderMsg(String orderNo, String order_detail){
        return new CancelOrderMsg(client,orderNo,order_detail);
    }

    class CreateOrderMsg extends LinkeaMsg {
        static final String method = "linkea.trade.order.create";

        public CreateOrderMsg(AsyncHttpClient client, String amount, String biz_code, String toCardNo, String phone, String summary) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("amount", amount);
            params.put("biz_code", biz_code);
            params.put("order_detail_json", "{"
                    + "\"toCardNo\":\"" + toCardNo + "\","
                    + "\"mobileNumber\":\"" + phone + "\""
                    + "}");
            params.put("summary", summary);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
        }
    }

    class PayOrderMsg extends LinkeaMsg {
        static final String method = "linkea.trade.order.pay";

        public class CardInfo{
            public String ksn;
            public String cardNo;
            public String encPin;
            public String type;
            public String track;
            public String icdata;
            public String cardSeqNo;
            public String encTrack2;
            public String encTrack3;
            public String track2Length;
            public String track3Length;
            public String encWorkingKey;

            public CardInfo(String ksn, String cardNo,String encPin,String track,String icdata,String cardSeqNo,
                            String encTrack2, String encTrack3,String encWorkingKey){
                this.ksn = ksn;
                this.cardNo = cardNo;
                this.encPin = encPin;
                this.type = "1";
                this.track = track;
                this.icdata = icdata;
                this.cardSeqNo = cardSeqNo;
                this.encTrack2 = encTrack2;
                this.encTrack3 = encTrack3;
                this.track2Length  =String.valueOf(encTrack2.length());
                this.track3Length = String.valueOf(encTrack3.length());
                this.encWorkingKey = encWorkingKey;
            }
        }

        public PayOrderMsg(AsyncHttpClient client, String pay_channel, String trade_no,
                           String ksn, String cardNo, String encPin, String track, String icdata, String cardSeqNo, String encTrack2, String encTrack3, String encTrackKey,String encMacKey,String encPinKey) {
            super(client);

            CardInfo cardInfo = new CardInfo( ksn,  cardNo, encPin, track, icdata, cardSeqNo, encTrack2,  encTrack3, encMacKey);

            params.put("method", method);
            params.put("access_token", access_token);

            params.put("pay_channel", pay_channel);
            params.put("pay_detail_json", new Gson().toJson(cardInfo));

            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
            params.put("trade_no", trade_no);
            params.put("coordinates", longitude + "," + latitude );
        }
    }
    public LinkeaMsg buildWinxinPayOrder(String trade_no, String auth_code,  String totalfee, String ip) {
        return new WechatPayOrderMsg(client, trade_no, auth_code, totalfee,ip);
    }
    class WechatPayOrderMsg extends LinkeaMsg {
        static final String method = "linkea.trade.order.fastpay";

        public class PayDetail{
            public String body;
            public String total_fee;
            public String term_ip;
            public String auth_code;
            public PayDetail(String body,String totalfee, String term_ip,String auth_code){
                this.body = body;
                this.total_fee = totalfee;
                this.term_ip = term_ip;
                this.auth_code = auth_code;
            }
        }
        public WechatPayOrderMsg(AsyncHttpClient client,  String trade_no,String auth_code,String totalfee, String ip ) {
            super(client);
            String  pay_channel = "PAY_WECHAT";
            String body = "联亿家-" + trade_no;
            PayDetail detail = new PayDetail( body, totalfee,  ip, auth_code);
            params.put("method", method);
            params.put("access_token", access_token);
            params.put("pay_channel", pay_channel);
            params.put("pay_detail_json", new Gson().toJson(detail) );
//            params.put("pay_detail_json","{\"body\":\""+body+"\"," +   //商品测试商品测试商品测试
//                    "\"total_fee\":\""+totalfee+"\"," +
//                    "\"term_ip\":\""+ip+"\"," +
//                    "\"auth_code\":\""+auth_code+"\"}"  //130265149358154950
//            );
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
            params.put("trade_no", trade_no);

        }
    }

    class ClientUpdateCheck extends LinkeaMsg {
        static final String method = "linkea.terminal.client.update.check";

        public ClientUpdateCheck(AsyncHttpClient client, String version) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("version", version);
        }
    }

//    class ClerkAdd extends LinkeaMsg {
//        static final String method = "linkea.mpos.clerk.add";
//
//        public ClerkAdd(AsyncHttpClient client, String clerk_name, String login_pwd, String member_no, String phone, String status, String type) {
//            super(client);
//            params.put("method", method);
//            params.put("access_token", access_token);
//
//            params.put("clerk_name", clerk_name);
//            params.put("login_pwd", login_pwd);
//            params.put("member_no", member_no);
//            params.put("phone", phone);
//            params.put("status", status);
//            params.put("term_id", term_id);
//            params.put("type", type);
//        }
//    }

    class MPosImagesUpload extends LinkeaMsg {
        static final String method = "linkea.mpos.images.upload";

        public MPosImagesUpload(AsyncHttpClient client, String accountImgName, String accountImgData,String idImgName, String idImgData,
                                String businessImgName, String businessImgData, String taxImgName, String taxImgData,
                                String orgImgName, String orgImgData, String headImgName, String headImgData,String certNum){
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("memberNo",  member_id);
            params.put("accountImgName", accountImgName);
            params.put("accountImg", accountImgData);
            params.put("idImgName", idImgName);
            params.put("idImg", idImgData);
            params.put("businessImgName", businessImgName);
            params.put("businessImg", businessImgData);
            params.put("certNum",certNum);
            params.put("orgImgName", orgImgName);
            params.put("orgImg", orgImgData);
            params.put("taxImgName", taxImgName);
            params.put("taxImg", taxImgData);
            params.put("headImgName", headImgName);
            params.put("headImg", headImgData);
        }
    }

    class UserImageUpload extends LinkeaMsg {
        static final String method = "linkea.user.auth.image.upload";

        public UserImageUpload(AsyncHttpClient client, String idImageName, String idImageData) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("head_image_name", idImageName);
            params.put("head_img", idImageData);
            params.put("id_img_name", idImageName);
            params.put("id_img", idImageData);
        }
    }

    class BusinessImageUpload extends LinkeaMsg {
        static final String method = "linkea.user.auth.image.uploadBus";

        public BusinessImageUpload(AsyncHttpClient client, String businessImageName, String businessImageData) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("business_img_name", businessImageName);
            params.put("business_img", businessImageData);
        }
    }

    class ImageUploads extends LinkeaMsg {
        static final String method = "linkea.user.auth.image.uploads";

        public ImageUploads(AsyncHttpClient client, String accountImageName, String accoutImageData, String orgImageName, String orgImageData, String taxImageName, String taxImageData) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("account_img", accoutImageData);
            params.put("account_img_name", accountImageName);
            params.put("org_img", orgImageData);
            params.put("org_img_name", orgImageName);
            params.put("tax_img", taxImageData);
            params.put("tax_img_name", taxImageName);
        }
    }

    class AirTicketQuery extends LinkeaMsg {
        static final String method = "linkea.jipiao.get";

        public AirTicketQuery(AsyncHttpClient client, String arrival, String departure, String departure_date) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            //params.put("airline",  airline);
            params.put("arrival", arrival);
            params.put("departure", departure);
            params.put("departure_date", departure_date);
        }
    }

    class AirTicketCreate extends LinkeaMsg {
        static final String method = "linkea.jipiao.order.create";

        public AirTicketCreate(AsyncHttpClient client, String arrival, String departure, String departure_date) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

//            params.put("fares",  fares);
//            params.put("link_name",  link_name);
//            params.put("link_tel",  link_tel);
//            //params.put("passengers",  passengers);
//            params.put("price",  price);
//            params.put("link_name",  link_name);
//            params.put("link_tel",  link_tel);
        }
    }

    class TrainTicketQuery extends LinkeaMsg {
        static final String method = "linkea.train.schedule.get";

        public TrainTicketQuery(AsyncHttpClient client, String arrival, String departure, String departure_date) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("arrival", arrival);
            params.put("departure", departure);
            params.put("departure_date", departure_date);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);

        }
    }

    class TrainTicketCreate extends LinkeaMsg {
        static final String method = "linkea.train.order.create";

        public TrainTicketCreate(AsyncHttpClient client, String arrival, String departure, String departure_date) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

//            params.put("fares",  fares);
//            params.put("link_name",  link_name);
//            params.put("link_tel",  link_tel);
//            //params.put("passengers",  passengers);
//            params.put("price",  price);
//            params.put("link_name",  link_name);
//            params.put("link_tel",  link_tel);
        }
    }

    class PhoneInfoGet extends LinkeaMsg {
        static final String method = "linkea.recharge.phone.home.get";

        public PhoneInfoGet(AsyncHttpClient client, String phone) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("phone", phone);
        }
    }

    class PhoneChargeOrderCreate extends LinkeaMsg {
        static final String method = "linkea.recharge.phone.order.create";

        public PhoneChargeOrderCreate(AsyncHttpClient client, String amount, String mobile_area, String operator, String phone) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("amount", amount);
            params.put("phone", phone);
            params.put("mobile_area", mobile_area);
            params.put("operator", operator);
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
        }
    }

    class BalanceInfoGet extends LinkeaMsg {
        static final String method ="linkea.trade.all.getbalance";// "linkea.trade.ipos.getbalance";

        public BalanceInfoGet(AsyncHttpClient client, String ksn, String cardNo, String encPin,String cardSeqNo,String encTrack2, String encTrack3,
                              String encWorkingKey,String icData) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);


            params.put("ksn",ksn);
            params.put("acctNo",cardNo);
            params.put("cardSeqNo",cardSeqNo);
            params.put("icData",icData);
            params.put("pinValue",encPin);

            params.put("track2",encTrack2);
            params.put("track3",encTrack3);
            params.put("track2Length",String.valueOf(encTrack2.length()));
            params.put("track3Length",String.valueOf(encTrack3.length()));
            params.put("workKey",encWorkingKey);
            params.put("type","2");
            params.put("pays_enum","PAY_MPOS");


            params.put("terminalId", term_id);
            params.put("terminalMac", term_mac);
        }
    }
    /**
     * add upload sign image
    */
    class PaySignMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.get.paySgin";

        public PaySignMsg(AsyncHttpClient client,String tradeNo ,String image) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
            params.put("tradeNo", tradeNo);
            params.put("paySignImg", image);
        }
    }

    /**
     * add adv  msg
     */
    class AdvMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.adv.download";

        public AdvMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("term_id", term_id);
            params.put("term_type", "4");
        }
    }

    class PermissionMsg extends LinkeaMsg {
        static final String method = "linkea.terminal.query.permissions";

        public PermissionMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("terminal_id", term_id);
        }
    }

    class MemberBindMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.add.termId2Shop";

        public MemberBindMsg(AsyncHttpClient client,String member_no) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("memberNo", member_no);
            params.put("termId", term_id);
        }
    }

    public  ClerkQueryMsg buildClerkQueryMsg(){
        return new ClerkQueryMsg(client);
    }

    class ClerkQueryMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.clerk.query.termId";

        public ClerkQueryMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);
            params.put("termId", term_id);
        }
    }

    public ClerkAddMsg buildClerkAddMsg(User user,String member_no){
        return new ClerkAddMsg(client,user,member_no);
    }
    class ClerkAddMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.clerk.add";
        public ClerkAddMsg(AsyncHttpClient client,User user,String member_no) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("clerk_name", user.getClerkName());
            params.put("login_pwd", user.getLoginPwd());
            params.put("member_no", member_no);
            params.put("phone", user.getPhone());
            params.put("status", "1");
            params.put("type", String.valueOf(user.getType()));
            params.put("term_id", term_id);
        }
    }

    public ClerkModMsg buildClerkModMsg(User user,String member_no){
        return new ClerkModMsg(client,user,member_no);
    }
    class ClerkModMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.clerk.update";

        public ClerkModMsg(AsyncHttpClient client,User user,String member_no) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("clerk_name", user.getClerkName());
            params.put("clerk_pwd", user.getLoginPwd());
            params.put("clerk_no", String.valueOf(user.getClerkNo()));
            params.put("member_no", member_no);
            params.put("phone", user.getPhone());
            params.put("type", String.valueOf(user.getType()));
            params.put("term_id", term_id);
        }
    }

    public ClerkDelMsg buildClerkDelMsg(User user){
        return new ClerkDelMsg(client,user);
    }
    class ClerkDelMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.clerk.delete";

        public ClerkDelMsg(AsyncHttpClient client,User user) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("clerk_no", String.valueOf(user.getClerkNo()));
        }
    }

    public GpsMsg buildGpsMsg(String latitude,String longitude){
        return new GpsMsg(client,latitude,longitude );
    }
    class GpsMsg extends LinkeaMsg {
        static final String method = "linkea.location.info.do";

        public GpsMsg(AsyncHttpClient client,String latitude,String longitude) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);

            params.put("latitude", latitude);
            params.put("longitude",longitude );
            params.put("termId", term_id);
        }
    }

    /**
     *  add class for shop order and checkout order by jz
     */
    public static class CheckoutOrderDetail{
        public CheckoutOrderDetail(){}
        public List<Good> goods;
        public static class Good {
            public long goodsId;
            public String goodsName;
            public String goodsPrice;
            public float goodsItem;
            public String barCode;
            public String memberNo;
        }
        @Override
        public String toString(){
            return new Gson().toJson(goods);
        }
    }

    public static class ShopOrderDetail{
        public List<Supplier> supplier;
        public String amount; //total price for all suppliers;
        public static class Supplier {
            public String supplierId;
            public String orderDate;
            public String consigneeName;
            public String consigneeMobile;
            public String consigneePhone;
            public String consigneeAddress;

            public String paymentName;
            public String transferName;
            public String totalPrice;
            public String remark;
            public List<Item> items;
        }

        public static class Item{
            public long productID;
            public String productName;
            public String price;
            public long quantity;
            public String totalPrice;
        }

        @Override
        public String toString(){
            return new Gson().toJson(this);
        }
    }

    public  LinkeaMsg buildMemberPayOrderMsg( String trade_no, String memberNo, String paymentPwd){
        return new MemberPayOrderMsg(client,trade_no,memberNo,paymentPwd);
    }
    public   class  MemberPayOrderMsg extends LinkeaMsg {
        static final String method = "linkea.trade.order.pay";

        public class MemberInfo{
            public String memberNo;
            public String paymentPwd;
            public MemberInfo(String memberNo, String paymentPwd){
                this.memberNo = memberNo;
                this.paymentPwd = paymentPwd;
            }
        }

        public MemberPayOrderMsg(AsyncHttpClient client,  String trade_no,String memberNo, String paymentPwd ) {
            super(client);

            String  pay_channel = "PAY_MEMBER";
            MemberInfo info  = new MemberInfo(memberNo,paymentPwd);

            params.put("method", method);
            params.put("access_token", access_token);

            params.put("pay_channel", pay_channel);
            params.put("pay_detail_json", new Gson().toJson(info) );
            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
            params.put("trade_no", trade_no);
        }
    }

    public ShopOrderStatusMsg buildShopOrderStatusMsg(){
        return new ShopOrderStatusMsg(client);
    }
    public class ShopOrderStatusMsg extends LinkeaMsg {
        static final String method = "linkea.mpos.query.order.status.memberId";
        String member_no = String.valueOf(Utils.getMemberId());

        public ShopOrderStatusMsg(AsyncHttpClient client) {
            super(client);
            params.put("method", method);
            params.put("access_token", access_token);
            params.put("member_no", member_no);

            params.put("term_id", term_id);
            params.put("term_mac", term_mac);
        }
    }
}
