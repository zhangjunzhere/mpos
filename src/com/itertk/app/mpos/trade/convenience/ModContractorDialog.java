package com.itertk.app.mpos.trade.convenience;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.itertk.app.mpos.R;

/**
 * Created by Administrator on 2014/7/11.
 */
public class ModContractorDialog extends Dialog {
    static final String TAG = "BankChooseDialog";
    Button btnBack;

    Context context;

    Contractor contractor;

    Button btnDelete;
    Button btnOK;
    EditText textFullName;
    EditText textPhone;
    EditText textEmail;

    public void setContractor(Contractor contractor) {
        this.contractor = contractor;

        textFullName.setText(contractor.fullName);
        textPhone.setText(contractor.phone);
        textEmail.setText(contractor.email);
    }

    public Contractor getContractor() {
        return contractor;
    }

    public ModContractorDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_mod_contractor);

        textFullName = (EditText) findViewById(R.id.textFullName);
        textPhone = (EditText) findViewById(R.id.textPhone);
        textEmail = (EditText) findViewById(R.id.textEmail);


        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModContractorDialog.this.dismiss();
            }
        });

        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }


}
