package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itertk.app.mpos.LoadingDialog;
import com.itertk.app.mpos.MPosApplication;
import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;
import com.itertk.app.mpos.comm.LinkeaResponseMsg;
import com.itertk.app.mpos.comm.LinkeaResponseMsgGenerator;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class TrainLineActivity extends Activity {
    private final static String TAG = "TrainLineActivity";
    TextView textTitle;
    ListView listTrainLine;
    AirlineAdapter airlineAdapter;

    ListView listCabin;
    CabinAdapter cabinAdapter;

    String fromcity;
    String tocity;
    String from;
    String to;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_line);
        MyActionbar.setNormalActionBarLayout(this);

        Intent it = getIntent();
        fromcity = it.getStringExtra("fromcity");
        tocity = it.getStringExtra("tocity");
        from = it.getStringExtra("from");
        to = it.getStringExtra("to");
        date = it.getStringExtra("date");

        Log.d(TAG, fromcity + ":" + from + " to " + tocity + ":" + to + "@" + date);

        final LoadingDialog loadingDialog = new LoadingDialog(TrainLineActivity.this, R.style.MyDialog);
        loadingDialog.show();

        ((MPosApplication) getApplication()).getMsgBuilder().buildTrainTicketQuery(to, from, date).send(new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                Log.e(TAG, throwable.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadingDialog.dismiss();
                Log.d(TAG, responseString);


                try {
                    LinkeaResponseMsg.TrainTicketQueryResponseMsg trainTicketQueryResponseMsg =
                            LinkeaResponseMsgGenerator.generateTrainTicketQueryResponseMsg(responseString);

                    Log.d(TAG, trainTicketQueryResponseMsg.toString());

                    if (trainTicketQueryResponseMsg.success) {

                    } else {
                        String erroInfo = "服务不可用";
                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, erroInfo);
                    }

//                    if (trainTicketQueryResponseMsg.isSuccess()) {
//                        SpannableString frontSpan = new SpannableString("\n（" + date + " 共有 ");
//                        frontSpan.setSpan(new RelativeSizeSpan(0.6f), 0, frontSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        textTitle.append(frontSpan);
//
//                        SpannableString middleSpan = new SpannableString("" + airTicketQueryResponseMsg.flightInfos.size());
//                        middleSpan.setSpan(new RelativeSizeSpan(0.6f), 0, middleSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        middleSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue_cc)), 0, middleSpan.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        textTitle.append(middleSpan);
//
//                        SpannableString endSpan = new SpannableString(" 个车次）");
//                        endSpan.setSpan(new RelativeSizeSpan(0.6f), 0, endSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        textTitle.append(endSpan);
//
//                        airlineAdapter.setFlightInfo(airTicketQueryResponseMsg.flightInfos);
//
//                    } else {
//                        String erroInfo = airTicketQueryResponseMsg.result_code_msg;
//                        Toast.makeText(getApplicationContext(), erroInfo, Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, erroInfo);
//                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            }
        });


        listTrainLine = (ListView) findViewById(R.id.listTrainLine);
        airlineAdapter = new AirlineAdapter();
        listTrainLine.setAdapter(airlineAdapter);

        listCabin = (ListView) findViewById(R.id.listCabin);
        cabinAdapter = new CabinAdapter();
        listCabin.setAdapter(cabinAdapter);
        listCabin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });


        listTrainLine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo flightInfo = airlineAdapter.getItem(position);
                cabinAdapter.setCabinInfoList(flightInfo.cabinInfos);
            }
        });


        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(fromcity + " —— " + tocity);

    }

    class CabinAdapter extends BaseAdapter {
        List<LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo.CabinInfo> cabinInfoList;

        public void setCabinInfoList(List<LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo.CabinInfo> cabinInfoList) {
            this.cabinInfoList = cabinInfoList;
            notifyDataSetChanged();
        }

        public CabinAdapter() {
            cabinInfoList = new ArrayList<LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo.CabinInfo>();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return cabinInfoList.size();
        }

        @Override
        public LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo.CabinInfo getItem(int position) {
            return cabinInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo.CabinInfo cabinInfo = getItem(position);

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                view = inflater.inflate(R.layout.cabin_list_item, null);
            }


            TextView textPrice = (TextView) view.findViewById(R.id.textPrice);
            TextView textCabin = (TextView) view.findViewById(R.id.textCabin);
            TextView textTicketInfo = (TextView) view.findViewById(R.id.textTicketInfo);

            textPrice.setText("￥" + cabinInfo.fare);
            double reduce = Integer.valueOf(cabinInfo.discountRate) / 10.0;
            if (reduce >= 10.0)
                textCabin.setText(cabinInfo.policyCode + " 全价");
            else
                textCabin.setText(cabinInfo.policyCode + " " + reduce + "折");
            if (cabinInfo.cabinInfo.compareTo("A") == 0) {
                textTicketInfo.setText(">9张");
            } else {
                textTicketInfo.setText(cabinInfo.cabinInfo + "张");
            }

            return view;
        }
    }

    class AirlineAdapter extends BaseAdapter {
        List<LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo> flightInfoList;

        public void setFlightInfo(List<LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo> flightInfoList) {
            this.flightInfoList = flightInfoList;
            notifyDataSetChanged();
        }


        public AirlineAdapter() {
            flightInfoList = new ArrayList<LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo>();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return flightInfoList.size();
        }

        @Override
        public LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo getItem(int position) {
            return flightInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo flightInfo = getItem(position);

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                view = inflater.inflate(R.layout.airline_list_item, null);
            }

            TextView textDepartTime = (TextView) view.findViewById(R.id.textDepartTime);
            TextView textArrivalTime = (TextView) view.findViewById(R.id.textArrivalTime);
            TextView textFromCity = (TextView) view.findViewById(R.id.textFromCity);
            TextView textToCity = (TextView) view.findViewById(R.id.textToCity);
            TextView textPrice = (TextView) view.findViewById(R.id.textPrice);
            TextView textCabin = (TextView) view.findViewById(R.id.textCabin);
            TextView textAirlineInfo = (TextView) view.findViewById(R.id.textAirlineInfo);
            TextView textTicketInfo = (TextView) view.findViewById(R.id.textTicketInfo);

            LinkeaResponseMsg.AirTicketQueryResponseMsg.FlightInfo.CabinInfo cabinInfo = flightInfo.cabinInfos.get(flightInfo.cabinInfos.size() - 1);
            textDepartTime.setText(flightInfo.departureTime.substring(11));
            textArrivalTime.setText(flightInfo.arrivalTime.substring(11));
            textFromCity.setText(fromcity);
            textToCity.setText(tocity);
            textPrice.setText("￥" + cabinInfo.fare);
            textCabin.setText(cabinInfo.policyCode + " " + Integer.valueOf(cabinInfo.discountRate) / 10.0 + "折");
            textAirlineInfo.setText(flightInfo.flightNo + "   机型 " + flightInfo.planeType);
            if (cabinInfo.cabinInfo.compareTo("A") == 0) {
                textTicketInfo.setText(">9张");
            } else {
                textTicketInfo.setText(cabinInfo.cabinInfo + "张");
            }

            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.train_line, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
