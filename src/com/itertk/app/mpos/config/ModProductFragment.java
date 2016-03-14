package com.itertk.app.mpos.config;



import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.utility.Utils;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductAttribute;
import com.itertk.app.mpos.dbhelper.ProductAttributeDao;
import com.itertk.app.mpos.dbhelper.ProductDao;
import com.itertk.app.mpos.dbhelper.Retail;
import com.itertk.app.mpos.dbhelper.RetailDao;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *修改商品
 */
public class ModProductFragment extends Fragment implements View.OnClickListener{
    ProductListFragment productListFragment;
    Product product;
    ProductAttributeDao productAttributeManager;
    Button btnSave = null;
    LinearLayout btnProductPic = null;
    LinearLayout btnProductAttribute = null;
    EditText textProductPrice = null;
    EditText textProductName = null;
    EditText textBarcode;
    EditText textProductTradePrice=null;
    LinearLayout tradePriceArea = null;
    String picPath;
    GridView gridAttribute;
    ArrayList<Attribute> selectAttribute;
    GridViewAdapter gridViewAdapter;
    ImageView imageProduct;


    public void modAttribute(){
        productAttributeManager.deleteInTx(product.getProductAttributeList());

        for (Attribute attribute : selectAttribute) {
            ProductAttribute productAttribute = new ProductAttribute(null, product.getId(), attribute.getAttributeId());
            productAttributeManager.insert(productAttribute);
        }
        gridViewAdapter.notifyDataSetChanged();
    }

    class GridViewAdapter extends BaseAdapter {
        ProductAttributeDao productAttributeManager;
        List<ProductAttribute> productAttributeList;
        ArrayList<Attribute> selectAttribute;
        public GridViewAdapter(){
            this.productAttributeManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getProductAttributeManager();
            this.selectAttribute = ModProductFragment.this.selectAttribute;
            product.resetProductAttributeList();
            productAttributeList = product.getProductAttributeList();

           for (int i = 0; i < productAttributeList.size(); i++){
               selectAttribute.add(productAttributeList.get(i).getAttribute());
           }
        }
        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            product.resetProductAttributeList();
            productAttributeList = product.getProductAttributeList();

        }

