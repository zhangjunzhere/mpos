package com.itertk.app.mpos.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.PictureDialog;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.config.DeviceInfo;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;

import java.io.ByteArrayOutputStream;

/**
*商户认证
* */

public class CertificateActivity extends Activity implements View.OnClickListener {
    static final String TAG = "CertificateActivity";
    MPosApplication mPosApplication;
    String status;

    RelativeLayout btnYYZZ = null;
    RelativeLayout btnSFZ = null;
    RelativeLayout btnKHXKZ = null;
    RelativeLayout btnSWDJZ = null;
    RelativeLayout btnZZDMZ = null;
    RelativeLayout btnHEAD=null;
    Button btnSubmit = null;

    TextView textFullname = null;
    TextView textUserName = null;

    static final String certificateConfig = "certificateconfig";
    static final String keyYYZZ = "yyzz";
    static final String keySFZ = "sfz";
    static final String keyKHXKZ = "khxkz";
    static final String keySWDJZ = "swdjz";
    static final String keyZZDMZ = "zzdmz";
    static final String keyHEAD = "head";
    String certNum="2";

    private void loadCertificateConfig() {
        Log.d(TAG, "loadCertificateConfig ");
        SharedPreferences sharedPre = getSharedPreferences(certificateConfig, MODE_PRIVATE);

        if (sharedPre.getString(keyYYZZ, "").isEmpty())
            unChooseYYZZ();
        else
            chooseYYZZ(Uri.parse(sharedPre.getString(keyYYZZ, "")));

        if (sharedPre.getString(keySFZ, "").isEmpty())
            unChooseSFZ();
        else
            chooseSFZ(Uri.parse(sharedPre.getString(keySFZ, "")));

        if (sharedPre.getString(keyKHXKZ, "").isEmpty())
            unChooseKHXKZ();
        else
            chooseKHXKZ(Uri.parse(sharedPre.getString(keyKHXKZ, "")));

        if (sharedPre.getString(keySWDJZ, "").isEmpty())
            unChooseSWDJZ();
        else
            chooseSWDJZ(Uri.parse(sharedPre.getString(keySWDJZ, "")));

        if (sharedPre.getString(keyZZDMZ, "").isEmpty())
            unChooseZZDMZ();
        else
            chooseZZDMZ(Uri.parse(sharedPre.getString(keyZZDMZ, "")));
        if (sharedPre.getString(keyHEAD, "").isEmpty())
            unChooseHEAD();
        else
            chooseHEAD(Uri.parse(sharedPre.getString(keyHEAD, "")));
    }

    private void saveCertificateConfig(String key, String value) {
        Log.d(TAG, "saveCertificateConfig " + key + ":" + value);
        SharedPreferences sharedPre = getSharedPreferences(certificateConfig, MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor = sharedPre.edit();
        //设置参数
        editor.putString(key, value);

        //提交
        editor.commit();
    }

    private void clearCertificateConfig() {
        unChooseYYZZ();
        unChooseSFZ();
        unChooseKHXKZ();
        unChooseSWDJZ();
        unChooseZZDMZ();
        unChooseHEAD();
    }

    private void choosePic(int requestCode) {
/*        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image*//*");
        //intent.putExtra("crop", "true");
        //intent.putExtra("aspectX", 1);
        //intent.putExtra("aspectY", 1);
        //intent.putExtra("outputX", 80);
        //intent.putExtra("outputY", 80);
        //intent.putExtra("return-data", true);


        startActivityForResult(intent, requestCode);*/

        if(DeviceInfo.checkCameraHardware(this)) {
            PictureDialog pictureDialog = new PictureDialog(this, R.style.MyDialog, null, requestCode);
            pictureDialog.show();
        }else{
            Utils.launchCamera(this,requestCode,false);
        }
    }

    private void chooseYYZZ(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed1);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage1);
        imageView.setImageURI(imgUri);

