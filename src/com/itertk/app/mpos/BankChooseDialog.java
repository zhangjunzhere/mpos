package com.itertk.app.mpos;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.itertk.app.mpos.locationhelper.Bank;
import com.itertk.app.mpos.locationhelper.BankDao;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2014/7/11.
 * 选择银行对话框
 */
public class BankChooseDialog extends Dialog {
    static final String TAG = "BankChooseDialog";
    Button btnBack;
    ListView listBank;
    BankAdapter bankAdapter;
    Bank bankSelected;


    Context context;


    public Bank getBankSelected(){
        return bankSelected;
    }

    public BankChooseDialog(Context context, int theme){
        super(context, theme);
        this.context = context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_bank_choose);


        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankChooseDialog.this.dismiss();
            }
        });

        listBank = (ListView)findViewById(R.id.listBank);
        bankAdapter =  new BankAdapter();
        listBank.setAdapter(bankAdapter);


       listBank.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               bankSelected = (Bank)parent.getAdapter().getItem(position);

               Log.d(TAG, bankSelected.getName());

               dismiss();
           }
       });
    }


    private class BankAdapter extends BaseAdapter {
        BankDao bankManager;
        List<Bank> bankList;
        BankAdapter(){
            bankManager = ((MPosApplication)getContext().getApplicationContext()).getLocationHelper().getBankManager();
            bankList = bankManager.loadAll();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return bankList.size();
        }

        @Override
        public Object getItem(int position) {
            return bankList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Bank bank = bankList.get(pos);
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.bank_list_item, null);
            }

            TextView textBank = (TextView)view.findViewById(R.id.textBank);
            textBank.setText(bank.getName());

            return view;
        }

    }
}