        @Override
        public int getCount() {
            return productAttributeList.size();
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
            ProductAttribute productAttribute = productAttributeList.get(pos);
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.attribute_grid_item, null);
            }

            Button btnAttribute = (Button)view.findViewById(R.id.btnAttribute);
            if(productAttribute.getAttribute() != null)
            btnAttribute.setText(productAttribute.getAttribute().getName());

            Log.d("GridViewAdapter", "pos="+pos);

            return view;
        }
    }

    public ArrayList<Attribute> getSelectAttribute(){
        return selectAttribute;
    }

    private void onBtnSave(){
        if(textBarcode.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "商品条码不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(textProductName.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "商品名称不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(textProductPrice.getText().toString().trim().isEmpty()){
            Toast.makeText(getActivity().getApplicationContext(), "商品零售价不能为空",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(picPath == null){
/*            Toast.makeText(getActivity().getApplicationContext(), "商品图片不能为空",
                    Toast.LENGTH_SHORT).show();
            return;*/
            picPath="";
        }

        if(textProductPrice.getText().toString().trim().indexOf(".")==textProductPrice.getText().toString().trim().length()-1){
            Toast.makeText(getActivity().getApplicationContext(), "商品零售价格式不正确,小数点后不能为空,请检查后重新输入",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (productListFragment.isBarcodeExist(textBarcode.getText().toString().trim(),product)&&!product.getImage_url().startsWith("http"))
        {
            ToastHelper.showToast(getActivity(), "修改商品失败,已有此条码对应的商品");
            return;
        }
        product.setBar_code(textBarcode.getText().toString().trim());
        product.setProd_name(textProductName.getText().toString().trim());
        product.setSale_price(product.getSale_price().trim());
        product.setImage_url(picPath);
        Retail retail;
        RetailDao retailDao=((MPosApplication)getActivity().getApplication()).getDataHelper().getDaoSession().getRetailDao();
        retail=retailDao.load(product.getId());
        if(retail==null){
            retail=new Retail(product.getId(),Utils.formatPriceNoSympol(getActivity(),textProductPrice.getText().toString().trim()));
            retailDao.insert(retail);
        }else {
            //retail=product.getRetail();
            retail.setPrice(Utils.formatPriceNoSympol(getActivity(),textProductPrice.getText().toString().trim()));
            retailDao.update(retail);
        }
        product.setRetail(retail);

/*        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 60, 60);

       // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //thumbnail.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
     //   product.setDataPhoto(outputStream.toByteArray());*/
        long productId =productListFragment.modProduct(product);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK ) {
            Log.d("TAG->onresult", "ActivityResult resultCode error");
            imageProduct.setImageResource(R.drawable.product_pic);
            picPath = null;
            return;
        }

       /* Bitmap bm = null;
        //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getActivity().getContentResolver();
        //此处的用于判断接收的Activity是不是你想要的那个

        if (requestCode == 1) {
            try {
                Uri originalUri = data.getData();        //获得图片的uri
                bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                //显得到bitmap图片
                //imgShow.setImageBitmap(bm);

               *//* String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().managedQuery(originalUri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();*//*
                String path = AddProductFragment.getImageAbsolutePath(getActivity(),originalUri);
                //imgPath.setText(path);
                Log.v("imag", path);
                picPath = path;

                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 60, 60);
                imageProduct.setImageBitmap(thumbnail);
            } catch (IOException e) {

                Log.e("TAG-->Error", e.toString());

            }
        }*/
        Bitmap bm=Utils.getBmFromData(getActivity(),data,requestCode);
        if(bm!=null) {
            String path=AddProductFragment.getImageAbsolutePath(getActivity(), Utils.getDataUri(data, requestCode));
            Log.v("imag", path);
            picPath = path;

            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 60, 60);
            imageProduct.setImageBitmap(thumbnail);
        }else {
            imageProduct.setImageResource(R.drawable.product_pic);
        }
    }


    private void onBtnProductPic(){
/*        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType("image*//*");
        startActivityForResult(getAlbum, 1);*/
        if(DeviceInfo.checkCameraHardware(getActivity())) {
            PictureDialog pictureDialog = new PictureDialog(getActivity(), R.style.MyDialog, ModProductFragment.this, 1);
            pictureDialog.show();
        }else{
            Utils.launchCamera(ModProductFragment.this, 1, false);
        }
    }

    private void onBtnProductAttribute(){
        ModAttributeDialog modAttributeDialog = new ModAttributeDialog(getActivity(),R.style.MyDialog, this);
        modAttributeDialog.show();
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

    public ModProductFragment (){

    }

    public void setParames(ProductListFragment productListFragment, Product product) {
        // Required empty public constructor
        this.productListFragment = productListFragment;
        this.product = product;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        if(product!=null){
            savedInstanceState.putLong("PRODUCT", product.getId());
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mod_product, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState!=null){
            long id=savedInstanceState.getLong("PRODUCT");
            ProductDao productManager;
            productManager = ((MPosApplication)getActivity().getApplication()).getDataHelper().getProductManager();
            product=productManager.load(id);
        }

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

        textProductTradePrice = (EditText)view.findViewById(R.id.textProductTradePrice);
        textProductTradePrice.setOnFocusChangeListener(textFocusChangeListener);
        Utils.setPricePoint(textProductTradePrice);

        tradePriceArea=(LinearLayout)view.findViewById(R.id.tradePrice_area);

        textProductName = (EditText)view.findViewById(R.id.textProductName);
        textProductName.setOnFocusChangeListener(textFocusChangeListener);

        textBarcode = (EditText)view.findViewById(R.id.textBarcode);

        gridAttribute = (GridView)view.findViewById(R.id.gridAttribute);
        gridAttribute.setSelector(new ColorDrawable(Color.TRANSPARENT));

        selectAttribute = new ArrayList<Attribute>();


        gridViewAdapter = new GridViewAdapter();
        gridAttribute.setAdapter(gridViewAdapter);


        textProductName.setText(product.getProd_name());
        textProductPrice.setText(String.valueOf(product.getLocalPrice()));
        textBarcode.setText(product.getBar_code());
        textProductTradePrice.setText(String.valueOf(product.getPrice()));

        picPath = product.getImage_url();

        if(!picPath.toLowerCase().startsWith("http"))
        {
            Bitmap bitmap = BitmapFactory.decodeFile(picPath);
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, 60, 60);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if(thumbnail !=null) {
                thumbnail.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
                imageProduct.setImageBitmap(bitmap);
            }
            float alpha=1.0f;
            textProductName.setAlpha(alpha);
            textProductName.setEnabled(true);
            textBarcode.setAlpha(alpha);
            textBarcode.setEnabled(true);
            btnProductPic.setAlpha(alpha);
            btnProductPic.setEnabled(true);
            textProductTradePrice.setAlpha(alpha);
            textProductTradePrice.setEnabled(true);
            tradePriceArea.setVisibility(View.GONE);
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)btnSave.getLayoutParams();
            params.topMargin=110;
            btnSave.setLayoutParams(params);
        }
        else
        {
            ImageLoader.getInstance().displayImage(picPath,  imageProduct);
            float alpha=0.5f;
            textProductName.setAlpha(alpha);
            textProductName.setEnabled(false);
            textBarcode.setAlpha(alpha);
            textBarcode.setEnabled(false);
            btnProductPic.setAlpha(alpha);
            btnProductPic.setEnabled(false);
            textProductTradePrice.setAlpha(alpha);
            textProductTradePrice.setEnabled(false);
            tradePriceArea.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)btnSave.getLayoutParams();
            params.topMargin=20;
            btnSave.setLayoutParams(params);
        }
    }
}
