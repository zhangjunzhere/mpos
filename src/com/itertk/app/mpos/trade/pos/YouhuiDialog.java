package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.ToastHelper;

import java.math.BigDecimal;

/**
 * Created by smile_gao on 2015/3/31.
 */
public class YouhuiDialog  extends Dialog{
    EditText etYouhuiprice;
    Button btnok;
    TextView tvTitle;
    ImageView backiv;
    BigDecimal youhuiprice;
    BigDecimal mMaxYouhui;
    TextView maxtv;
    public  BigDecimal getYouhuiprice()
    {
        return youhuiprice;
    }
    public YouhuiDialog(Context context, int theme,BigDecimal youhui, BigDecimal maxYouhui)
    {
           super(context,theme);
           youhuiprice = youhui;
        mMaxYouhui = maxYouhui;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_youhui);
      //  tvTitle.setText("优惠");
        etYouhuiprice = (EditText)findViewById(R.id.youhuiprice);
        btnok = (Button) findViewById(R.id.ok);
        backiv = (ImageView) findViewById(R.id.dialogback);
        maxtv = (TextView) findViewById(R.id.tvyouhuimax);
        maxtv.setText("最大优惠金额: "+ DecimalHelper.getDecimalString(mMaxYouhui));
        backiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        etYouhuiprice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i2, int i3) {
                int dotindex = s.toString().indexOf(".");

                if (dotindex == -1) {
                    if (s.length() > 8) {
                        s = s.subSequence(0, 8);
                        etYouhuiprice.setText(s);
                        etYouhuiprice.setSelection(s.length());
                        return;
                    }
                } else {
                    if (dotindex > 8) {
                        CharSequence zhenshu = s.subSequence(0, 8);
                        CharSequence xiaoshu = s.subSequence(dotindex, s.length());
                        s = zhenshu.toString() + xiaoshu.toString();
                        Log.i("smile", "dotindex: " + dotindex + " " + s);
                        etYouhuiprice.setText(s);
                        etYouhuiprice.setSelection(i);
                        return;
                    }
                }

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etYouhuiprice.setText(s);
                        etYouhuiprice.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etYouhuiprice.setText(s);
                    etYouhuiprice.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etYouhuiprice.setText(s.subSequence(0, 1));
                        etYouhuiprice.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if(youhuiprice!=null)
        {
            etYouhuiprice.setText(youhuiprice.toString());
            etYouhuiprice.setSelection(youhuiprice.toString().length());
        }
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etYouhuiprice.getText()==null||etYouhuiprice.getText().toString().length()==0)
                {
                    etYouhuiprice.setText("0");
                }

                youhuiprice = Arith.newBigDecimal(etYouhuiprice.getText().toString().trim());
                if(youhuiprice.compareTo(mMaxYouhui)>0)
                {
                    ToastHelper.showToast(getContext(),"优惠金额不能超过最大金额");
                    youhuiprice = Arith.newZeroBigDecimal();
                    return;
                }
                dismiss();
            }
        });

    }
}
