package com.itertk.app.mpos.trade.pos;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.PrinterService;
import com.itertk.app.mpos.utility.Arith;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.dbhelper.SaleOrder;
import com.itertk.app.mpos.dbhelper.SaleOrderDao;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * 销售统计
 */
public class SaleStatisticFragment extends Fragment implements View.OnClickListener {
    LinearLayout btnStartTime;
    LinearLayout btnEndTime;

    TextView textStartTime;
    TextView textEndTime;
    Date dateStart;
    Date dateEnd;

    TextView textSaleOrderCount;
    TextView textBankcard;
    TextView textCash;
    TextView textTotalSale;

    LinearLayout btnPrint;

    SaleOrderDao saleOrderManager;

    private void onbtnStartTime() {
        final PickTimeDialog pickTimeDialog = new PickTimeDialog(getActivity(), R.style.MyDialog, dateStart);
        pickTimeDialog.show();

        pickTimeDialog.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date test=new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                if(dateStart.after(test)){
                    Toast.makeText(getActivity(),"开始时间不能在当前时间之后,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dateEnd.before(dateStart)){
                    Toast.makeText(getActivity(),"开始时间不能在结束时间之后,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                textStartTime.setText(simpleDateFormat.format(dateStart));
                pickTimeDialog.dismiss();
                displayStatistic();
            }
        });


    }

    private void onbtnEndTime() {
        final PickTimeDialog pickTimeDialog = new PickTimeDialog(getActivity(), R.style.MyDialog, dateEnd);
        pickTimeDialog.show();

        pickTimeDialog.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date test=new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                if(dateEnd.after(test)){
                    Toast.makeText(getActivity(),"结束时间不能在当前时间之前,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(dateEnd.before(dateStart)){
                    Toast.makeText(getActivity(),"结束时间不能在开始时间之前,请重新选择",Toast.LENGTH_SHORT).show();
                    return;
                }
                textEndTime.setText(simpleDateFormat.format(dateEnd));
                pickTimeDialog.dismiss();
                displayStatistic();
            }
        });


    }

    private void onbtnPrint() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("             销售统计\n");
        stringBuffer.append("开始时间：" + textStartTime.getText().toString() + "\n");
        stringBuffer.append("结束时间：" + textEndTime.getText().toString() + "\n");
        stringBuffer.append("================================");
        stringBuffer.append("销售总额        " + textTotalSale.getText().toString() + "\n");
        stringBuffer.append("现金消费        " + textCash.getText().toString() + "\n");
        stringBuffer.append("刷卡消费        " + textBankcard.getText().toString() + "\n");
        stringBuffer.append("交易数量        " + textSaleOrderCount.getText().toString() + "\n");
        stringBuffer.append("================================");
        stringBuffer.append("\n\n\n\n");

        Log.d("printer", stringBuffer.toString());

        try {
            PrinterService.getService().printString(stringBuffer.toString(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartTime:
                onbtnStartTime();
                break;
            case R.id.btnEndTime:
                onbtnEndTime();
                break;
            case R.id.btnPrint:
                onbtnPrint();
                break;
        }
    }

    public SaleStatisticFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sale_statistic, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saleOrderManager = ((MPosApplication) getActivity().getApplication()).getDataHelper().getSaleOrderManager();

        btnStartTime = (LinearLayout) view.findViewById(R.id.btnStartTime);
        btnStartTime.setOnClickListener(this);

        btnEndTime = (LinearLayout) view.findViewById(R.id.btnEndTime);
        btnEndTime.setOnClickListener(this);

        textStartTime = (TextView) view.findViewById(R.id.textStartTime);
        textEndTime = (TextView) view.findViewById(R.id.textEndTime);

        btnPrint = (LinearLayout) view.findViewById(R.id.btnPrint);
        btnPrint.setOnClickListener(this);

        dateEnd = new Date();
        dateStart = new Date();
        //dateStart.setDate(1);
        dateStart.setHours(0);
        dateStart.setMinutes(0);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        textEndTime.setText(simpleDateFormat.format(dateEnd));
        textStartTime.setText(simpleDateFormat.format(dateStart));

        textTotalSale = (TextView) view.findViewById(R.id.textTotalSale);
        textCash = (TextView) view.findViewById(R.id.textCash);
        textBankcard = (TextView) view.findViewById(R.id.textBankcard);
        textSaleOrderCount = (TextView) view.findViewById(R.id.textSaleOrderCount);

        displayStatistic();
    }

    private void displayStatistic() {
        List<SaleOrder> saleOrderList = saleOrderManager.queryBuilder()
                .where(SaleOrderDao.Properties.PayedType.gt(0), SaleOrderDao.Properties.CreateTime.between(dateStart.getTime(), dateEnd.getTime()),SaleOrderDao.Properties.PayedTime.gt(0))
                .list();

        textSaleOrderCount.setText(String.valueOf(saleOrderList.size()));
        BigDecimal totalSale =Arith.newZeroBigDecimal();
        BigDecimal cashSale = Arith.newZeroBigDecimal();
        BigDecimal bankcardSale = Arith.newZeroBigDecimal();

        for (SaleOrder saleOrder : saleOrderList) {
            if(saleOrder.getPayedTime()>0) {
            totalSale = totalSale.add(saleOrder.getShihouPrice());
                if (saleOrder.getPayedType() == 1) {
                    cashSale = cashSale.add(saleOrder.getShihouPrice());// saleOrder.getPrice();
                } else if (saleOrder.getPayedType() == 2) {
                    //  bankcardSale += saleOrder.getPrice();
                    bankcardSale = bankcardSale.add(saleOrder.getShihouPrice());
                }
            }
        }

        textCash.setText("￥" + (new DecimalFormat("0.00")).format(cashSale));
        textBankcard.setText("￥" + (new DecimalFormat("0.00")).format(bankcardSale));
        textTotalSale.setText("￥" + (new DecimalFormat("0.00")).format(totalSale));
    }
}
