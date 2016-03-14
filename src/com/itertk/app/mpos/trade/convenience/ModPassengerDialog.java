package com.itertk.app.mpos.trade.convenience;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.R;

/**
 * Created by Administrator on 2014/7/11.
 */
public class ModPassengerDialog extends Dialog {
    static final String TAG = "BankChooseDialog";
    Button btnBack;

    Context context;

    Passenger passenger;

    EditText textFullName;
    TextView textIDType;

    Button btnIDTypeLicense;
    Button btnIDTypePassport;
    Button btnIDTypeOther;
    EditText textID;
    Button btnOK;
    Button btnDelete;

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }


    public ModPassengerDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_mod_passenger);


        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModPassengerDialog.this.dismiss();
            }
        });


        textFullName = (EditText) findViewById(R.id.textFullName);
        textIDType = (TextView) findViewById(R.id.textIDType);
        textID = (EditText) findViewById(R.id.textID);


        btnIDTypeLicense = (Button) findViewById(R.id.btnIDTypeLicense);
        btnIDTypePassport = (Button) findViewById(R.id.btnIDTypePassport);
        btnIDTypeOther = (Button) findViewById(R.id.btnIDTypeOther);


        textFullName.setText(passenger.fullName);
        textIDType.setText(passenger.idType);
        textID.setText(passenger.idNo);

        if (passenger.idType.compareTo(btnIDTypeLicense.getText().toString()) == 0) {
            btnIDTypeLicense.setSelected(true);
        } else {
            if (passenger.idType.compareTo(btnIDTypePassport.getText().toString()) == 0) {
                btnIDTypePassport.setSelected(true);
            } else {
                if (passenger.idType.compareTo(btnIDTypeOther.getText().toString()) == 0) {
                    btnIDTypeOther.setSelected(true);
                }
            }
        }


        btnIDTypeLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIDTypeLicense.setSelected(true);
                btnIDTypePassport.setSelected(false);
                btnIDTypeOther.setSelected(false);
                textIDType.setText(btnIDTypeLicense.getText());
            }
        });

        btnIDTypePassport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIDTypeLicense.setSelected(false);
                btnIDTypePassport.setSelected(true);
                btnIDTypeOther.setSelected(false);
                textIDType.setText(btnIDTypePassport.getText());
            }
        });

        btnIDTypeOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIDTypeLicense.setSelected(false);
                btnIDTypePassport.setSelected(false);
                btnIDTypeOther.setSelected(true);
                textIDType.setText(btnIDTypeOther.getText());
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

                if (textIDType.getText().toString().isEmpty()) {
                    Toast.makeText(context, "证件类型不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (textID.getText().toString().isEmpty()) {
                    Toast.makeText(context, "证件号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                passenger.fullName = textFullName.getText().toString();
                passenger.idType = textIDType.getText().toString();
                passenger.idNo = textID.getText().toString();

                ModPassengerDialog.this.dismiss();
            }
        });

        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passenger = null;
                ModPassengerDialog.this.dismiss();
            }
        });

    }


}
