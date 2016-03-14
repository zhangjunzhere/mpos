package com.itertk.app.mpos.trade.convenience;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.BankBranchChooseDialog;
import com.itertk.app.mpos.BankChooseDialog;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.locationhelper.Bank;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseBankFragment extends Fragment {
    RelativeLayout btnBank = null;
    Button btnSearch = null;
    Button btnOK;
    TextView textBank;
    TextView textBankDescription;
    EditText textSearch = null;
    TextView textKHH;

    Bank bank;
    LinkeaResponseMsg.GetBankBranchResponseMsg.Branch branch;

    public ChooseBankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_bank, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textBank = (TextView) view.findViewById(R.id.textBank);
        textBankDescription = (TextView) view.findViewById(R.id.textBankDescription);
        textKHH = (TextView) view.findViewById(R.id.textKHH);
        textSearch = (EditText) view.findViewById(R.id.textSearch);

        btnBank = (RelativeLayout) view.findViewById(R.id.btnBank);
        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BankChooseDialog bankChooseDialog = new BankChooseDialog(getActivity(), R.style.MyDialog);
                bankChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        bank = ((BankChooseDialog) dialog).getBankSelected();

                        if (bank == null) {
                            textBank.setText("请选择银行");
                            textBankDescription.setVisibility(View.VISIBLE);
                        } else {
                            textBank.setText(bank.getName());
                            textBankDescription.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                bankChooseDialog.show();
            }
        });

        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bank.getName() == null || bank.getName().isEmpty()) {
                    Toast.makeText(getActivity(), "请选择银行",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textSearch.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "请输入分行或者支行名称，再重新检索",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                BankBranchChooseDialog bankBranchChooseDialog = new BankBranchChooseDialog(getActivity(), R.style.MyDialog, bank, textSearch.getText().toString());
                bankBranchChooseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        branch = ((BankBranchChooseDialog) dialog).getSelectedBankBranch();
                        if (branch == null) {

                        } else {
                            textKHH.setText(branch.name);
                        }
                    }
                });

                bankBranchChooseDialog.show();
            }
        });

        btnOK = (Button) view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