        saveCertificateConfig(keyYYZZ, imgUri.toString());
    }

    private void unChooseYYZZ() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed1);
        textView.setText("未上传");
        textView.setTextColor(getResources().getColor(R.color.solid_red));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage1);
        imageView.setImageResource(R.drawable.item_image);

        saveCertificateConfig(keyYYZZ, "");
    }

    private void chooseSFZ(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed2);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage2);
        imageView.setImageURI(imgUri);

        saveCertificateConfig(keySFZ, imgUri.toString());
    }

    private void unChooseSFZ() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed2);
        textView.setText("未上传");
        textView.setTextColor(getResources().getColor(R.color.solid_red));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage2);
        imageView.setImageResource(R.drawable.item_image);

        saveCertificateConfig(keySFZ, "");
    }

    private void chooseKHXKZ(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed3);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage3);
        imageView.setImageURI(imgUri);

        saveCertificateConfig(keyKHXKZ, imgUri.toString());
    }

    private void unChooseKHXKZ() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed3);
        textView.setText("可选择");


        ImageView imageView = (ImageView) findViewById(R.id.ItemImage3);
        imageView.setImageResource(R.drawable.item_image);

        saveCertificateConfig(keyKHXKZ, "");
    }

    private void chooseSWDJZ(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed4);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage4);
        imageView.setImageURI(imgUri);

        saveCertificateConfig(keySWDJZ, imgUri.toString());
    }

    private void unChooseSWDJZ() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed4);
        textView.setText("可选择");

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage4);
        imageView.setImageResource(R.drawable.item_image);

        saveCertificateConfig(keySWDJZ, "");
    }

    private void chooseZZDMZ(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed5);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage5);
        imageView.setImageURI(imgUri);

        saveCertificateConfig(keyZZDMZ, imgUri.toString());
    }

    private void unChooseZZDMZ() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed5);
        textView.setText("可选择");

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage5);
        imageView.setImageResource(R.drawable.item_image);

        saveCertificateConfig(keyZZDMZ, "");
    }

    private void chooseHEAD(Uri imgUri) {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed6);
        textView.setText("已上传");
        textView.setTextColor(getResources().getColor(R.color.clear_shop));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage6);
        imageView.setImageURI(imgUri);

        saveCertificateConfig(keyHEAD, imgUri.toString());
    }

    private void unChooseHEAD() {
        TextView textView = (TextView) findViewById(R.id.ItemTextNeed6);
        textView.setText("未上传");
        textView.setTextColor(getResources().getColor(R.color.solid_red));

        ImageView imageView = (ImageView) findViewById(R.id.ItemImage6);
        imageView.setImageResource(R.drawable.item_image);

        saveCertificateConfig(keyHEAD, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=Activity.RESULT_OK){
            return;
        }
        Uri uri= Utils.getDataUri(data, requestCode);
        if (uri == null) {
            Log.e(TAG, "onActivityResult intent is null");

            switch (requestCode) {
                case 1:
                    unChooseYYZZ();
                    break;
                case 2:
                    unChooseSFZ();
                    break;
                case 3:
                    unChooseKHXKZ();
                    break;
                case 4:
                    unChooseSWDJZ();
                    break;
                case 5:
                    unChooseZZDMZ();
                    break;
                case 6:
                    unChooseHEAD();
                    break;
            }


            return;
        }

        //Log.d(TAG, " " + requestCode + " " + resultCode + data.toString());

        switch (requestCode) {
            case 1:
                chooseYYZZ(uri);
                break;
            case 2:
                chooseSFZ(uri);
                break;
            case 3:
                chooseKHXKZ(uri);
                break;
            case 4:
                chooseSWDJZ(uri);
                break;
            case 5:
                chooseZZDMZ(uri);
                break;
            case 6:
                chooseHEAD(uri);
                break;
        }
    }

    private void onbtnYYZZ() {
        choosePic(1);
    }

    private void onbtnSFZ() {
        choosePic(2);
    }

    private void onbtnKHXKZ() {
        choosePic(3);
    }

    private void onbtnSWDJZ() {
        choosePic(4);
    }

    private void onbtnZZDMZ() {
        choosePic(5);
    }

    private void onbtnHEAD() {
        choosePic(6);
    }

    public String bitmapToBase64(String uriPath) {
        String string = null;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uriPath));
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            int qulity=80;
            if(!certNum.equals("2")){
                qulity=60;
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, qulity, bStream);
            byte[] bytes = bStream.toByteArray();
            string = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return string;
    }

    private  void  ButtonSwitch(float alpha,boolean enable){
        btnHEAD.setAlpha(alpha);
        btnHEAD.setEnabled(enable);
        btnZZDMZ.setAlpha(alpha);
        btnZZDMZ.setEnabled(enable);
        btnSWDJZ.setAlpha(alpha);
        btnSWDJZ.setEnabled(enable);
        btnKHXKZ.setAlpha(alpha);
        btnKHXKZ.setEnabled(enable);
        btnSFZ.setAlpha(alpha);
        btnSFZ.setEnabled(enable);
        btnYYZZ.setAlpha(alpha);
        btnYYZZ.setEnabled(enable);
    }

    private void submitImages() {
        Log.d(TAG, "submitImages ");
        SharedPreferences sharedPre = getSharedPreferences(certificateConfig, MODE_PRIVATE);

        String yyzzPath = sharedPre.getString(keyYYZZ, "");
        if (yyzzPath.isEmpty()) {
            Toast.makeText(getApplicationContext(), "营业执照不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String sfzPath = sharedPre.getString(keySFZ, "");
        if (sfzPath.isEmpty()) {
            Toast.makeText(getApplicationContext(), "身份证不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String headPath = sharedPre.getString(keyHEAD, "");
        if (headPath.isEmpty()) {
            Toast.makeText(getApplicationContext(), "用户头像不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        String kfxkzPath = sharedPre.getString(keyKHXKZ, "");
        String swdjzPath = sharedPre.getString(keySWDJZ, "");
        String zzdmzPath = sharedPre.getString(keyZZDMZ, "");

        certNum="2";
        if(!"".equals(kfxkzPath)||!"".equals(swdjzPath)||!"".equals(zzdmzPath)){
            certNum="5";
        }

        String businessImageName = "ib.jpg";//Uri.parse(yyzzPath).getLastPathSegment();
        String businessImageData = bitmapToBase64(yyzzPath);

        String idImageName = "id.jpg";//Uri.parse(sfzPath).getLastPathSegment();
        String idImageData = bitmapToBase64(sfzPath);

        String headImageName="head.jpg";
        String headImageData = bitmapToBase64(headPath);


        String kfxkzImageName="";
        String swdjzImageName="";
        String zzdmzImageName="";
        String kfxkzImageData="";
        String swdjzImageData="";
        String zzdmzImageData="";

        if(!"".equals(kfxkzPath)){
            kfxkzImageName="kfxkz.jpg";
            kfxkzImageData = bitmapToBase64(kfxkzPath);
        }
        if(!"".equals(swdjzPath)){
            swdjzImageName="swdjz.jpg";
            swdjzImageData = bitmapToBase64(swdjzPath);
        }
        if(!"".equals(zzdmzPath)){
            zzdmzImageName="kfxkz.jpg";
            zzdmzImageData = bitmapToBase64(zzdmzPath);
        }
        final LoadingDialog loadingDialog = new LoadingDialog(this, R.style.MyDialog);
        loadingDialog.show();

        ((MPosApplication) getApplication()).getMsgBuilder().buildMPosImagesUpload(kfxkzImageName, kfxkzImageData, idImageName, idImageData,
                businessImageName, businessImageData, swdjzImageName, swdjzImageData, zzdmzImageName, zzdmzImageData, headImageName, headImageData,certNum).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, throwable.toString());
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d(TAG, responseString);
                loadingDialog.dismiss();

                try {
                    LinkeaResponseMsg.ResponseMsg userImageUploadResponseMsg =
                            LinkeaResponseMsgGenerator.generateFiveImagesResponseMsg(responseString);

                    if (userImageUploadResponseMsg.success) {
                        btnSubmit.setText("资料审核中，请耐心等待");
                        btnSubmit.setBackgroundResource(R.drawable.btn_green);
                        btnSubmit.setOnClickListener(null);
                        ButtonSwitch(0.5f,false);
                    } else {
                       String erroInfo = userImageUploadResponseMsg.toString();
                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, erroInfo);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
               }

           }

        });


//        ((MPosApplication) getApplication()).getMsgBuilder().buildUserImageUpload(idImageName, idImageData).send(new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getApplicationContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
//                Log.d(TAG, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                Log.d(TAG, responseString);
//
//                try {
//                    LinkeaResponseMsg.UserImageUploadResponseMsg userImageUploadResponseMsg =
//                            LinkeaResponseMsg.generateUserImageUploadResponseMsg(responseString);
//
//                    if (userImageUploadResponseMsg.isSuccess()) {
//
//                    } else {
//                        String erroInfo = userImageUploadResponseMsg.toString();
//                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, erroInfo);
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, e.toString());
//                }
//
//            }
//        });


//        String businessImageName = Uri.parse(yyzzPath).getLastPathSegment();
//        String businessImageData = bitmapToBase64(yyzzPath);
//
//        ((MPosApplication) getApplication()).getMsgBuilder().buildBusinessImageUpload(businessImageName, businessImageData).send(new TextHttpResponseHandler() {
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
//                Log.e(TAG, throwable.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                try {
//                    LinkeaResponseMsg.BusinessImageUploadResponseMsg businessImageUploadResponseMsg =
//                            LinkeaResponseMsg.generateBusinessImageUploadResponseMsg(responseString);
//                    if (businessImageUploadResponseMsg.isSuccess()) {
//
//                    } else {
//                        String erroInfo = businessImageUploadResponseMsg.toString();
//                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, erroInfo);
//                    }
//                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, e.toString());
//                }
//            }
//        });


//
//        String khxkz = sharedPre.getString(keyKHXKZ, "");
//        String swdjz = sharedPre.getString(keySWDJZ, "");
//        String zzdmz = sharedPre.getString(keyZZDMZ, "");
//
//        if(khxkz.isEmpty() && swdjz.isEmpty() && zzdmz.isEmpty()) return;
//
//        String accountImageName = null;
//        String accoutImageData = null;
//        String orgImageName = null;
//        String orgImageData = null;
//        String taxImageName = null;
//        String taxImageData = null;
//
//        ((MPosApplication) getApplication()).getMsgBuilder().buildImageUploads(accountImageName, accoutImageData, orgImageName, orgImageData, taxImageName, taxImageData).send(new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//            }
//        });

    }

    String tempStaus;
    private void onbtnSubmit() {
        //status = "4";
        if(mPosApplication.getMember()==null) {
            Toast.makeText(this,"您为离线用户，不能提交审核",Toast.LENGTH_SHORT).show();
            return;
        }
        checkCertificateStatus(false);

        if (status.equals("1")) {//未审核
            submitImages();
        } else if (status.equals("2")) {//审核中

        } else if (status.equals("3")) {//审核失败
            if("1".equals(tempStaus)){
                submitImages();
                return;
            }
            clearCertificateConfig();
            btnSubmit.setText("提交审核");
            btnSubmit.setBackgroundResource(R.drawable.my_blue_selector);
            tempStaus="1";
        } else if (status.equals("4")) {//审核通过
            Intent it = new Intent(CertificateActivity.this, BindBankCardActivity.class);
            startActivity(it);
        } else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYYZZ:
                onbtnYYZZ();
                break;
            case R.id.btnSFZ:
                onbtnSFZ();
                break;
            case R.id.btnKHXKZ:
                onbtnKHXKZ();
                break;
            case R.id.btnSWDJZ:
                onbtnSWDJZ();
                break;
            case R.id.btnZZDMZ:
                onbtnZZDMZ();
                break;
            case R.id.btnHEAD:
                onbtnHEAD();
                break;
            case R.id.btnSubmit:
                onbtnSubmit();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        MyActionbar.setLoginActionBarLayout(this, "商户认证");

        mPosApplication = (MPosApplication) getApplication();

        textFullname = (TextView) findViewById(R.id.textFullname);
        textUserName = (TextView) findViewById(R.id.textUserName);


        btnYYZZ = (RelativeLayout) findViewById(R.id.btnYYZZ);
        btnYYZZ.setOnClickListener(this);

        btnSFZ = (RelativeLayout) findViewById(R.id.btnSFZ);
        btnSFZ.setOnClickListener(this);

        btnKHXKZ = (RelativeLayout) findViewById(R.id.btnKHXKZ);
        btnKHXKZ.setOnClickListener(this);

        btnSWDJZ = (RelativeLayout) findViewById(R.id.btnSWDJZ);
        btnSWDJZ.setOnClickListener(this);

        btnZZDMZ = (RelativeLayout) findViewById(R.id.btnZZDMZ);
        btnZZDMZ.setOnClickListener(this);

        btnHEAD=(RelativeLayout)findViewById(R.id.btnHEAD);
        btnHEAD.setOnClickListener(this);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        if(mPosApplication.getMember()!=null) {
            textFullname.setText(mPosApplication.getMember().member.member_name);
            textUserName.setText(mPosApplication.getMember().member.member_id);
        }

        loadCertificateConfig();

        if(mPosApplication.getMember()!=null)
            checkCertificateStatus(true);

    }

    private void checkCertificateStatus(boolean isShowInfo) {
        status = mPosApplication.getMember().authenticate.status;
        ButtonSwitch(1.0f,true);

        if (status.equals("1")) {

        } else if (status.equals("2")) {
            btnSubmit.setText("资料审核中，请耐心等待");
            btnSubmit.setBackgroundResource(R.drawable.btn_green);
            ButtonSwitch(0.5f, false);
            if(isShowInfo){
                showUploadInfo();
            }
        } else if (status.equals("3")) {
            if("1".equals(tempStaus)){
                return;
            }
            btnSubmit.setText("资料审核失败，请重新上传资料");
            btnSubmit.setBackgroundResource(R.drawable.my_red_selector);
        } else if (status.equals("4")) {
            btnSubmit.setText("审核成功！");
            btnSubmit.setBackgroundResource(R.drawable.my_blue_selector);
        }
    }

    private void showUploadInfo(){
        ImageView imageView1 = (ImageView) findViewById(R.id.ItemImage1);
        ImageLoader.getInstance().displayImage(mPosApplication.getMember().authenticate.buss_image_url, imageView1);
        TextView textView1 = (TextView) findViewById(R.id.ItemTextNeed1);
        textView1.setText("已上传");
        ImageView imageView2 = (ImageView) findViewById(R.id.ItemImage2);
        ImageLoader.getInstance().displayImage(mPosApplication.getMember().authenticate.id_image_url, imageView1);
        TextView textView2 = (TextView) findViewById(R.id.ItemTextNeed2);
        textView2.setText("已上传");
        ImageView imageView3 = (ImageView) findViewById(R.id.ItemImage6);
        ImageLoader.getInstance().displayImage(mPosApplication.getMember().authenticate.head_image_url, imageView1);
        TextView textView3 = (TextView) findViewById(R.id.ItemTextNeed6);
        textView3.setText("已上传");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.certificate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
