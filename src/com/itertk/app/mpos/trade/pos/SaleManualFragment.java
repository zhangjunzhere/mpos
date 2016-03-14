package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itertk.app.mpos.R;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 * 手动
 */
public class SaleManualFragment extends Fragment {
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnPoint, btnCut;
    private ImageButton btnPlus, btnDel;
    private TextView txtMoney;
    private String inter = new String();
    private Boolean flag = true;
    private Boolean pointFlag = false;
    private String decimal = new String();
    private  final  static  int MAX_NUM =10;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn0 = (Button) view.findViewById(R.id.button14);
        btn1 = (Button) view.findViewById(R.id.button1);
        btn2 = (Button) view.findViewById(R.id.button2);
        btn3 = (Button) view.findViewById(R.id.button3);
        btn4 = (Button) view.findViewById(R.id.button5);
        btn5 = (Button) view.findViewById(R.id.button6);
        btn6 = (Button) view.findViewById(R.id.button7);
        btn7 = (Button) view.findViewById(R.id.button9);
        btn8 = (Button) view.findViewById(R.id.button10);
        btn9 = (Button) view.findViewById(R.id.button11);
        btnPoint = (Button) view.findViewById(R.id.button13);
        btnPlus = (ImageButton) view.findViewById(R.id.button12);
        btnDel = (ImageButton) view.findViewById(R.id.button4);
        btnCut = (Button) view.findViewById(R.id.button8);
        txtMoney = (TextView) view.findViewById(R.id.moneyInputView);

    

        btn0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2)
                        decimal += '0';
                } else {
                    if (!inter.isEmpty())
                    {
                        if(inter.length()<MAX_NUM)
                              inter += '0';
                    }
                }
                displayMoney();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '1';
                } else
                {
                    if(inter.length()<MAX_NUM)
                           inter += '1';
                }
                displayMoney();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '2';
                } else
                {
                    if(inter.length()<MAX_NUM)
                    inter += '2';
                }
                displayMoney();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '3';
                } else
                {
                    if(inter.length()<MAX_NUM)
                        inter += '3';
                }

                displayMoney();
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '4';
                } else
                {
                    if(inter.length()<MAX_NUM)
                      inter += '4';
                }
                displayMoney();
            }
        });
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '5';
                } else
                {
                    if(inter.length()<MAX_NUM)
                       inter += '5';
                }
                displayMoney();
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '6';
                } else
                {
                    if(inter.length()<MAX_NUM)
                      inter += '6';
                }
                displayMoney();
            }
        });
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '7';
                } else
                {
                    if(inter.length()<MAX_NUM)
                        inter += '7';
                }
                displayMoney();
            }
        });
        btn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '8';
                } else
                {
                    if(inter.length()<MAX_NUM)
                         inter += '8';
                }
                displayMoney();
            }
        });
        btn9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointFlag) {
                    if (decimal.length() < 2) decimal += '9';
                } else
                {
                    if(inter.length()<MAX_NUM)
                      inter += '9';
                }
                displayMoney();
            }
        });

        btnCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  flag = !flag;
                inter = new String();
                decimal = new String();
                pointFlag =false;
                displayMoney();
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!decimal.isEmpty()) {
                    decimal = decimal.substring(0, decimal.length() - 1);
                } else {
                    pointFlag = false;
                    if (!inter.isEmpty())
                        inter = inter.substring(0, inter.length() - 1);
                }
                displayMoney();
            }
        });

        btnPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointFlag = true;
                displayMoney();
            }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = displayMoney();
                ((PosActivity) getActivity()).addManualItem(money);
                inter = "";
                decimal = "";
                pointFlag = false;
                flag = true;
                displayMoney();
            }
        });

        displayMoney();
    }

    private String displayMoney() {
        String money = new String();

        if (flag) money += "￥";
        else money += "-￥";

        if (inter.isEmpty()) money += "0";
        else money += inter;

        if (decimal.length() == 2) money += "." + decimal.substring(0, 2);
        else if (decimal.length() == 1) money += "." + decimal.substring(0, 1) + "0";
        else money += ".00";

        txtMoney.setText(money);

        String floatMoney = "0";
        if (flag) {
            floatMoney =money.substring(1);
        } else {
            floatMoney =money.substring(2);
        }

        return floatMoney;
    }

    public SaleManualFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_manual, container, false);
    }


}
