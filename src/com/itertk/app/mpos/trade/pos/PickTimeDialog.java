package com.itertk.app.mpos.trade.pos;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.itertk.app.mpos.R;
import com.itertk.app.mpos.utility.Utils;

import java.util.Calendar;
import java.util.Date;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.StrericWheelAdapter;
import kankan.wheel.widget.WheelView;

/**
 * Created by Administrator on 2014/7/15.
 * 时间选取对话框
 */
public class PickTimeDialog extends Dialog {
    static final String TAG = "PickTimeDialog";
    Date date;
    WheelView wheelYear;
    WheelView wheelMonth;
    WheelView wheelDay;
    WheelView wheelHour;
    WheelView wheelMinute;

    Button btnOK;
    Button btnCancel;
    Date tempdate;

    public PickTimeDialog(Context context, int theme, Date date) {
        super(context, theme);
        this.date = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_pick_time);

        tempdate=new Date();
        tempdate.setTime(date.getTime());

        final String[] month1=new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        final String[] month2=new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28"};
        final String[] month4=new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28","29"};
        final String[] month3=new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};

        wheelYear = (WheelView) findViewById(R.id.wheelYear);
        wheelMonth = (WheelView) findViewById(R.id.wheelMonth);
        wheelDay = (WheelView) findViewById(R.id.wheelDay);
        wheelHour = (WheelView) findViewById(R.id.wheelHour);
        wheelMinute = (WheelView) findViewById(R.id.wheelMinute);
        wheelYear.setAdapter(new StrericWheelAdapter(new String[]{"2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021"}));
        wheelYear.setCyclic(true);
        wheelYear.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                Calendar calendar=Calendar.getInstance();
                wheelYear.getAdapter().setItem(oldValue, wheel.getAdapter().getItem(oldValue).replace("年", ""));
                wheel.getAdapter().setItem(newValue, wheel.getAdapter().getItem(newValue) + "年");
                calendar.setTimeInMillis(date.getTime());
                int lastMonth=calendar.get(Calendar.MONTH);
                date.setYear(110 + newValue);
                calendar.setTimeInMillis(date.getTime());
                if(lastMonth!=calendar.get(Calendar.MONTH)) {
                    date.setMonth(lastMonth);
                    calendar.setTimeInMillis(date.getTime());
                }
                if(calendar.get(Calendar.MONTH)+1==2) {
                    if (Utils.isLeapYear(calendar.get(Calendar.YEAR))) {//闰年
                        wheelDay.setAdapter(new StrericWheelAdapter(month4));
                    } else {
                        wheelDay.setAdapter(new StrericWheelAdapter(month2));
                    }
                    wheelDay.setCurrentItem(date.getDate() - 1);
                    for (int i = 0; i < wheelDay.getAdapter().getItemsCount(); i++) {
                        wheelDay.getAdapter().setItem(i, wheelDay.getAdapter().getItem(i).replace("日", ""));
                    }
                    wheelDay.getAdapter().setItem(date.getDate() - 1, wheelDay.getAdapter().getItem(date.getDate() - 1) + "日");
                }
            }
        });
        Log.d(TAG, "date=" + date.toString()
                        + " year=" + date.getYear()
                        + " month=" + date.getMonth()
                        + " day=" + date.getDate()
                        + " hour=" + date.getHours()
                        + " minute=" + date.getMinutes()
        );
        wheelYear.setCurrentItem(date.getYear() - 110);


        wheelDay.setAdapter(new StrericWheelAdapter(month1));
        wheelMonth = (WheelView) findViewById(R.id.wheelMonth);
        wheelMonth.setAdapter(new StrericWheelAdapter(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}));
        wheelMonth.setCyclic(true);
        wheelMonth.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                wheel.getAdapter().setItem(oldValue, wheel.getAdapter().getItem(oldValue).replace("月", ""));
                wheel.getAdapter().setItem(newValue, wheel.getAdapter().getItem(newValue) + "月");
                int tempValue=newValue+1;
                if(tempValue==2){
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTimeInMillis(date.getTime());
                    if(Utils.isLeapYear(calendar.get(Calendar.YEAR))){//闰年
                        wheelDay.setAdapter(new StrericWheelAdapter(month4));
                    }else {
                        wheelDay.setAdapter(new StrericWheelAdapter(month2));
                    }
                }else if(tempValue==4||tempValue==6
                        ||tempValue==9||tempValue==11){
                    wheelDay.setAdapter(new StrericWheelAdapter(month3));
                }else{
                    wheelDay.setAdapter(new StrericWheelAdapter(month1));
                }
                wheelDay.setCurrentItem(date.getDate()-1);
                for(int i=0;i<wheelDay.getAdapter().getItemsCount();i++){
                    wheelDay.getAdapter().setItem(i, wheelDay.getAdapter().getItem(i).replace("日", ""));
                }
                wheelDay.getAdapter().setItem(date.getDate()-1, wheelDay.getAdapter().getItem(date.getDate()-1) + "日");
                date.setMonth(newValue);
            }
        });
        wheelMonth.setCurrentItem(date.getMonth());


        wheelDay.setCyclic(true);
        wheelDay.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if(wheel.getAdapter().getItem(oldValue)!=null) {
                    wheel.getAdapter().setItem(oldValue, wheel.getAdapter().getItem(oldValue).replace("日", ""));
                }
                wheel.getAdapter().setItem(newValue, wheel.getAdapter().getItem(newValue) + "日");
                date.setDate(newValue + 1);
            }
        });
        wheelDay.setCurrentItem(date.getDate() - 1);


        wheelHour.setAdapter(new StrericWheelAdapter(new String[]{
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23"}));
        wheelHour.setCyclic(true);
        wheelHour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                wheel.getAdapter().setItem(oldValue, wheel.getAdapter().getItem(oldValue).replace("时", ""));
                wheel.getAdapter().setItem(newValue, wheel.getAdapter().getItem(newValue) + "时");
                date.setHours(newValue);
            }
        });
        wheelHour.setCurrentItem(date.getHours());



        wheelMinute.setAdapter(new StrericWheelAdapter(new String[]{
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40",
                "41", "42", "43", "44", "45", "46", "47", "48", "49", "50",
                "51", "52", "53", "54", "55", "56", "57", "58", "59",
        }));
        wheelMinute.setCyclic(true);
        wheelMinute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                wheel.getAdapter().setItem(oldValue, wheel.getAdapter().getItem(oldValue).replace("分", ""));
                wheel.getAdapter().setItem(newValue, wheel.getAdapter().getItem(newValue) + "分");
                date.setMinutes(newValue);
            }
        });
        if(date.getMinutes()==0){
            wheelMinute.getAdapter().setItem(date.getMinutes(), wheelMinute.getAdapter().getItem(date.getMinutes()).replace("分", ""));
            wheelMinute.getAdapter().setItem(date.getMinutes(), wheelMinute.getAdapter().getItem(date.getMinutes()) + "分");
        }
        if(date.getHours()==0){
            wheelHour.getAdapter().setItem(date.getHours(), wheelHour.getAdapter().getItem(date.getHours()).replace("分", ""));
            wheelHour.getAdapter().setItem(date.getHours(), wheelHour.getAdapter().getItem(date.getHours()) + "分");
        }
        if(date.getYear()==110){
            wheelYear.getAdapter().setItem(date.getYear()-110, wheelYear.getAdapter().getItem(date.getYear()-110).replace("年", ""));
            wheelYear.getAdapter().setItem(date.getYear()-110, wheelYear.getAdapter().getItem(date.getYear()-110) + "年");
        }
        if(date.getMonth()==0){
            wheelMonth.getAdapter().setItem(date.getMonth(), wheelMonth.getAdapter().getItem(date.getMonth()).replace("月", ""));
            wheelMonth.getAdapter().setItem(date.getMonth(), wheelMonth.getAdapter().getItem(date.getMonth()) + "月");
        }
        if(date.getDate()==1){
            wheelDay.getAdapter().setItem(date.getDate()-1, wheelDay.getAdapter().getItem(date.getDate()-1).replace("日", ""));
            wheelDay.getAdapter().setItem(date.getDate()-1, wheelDay.getAdapter().getItem(date.getDate()-1) + "日");
        }
        wheelMinute.setCurrentItem(date.getMinutes());


        btnOK = (Button) findViewById(R.id.btnOK);
//        btnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PickTimeDialog.this.dismiss();
//            }
//        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickTimeDialog.this.dismiss();
                date.setTime(tempdate.getTime());
            }
        });
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                date.setTime(tempdate.getTime());
            }
        });

    }
}
