package com.itertk.app.mpos.trade.convenience;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.itertk.app.mpos.R;

/**
 * Created by Administrator on 2014/7/11.
 */
public class AddContractorDialog extends Dialog {
    static final String TAG = "BankChooseDialog";
    Button btnBack;
    Button btnOK;

    Context context;

    EditText textFullName;
    EditText textPhone;
    EditText textEmail;
    Contractor contractor;

    public Contractor getContractor() {
        return contractor;
    }


    public AddContractorDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_add_contractor);

        textFullName = (EditText) findViewById(R.id.textFullName);
        textPhone = (EditText) findViewById(R.id.textPhone);
        textEmail = (EditText) findViewById(R.id.textEmail);


        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddContractorDialog.this.dismiss();
            }
        });


        btnOK = (Button) findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textFullName.getText().toString().isEmpty()) {
                    Toast.makeText(context, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textPhone.getText().toString().isEmpty()) {
                    Toast.makeText(context, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                contractor = new Contractor(textFullName.getText().toString(), textPhone.getText().toString(), textEmail.getText().toString());

                AddContractorDialog.this.dismiss();

            }
        });
    }


}
