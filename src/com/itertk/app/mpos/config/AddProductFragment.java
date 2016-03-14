package com.itertk.app.mpos.config;



import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.PictureDialog;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.TextFocusChangeListener;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductAttribute;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.ProductAttributeDao;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 *添加商品
 */
public class AddProductFragment extends Fragment implements View.OnClickListener{
    ProductListFragment productListFragment;

    Button btnSave = null;
    LinearLayout btnProductPic = null;
    LinearLayout btnProductAttribute = null;
    EditText textProductPrice = null;
    EditText textProductName = null;
    EditText textBarcode;
    String picPath;
    GridView gridAttribute;
    ArrayList<Attribute> selectAttribute;
    GridViewAdapter gridViewAdapter;
    //Catalog catalog;
    ProductAttributeDao productAttributeManager;
    ImageView imageProduct;

    public static final long DEFAULT_CATALOG_ID= 1;


    class GridViewAdapter extends BaseAdapter{
        ArrayList<Attribute> selectAttribute;
        public GridViewAdapter(){
            this.selectAttribute = AddProductFragment.this.selectAttribute;
        }
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return selectAttribute.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;

            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.attribute_grid_item, null);
            }

            final Button btnAttribute = (Button)view.findViewById(R.id.btnAttribute);
            btnAttribute.setText(selectAttribute.get(pos).getName());

            return view;
        }
    }

    public ArrayList<Attribute> getSelectAttribute(){
        return selectAttribute;
    }

    public void addAttribute(){
        gridViewAdapter.notifyDataSetChanged();
    }

    private void onBtnSave(){
        if(textBarcode.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "商品条码不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(textBarcode.getText().toString().length()>60)
        {
            Toast.makeText(getActivity().getApplicationContext(), "商品条码长度不能超过60",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(textProductName.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "商品名称不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(textProductName.getText().toString().length()>60)
        {
            Toast.makeText(getActivity().getApplicationContext(), "商品名称长度不能超过60",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(textProductPrice.getText().toString().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "商品价格不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        BigDecimal price= Arith.newBigDecimal(0);
        try
        {
            price =Arith.newBigDecimal(textProductPrice.getText().toString().trim());

        }catch (Exception e)
        {

        }
        int max = 10000000;
        if(price.compareTo(Arith.newBigDecimal(max))>0)
        {
            Toast.makeText(getActivity().getApplicationContext(), "商品价格不能超过"+max,
                    Toast.LENGTH_SHORT).show();
            return;
        }
  //      picPath = Environment.getExternalStorageDirectory().getPath()+"/Pictures/1.jpg";
 //       Log.i("smile",picPath);
  ///      File f = new File(picPath);
  //     assert(f.exists());
        if(picPath == null){
/*            Toast.makeText(getActivity().getApplicationContext(), "商品图片不能为空",
                    Toast.LENGTH_SHORT).show();
            return;*/
            picPath="";
        }
        if(textProductPrice.getText().toString().trim().indexOf(".")==textProductPrice.getText().toString().trim().length()-1){
            Toast.makeText(getActivity().getApplicationContext(), "商品价格格式不正确,小数点后不能为空,请检查后重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }



/*        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 60, 60);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        Log.d("path", picPath + " count=" + thumbnail.getByteCount() + " compress=" + outputStream.size());*/


        Product product = new Product(System.currentTimeMillis(), textBarcode.getText().toString(),"",  picPath, DataHelper.LOCAL_SUPPLIER,textProductName.getText().toString(), "",
                Utils.formatPriceNoSympol(getActivity(),textProductPrice.getText().toString().trim()),
                Utils.formatPriceNoSympol(getActivity(),textProductPrice.getText().toString().trim()), 255,DEFAULT_CATALOG_ID) ;

        long productId = productListFragment.addProduct(product);
        if (productId < 0)
        {
            ToastHelper.showToast(getActivity(),"添加商品失败,已有此条码对应的商品");
            return;
        }

        for (Attribute attribute : selectAttribute) {
            ProductAttribute productAttribute = new ProductAttribute(null, productId, attribute.getAttributeId());
            productAttributeManager.insert(productAttribute);
        }
        gridViewAdapter.notifyDataSetChanged();

        ToastHelper.showToast(getActivity(),"添加商品成功");
    }

    //end smile
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Log.d("TAG->onresult", "ActivityResult resultCode error");
            imageProduct.setImageResource(R.drawable.product_pic);
            picPath=null;
            return;
        }

/*        if (data == null){
            imageProduct.setImageResource(R.drawable.product_pic);
            return;
        }*/




      /*  //imageProduct.setImageURI(data.getData());

        Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getActivity().getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个

        if (requestCode == Utils.REQUEST_ALBUM_CODE||requestCode==Utils.REQUEST_CAMER_CODE) {
            try {
                Uri originalUri;
                if(data!=null) {
                    originalUri = data.getData();        //获得图片的uri
                }else{
                    originalUri=Utils.setOutImageUri();
                }
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
                //imgShow.setImageBitmap(bm);

               *//* String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);*//*

//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream() ;
//                thumbnail.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
//                Log.d("path", picPath + " count=" + thumbnail.getByteCount() + " compress=" + outputStream.size());
//                Bitmap bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
//                imageProduct.setImageBitmap(bitmap);


            } catch (IOException e) {

                Log.e("TAG-->Error", e.toString());

            }
        }*/
        Bitmap bm=Utils.getBmFromData(getActivity(),data,requestCode);
        if(bm!=null) {
            String path=getImageAbsolutePath(getActivity(),Utils.getDataUri(data,requestCode));
            Log.v("imag", path);
            picPath = path;

            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 60, 60);
            imageProduct.setImageBitmap(thumbnail);
        }else {
            imageProduct.setImageResource(R.drawable.product_pic);
        }
    }

    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }




    private void onBtnProductPic(){
/*        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType("image*//*");
        startActivityForResult(getAlbum, 1);*/
        if(DeviceInfo.checkCameraHardware(getActivity())) {
            PictureDialog pictureDialog = new PictureDialog(getActivity(), R.style.MyDialog, AddProductFragment.this, 1);
            pictureDialog.show();
        }else{
            Utils.launchCamera(AddProductFragment.this, 1, false);
        }
    }

    private void onBtnProductAttribute(){
        AddAttributeDialog dlg = new AddAttributeDialog(getActivity(), R.style.MyDialog, this);
        dlg.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave: onBtnSave();
                break;
            case R.id.btnProductPic: onBtnProductPic();
                break;
            case R.id.btnProductAttribute: onBtnProductAttribute();
                break;
        }
    }

    public AddProductFragment() {
        // Required empty public constructor
    }
    //smile_gao
    public  void setScancode(String scancode)
    {
        textBarcode.setText(scancode);
    }
    //end smile
    public void setParames(ProductListFragment productListFragment, Catalog catalog){
        this.productListFragment = productListFragment;
        //this.catalog = catalog;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        productAttributeManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getProductAttributeManager();


        imageProduct = (ImageView)view.findViewById(R.id.imageProduct);

        btnSave = (Button)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnProductAttribute = (LinearLayout)view.findViewById(R.id.btnProductAttribute);
        btnProductAttribute.setOnClickListener(this);

        btnProductPic = (LinearLayout)view.findViewById(R.id.btnProductPic);
        btnProductPic.setOnClickListener(this);

        TextFocusChangeListener textFocusChangeListener = new TextFocusChangeListener();
        textProductPrice = (EditText)view.findViewById(R.id.textProductPrice);
        textProductPrice.setOnFocusChangeListener(textFocusChangeListener);
        Utils.setPricePoint(textProductPrice);

        textProductName = (EditText)view.findViewById(R.id.textProductName);
        textProductName.setOnFocusChangeListener(textFocusChangeListener);

        textBarcode = (EditText)view.findViewById(R.id.textBarcode);

        gridAttribute = (GridView)view.findViewById(R.id.gridAttribute);
        gridAttribute.setSelector(new ColorDrawable(Color.TRANSPARENT));

        selectAttribute = new ArrayList<Attribute>();
        gridViewAdapter = new GridViewAdapter();
        gridAttribute.setAdapter(gridViewAdapter);

    }
}
