package com.itertk.app.mpos;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.itertk.app.mpos.comm.LinkeaResponseMsg;

import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.itertk.app.mpos.locationhelper.Bank;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/7/11.
 * 选择支行对话框
 */
public class BankBranchChooseDialog extends Dialog {
    static final String TAG = "BankBranchChooseDialog";
    Button btnBack;
    ListView listBankBranch;
    BankBranchAdapter bankBranchAdapter;
    Bank bank;
    Context context;
    String nameSearch;
    List<LinkeaResponseMsg.GetBankBranchResponseMsg.Branch> branchList;
    private int lastItem;
    private Integer index =1;
    LinkeaResponseMsg.GetBankBranchResponseMsg.Branch branchSelected = null;
    TextView textNoInfo;
    ProgressBar progressBar;

    public LinkeaResponseMsg.GetBankBranchResponseMsg.Branch getSelectedBankBranch(){
        return branchSelected;
    }

    public BankBranchChooseDialog(Context context, int theme, Bank bank, String nameSearch){
        super(context, theme);
        this.context = context;
        this.bank = bank;
        this.nameSearch = nameSearch;
        setCanceledOnTouchOutside(false);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_bank_branch_choose);


        btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankBranchChooseDialog.this.dismiss();
            }
        });

        listBankBranch = (ListView)findViewById(R.id.listBankBranch);
        bankBranchAdapter =  new BankBranchAdapter();
        listBankBranch.setAdapter(bankBranchAdapter);


        listBankBranch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            branchSelected = (LinkeaResponseMsg.GetBankBranchResponseMsg.Branch)parent.getAdapter().getItem(position);

            Log.d(TAG, branchSelected.code +" " + branchSelected.name);


                dismiss();
            }
        });

        textNoInfo=(TextView)findViewById(R.id.text_no_info);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);



      listBankBranch.setOnScrollListener(new AbsListView.OnScrollListener() {
          @Override
          public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (lastItem == view.getAdapter().getCount() && scrollState == this.SCROLL_STATE_IDLE){
                index++;
                loadMoreData();
            }
          }

          @Override
          public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
              lastItem = firstVisibleItem + visibleItemCount;
          }
      });
    }

    private void loadMoreData(){
        ((MPosApplication) getContext().getApplicationContext()).getMsgBuilder().buildGetBankBranch(bank.getCode().toString(), nameSearch, index.toString(), "20").send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                textNoInfo.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    LinkeaResponseMsg.GetBankBranchResponseMsg getBankBranchResponseMsg =
                            LinkeaResponseMsgGenerator.generateGetBankBranchResponseMsg(responseString);
                    if (getBankBranchResponseMsg.isSuccess()){
                        Log.d(TAG, getBankBranchResponseMsg.toString());

                        branchList.addAll(getBankBranchResponseMsg.branchs);
                        if(branchList.isEmpty()){
                            textNoInfo.setVisibility(View.VISIBLE);
                        }
                        bankBranchAdapter.notifyDataSetChanged();
                    }else{
                        if(branchList.isEmpty()){
                            textNoInfo.setVisibility(View.VISIBLE);
                        }
                    }

                }catch (Exception e){

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private class BankBranchAdapter extends BaseAdapter {


        BankBranchAdapter(){
            branchList = new ArrayList<LinkeaResponseMsg.GetBankBranchResponseMsg.Branch>();

            loadMoreData();
        }


        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return branchList.size();
        }

        @Override
        public Object getItem(int position) {
            return branchList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            LinkeaResponseMsg.GetBankBranchResponseMsg.Branch branch = branchList.get(pos);
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                view = inflater.inflate(R.layout.bank_list_item, null);
            }

            TextView textBank = (TextView)view.findViewById(R.id.textBank);
            textBank.setText(branch.name);

            return view;
        }

    }
}
