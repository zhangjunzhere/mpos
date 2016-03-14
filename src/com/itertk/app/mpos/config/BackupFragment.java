package com.itertk.app.mpos.config;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MessageDialog;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.Attribute;
import com.itertk.app.mpos.dbhelper.AttributeDao;
import com.itertk.app.mpos.dbhelper.AttributeValue;
import com.itertk.app.mpos.dbhelper.AttributeValueDao;
import com.itertk.app.mpos.dbhelper.Catalog;
import com.itertk.app.mpos.dbhelper.CatalogDao;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;
import com.itertk.app.mpos.dbhelper.SaleOrderItem;
import com.itertk.app.mpos.dbhelper.SaleOrderItemDao;
import com.itertk.app.mpos.dbhelper.User;
import com.itertk.app.mpos.trade.taobao.MessageBoxDialog;
import com.itertk.app.mpos.utility.DataHelper;
import com.itertk.app.mpos.dbhelper.Product;
import com.itertk.app.mpos.dbhelper.ProductAttribute;
import com.itertk.app.mpos.dbhelper.ProductAttributeDao;
import com.itertk.app.mpos.dbhelper.ProductDao;
import com.itertk.app.mpos.dbhelper.Reduce;
import com.itertk.app.mpos.dbhelper.ReduceDao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 备份
 */
public class BackupFragment extends Fragment {
    final private static String TAG = "BackupFragment";

    LinearLayout btnSave;
    LinearLayout btnLoad;



    public BackupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_backup, container, false);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要上传的文件"),
                    1);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(getActivity(), "请安装文件管理器", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private static void copyFile(String source, String target) {
        Log.d(TAG, "cp " + source + " ------->>>> " + target);
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);






        btnSave = (LinearLayout)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, Environment.getExternalStorageDirectory().toString());


                SimpleDateFormat simpleFormat = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
                String target = Environment.getExternalStorageDirectory().toString() + "/" +simpleFormat.format(new Date()) + ".bak";
                copyFile(getActivity().getDatabasePath("mpos.db").toString(), target);

                MessageDialog messageDialog = new MessageDialog(getActivity());
                messageDialog.show();
                messageDialog.setTitle(" 导出成功!", target);
            }
        });

        btnLoad = (LinearLayout)view.findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();



            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            // Get the Uri of the selected file
            Log.d(TAG, data.getData().getPath());
            String target = getActivity().getDatabasePath("mpos.bak").getAbsolutePath();

            String path = data.getData().getPath();
            path = path.substring(path.indexOf(':') + 1);

            String source = Environment.getExternalStorageDirectory().toString() + File.separator +path;
            copyFile(source, target);

            DataHelper backDataHelper = new DataHelper(getActivity(), "mpos.bak");
            final List<User> users = backDataHelper.getUserManager().loadAll();
            //at least user is not null
            if(users.size() <= 0){
                MessageBoxDialog messasgeBox =  new MessageBoxDialog(getActivity(),"数据导入失败","请确认导入有效的 .bak 文件",false);
                messasgeBox.show();
                return;
            }


            final List<SaleOrder> saleOrders =  backDataHelper.getSaleOrderManager().loadAll();
            final List<SaleOrderItem> saleOrderItems = backDataHelper.getSaleOrderItemManager().loadAll();

            MPosApplication mPosApplication =  (MPosApplication)getActivity().getApplication();
            final SaleOrderDao saleOrderDao = mPosApplication.getDataHelper().getDaoSession().getSaleOrderDao();
            final SaleOrderItemDao saleOrderItemDao = mPosApplication.getDataHelper().getDaoSession().getSaleOrderItemDao();

            mPosApplication.getDataHelper().getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    saleOrderDao.deleteAll();
                    saleOrderItemDao.deleteAll();
                    saleOrderDao.insertInTx(saleOrders);
                    saleOrderItemDao.insertInTx(saleOrderItems);
                }
            });



            MessageBoxDialog messasgeBox =  new MessageBoxDialog(getActivity(),"数据恢复成功",true);
            messasgeBox.show();
        }
    }
}
