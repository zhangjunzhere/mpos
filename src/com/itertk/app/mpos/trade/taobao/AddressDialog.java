package com.itertk.app.mpos.trade.taobao;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ToastHelper;
import com.itertk.app.mpos.dbhelper.Address;
import com.itertk.app.mpos.dbhelper.AddressDao;
import com.itertk.app.mpos.utility.DataHelper;

import java.util.List;

/**
 * Created by smile_gao on 2015/3/20.
 * 地址选择对话框
 */
public class AddressDialog extends Dialog implements View.OnClickListener {
    AddressDao addressDao;
    ListView mAddressList ;
    AddressAdapter addressAdapter;

    EditText address1;
    String mPrefAddress;
    Button btnOk;
    RelativeLayout editlayout;
    Button btnUuseaddress;

    public AddressDialog(Context context, int theme, String prefAddress) {
        super(context, theme);
        mPrefAddress = prefAddress;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.ok:
                addAddressToDb();
                break;
            case R.id.btnBack:
                dismiss();
                break;
        }
    }
    public  void useSelectAddress()
    {
//        Address address = addressAdapter.getSelectedItem();
//        if(address ==null)
//            return;
//        mUseAddress = address;
//        dismiss();
    }
    public  String getmUseAddress()
    {
        return  mPrefAddress;
    }

    private  void addAddressToDb()
    {
        String addr1 = this.address1.getText().toString();
        if(addr1.equals(""))
        {
            ToastHelper.showToast(getContext(),"请填写完地址");
            return;
        }
        Address addr = new Address(null,addr1,false);
        List<Address> list = addressDao.queryBuilder().where(AddressDao.Properties.Address1.eq(addr1)).list();
        if(list == null || list.size() == 0) {
            addressDao.insert(addr);
            clearInputs();
            loadData();
            ToastHelper.showToast(getContext(),"添加地址成功");
        }
        else
        {
            ToastHelper.showToast(getContext(),"地址已存在");
        }
    }
    private  void addNewAddress()
    {
        clearInputs();
        showBtnOk();
        editlayout.setVisibility(View.VISIBLE);
    }
    private  void clearInputs()
    {

        this.address1.setText("");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.address_dialog);


        mAddressList =  (ListView) findViewById(R.id.addresslist);
        mAddressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Address address= (Address)addressAdapter.getItem(i);
                mPrefAddress = address.getAddress1();
                Log.i("smile","pos "+i);
                addressAdapter.setSelectIndex(i);
                addressAdapter.notifyDataSetChanged();

                MPosApplication mPosApplication = MPosApplication.getInstance();
                addressDao =  mPosApplication.getDataHelper().getDaoSession().getAddressDao();
                List<Address> list = addressDao.loadAll();
                for(Address item : list){
                    Boolean defaultAddr = mPrefAddress.equals(item.getAddress1());
                    item.setMoren(defaultAddr);
                }
                addressDao.updateInTx(list);
                AddressDialog.this.dismiss();
            }
        });

        loadData();

        address1 = (EditText) findViewById(R.id.address1);

        btnOk = (Button) findViewById(R.id.ok);
        btnOk.setOnClickListener(this);
        editlayout = (RelativeLayout) findViewById(R.id.editlayout);
        findViewById(R.id.btnBack).setOnClickListener(this);

    }

    private void deleteAddress()
    {
        Address address = addressAdapter.getSelectedItem();
        if(address ==null)
            return;
        addressDao.delete(address);
        loadData();
        Toast.makeText(getContext(),"删除成功",Toast.LENGTH_SHORT);
    }
    private  void showBtnOk()
    {
        btnOk.setVisibility(View.VISIBLE);
    }
    private  void showBtnUpdate()
    {
        btnOk.setVisibility(View.GONE);
    }



    private  void loadData()
    {
        DataHelper dbHelper =  MPosApplication.getInstance().getDataHelper();
        MPosApplication mPosApplication = MPosApplication.getInstance();
        addressDao =  dbHelper.getDaoSession().getAddressDao();
        List<Address> list = addressDao.loadAll();
        addressAdapter = new AddressAdapter(getContext(),list,mPrefAddress);
        mAddressList.setAdapter(addressAdapter);
    }
}
