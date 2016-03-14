package com.itertk.app.mpos.config;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.itertk.app.mpos.BlankFragment;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.CatalogDao;

import java.util.List;

public class ProductConfigActivity extends Activity implements View.OnClickListener{
    LinearLayout btnProduct = null;
    LinearLayout btnAttribute = null;
    LinearLayout btnReduce = null;


    CatalogListFragment catalogListFragment = null;
    AttributeListFragment attributeListFragment = null;
    ReduceListFragment reduceListFragment = null;
    ProductListFragment productListFragment = null;
    //smile add for scan code
    EditText textInputGun;
    String scanCode="";


    FragmentTransaction transaction;

    public void onProductListFragmentBack(){
        onbtnProduct();
    }

    public void onCatalogItemClick(Catalog catalog){
        if (null == productListFragment) {
            productListFragment = new ProductListFragment();
        }
        productListFragment.setParames(catalog);
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_container, productListFragment);
        transaction.commit();

        clearItemArea();
    }


    private void clearItemArea(){
        BlankFragment blankFragment = new BlankFragment();
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.itemArea, blankFragment);
        transaction.commit();
    }

    private void onbtnProduct(){
        btnProduct.setSelected(true);
        btnAttribute.setSelected(false);
        btnReduce.setSelected(false);

        if (null == catalogListFragment)
        {
            catalogListFragment = new CatalogListFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_container, catalogListFragment);
        transaction.commit();

        clearItemArea();
    }

    private void onbtnAttribute(){
        btnProduct.setSelected(false);
        btnAttribute.setSelected(true);
        btnReduce.setSelected(false);

        if (null == attributeListFragment)
        {
            attributeListFragment = new AttributeListFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_container, attributeListFragment);
        transaction.commit();

        clearItemArea();
    }

    private void onbtnReduce(){
        btnProduct.setSelected(false);
        btnAttribute.setSelected(false);
        btnReduce.setSelected(true);

        if (null == reduceListFragment)
        {
            reduceListFragment = new ReduceListFragment();
        }
        transaction = getFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.list_container, reduceListFragment);
        transaction.commit();

        clearItemArea();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnProduct: onbtnProduct(); break;
            case R.id.btnAttribute: onbtnAttribute(); break;
            case R.id.btnReduce: onbtnReduce(); break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_config);
        MyActionbar.setConfigActionBarLayout(this);

        btnProduct = (LinearLayout)findViewById(R.id.btnProduct);
        btnProduct.setOnClickListener(this);

        btnAttribute = (LinearLayout)findViewById(R.id.btnAttribute);
        btnAttribute.setOnClickListener(this);

        btnReduce = (LinearLayout)findViewById(R.id.btnReduce);
        btnReduce.setOnClickListener(this);

        //smile_gao add for scan code
        textInputGun = (EditText) findViewById(R.id.textInputGun);
        textInputGun.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d("textInput", "smile "+scanCode);
                Log.d("textInput", "getDevice Name:  "+event.getDevice().getName()+"  "+event.getNumber());
                if (event.getDevice().getName().contains("HID")) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_ENTER) {
                            Log.d("textInput productListFragment", scanCode);
                            productListFragment.setScanCode(scanCode);
                            scanCode = "";
                        } else {
                            if(event.getScanCode()<10)
                                scanCode += event.getNumber();
                            else
                            {
                                scanCode += (char) event.getUnicodeChar();
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        //onbtnProduct();

        catalogManager = ((MPosApplication)this.getApplication()).getDataHelper().getCatalogManager();
        catalogList = catalogManager.loadAll();
        Catalog catalog =new Catalog(AddProductFragment.DEFAULT_CATALOG_ID,"local",null);
        if(catalogList.size()==0){
            addCatalog(catalog);
        }else {
            catalog = catalogList.get(0);
        }
        onCatalogItemClick(catalog);
    }
    CatalogDao catalogManager;
    List<Catalog> catalogList;
    public long addCatalog(Catalog catalog){
        long result = 0;

        try {
            result = catalogManager.insert(catalog);
        }catch (Exception e){
            result = -1;
        }

        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.product_config, menu);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
