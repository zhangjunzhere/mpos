package com.itertk.app.mpos.trade.convenience;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itertk.app.mpos.MyActionbar;
import com.itertk.app.mpos.R;

public class AirTicketOrderActivity extends Activity {
    LinearLayout layoutBaoxiao;
    LinearLayout btnBaoxiao;
    ImageView imgDivide;
    EditText textAddress;
    ImageButton btnAddPassenger;
    ImageButton btnAddContractor;
    TextView textPassengerDescription;
    LinearLayout gridPassenger;
    TextView textContractorDescription;
    LinearLayout gridContractor;


    TextView textArrow;

    TextView textDepartDate;
    TextView textCityLabel;
    TextView textDepartTime;
    TextView textFromCity;
    TextView textArrivalTime;
    TextView textToCity;
    TextView textLineInfo;
    TextView textTicketPrice;
    TextView textAirFee;
    TextView textTotalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_ticket_order);
        MyActionbar.setNormalActionBarLayout(this);

        Intent intent = getIntent();


        textDepartDate = (TextView) findViewById(R.id.textDepartDate);
        textCityLabel = (TextView) findViewById(R.id.textCityLabel);
        textDepartTime = (TextView) findViewById(R.id.textDepartTime);
        textFromCity = (TextView) findViewById(R.id.textFromCity);
        textArrivalTime = (TextView) findViewById(R.id.textArrivalTime);
        textToCity = (TextView) findViewById(R.id.textToCity);
        textLineInfo = (TextView) findViewById(R.id.textLineInfo);
        textTicketPrice = (TextView) findViewById(R.id.textTicketPrice);
        textAirFee = (TextView) findViewById(R.id.textAirFee);
        textTotalPrice = (TextView) findViewById(R.id.textTotalPrice);


        layoutBaoxiao = (LinearLayout) findViewById(R.id.layoutBaoxiao);
        btnBaoxiao = (LinearLayout) findViewById(R.id.btnBaoxiao);

        imgDivide = (ImageView) findViewById(R.id.imgDivide);
        textAddress = (EditText) findViewById(R.id.textAddress);

        textPassengerDescription = (TextView) findViewById(R.id.textPassengerDescription);
        gridPassenger = (LinearLayout) findViewById(R.id.gridPassenger);

        textContractorDescription = (TextView) findViewById(R.id.textContractorDescription);
        gridContractor = (LinearLayout) findViewById(R.id.gridContractor);

        textArrow = (TextView) findViewById(R.id.textArrow);


        btnBaoxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("btnBaoxiao", v.toString());
                if (imgDivide.getVisibility() == View.GONE) {

                    Animation animation = AnimationUtils.loadAnimation(AirTicketOrderActivity.this, R.anim.text_rotate_down);
                    animation.setFillAfter(true);
                    textArrow.startAnimation(animation);

                    ViewGroup.LayoutParams layoutParams = layoutBaoxiao.getLayoutParams();
                    layoutParams.height *= 2;
                    layoutBaoxiao.setLayoutParams(layoutParams);
                    imgDivide.setVisibility(View.VISIBLE);
                    textAddress.setVisibility(View.VISIBLE);


                } else {
                    Animation animation = AnimationUtils.loadAnimation(AirTicketOrderActivity.this, R.anim.text_rotate_right);
                    animation.setFillAfter(true);
                    textArrow.startAnimation(animation);

                    ViewGroup.LayoutParams layoutParams = layoutBaoxiao.getLayoutParams();
                    layoutParams.height /= 2;
                    layoutBaoxiao.setLayoutParams(layoutParams);
                    imgDivide.setVisibility(View.GONE);
                    textAddress.setVisibility(View.GONE);

                }
            }
        });

        btnAddPassenger = (ImageButton) findViewById(R.id.btnAddPassenger);
        btnAddPassenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("btnAddPassenger", v.toString());
                AddPassengerDialog addPassengerDialog = new AddPassengerDialog(AirTicketOrderActivity.this, R.style.MyDialog);
                addPassengerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        final Passenger passenger = ((AddPassengerDialog) dialog).getPassenger();
                        if (passenger != null) {
                            textPassengerDescription.setVisibility(View.GONE);

                            final TextView textView = new TextView(getApplicationContext());
                            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = 10;
                            textView.setLayoutParams(lp);


                            textView.setClickable(true);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ModPassengerDialog modPassengerDialog = new ModPassengerDialog(AirTicketOrderActivity.this, R.style.MyDialog);
                                    modPassengerDialog.setPassenger(passenger);
                                    modPassengerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            Passenger passengerMod = ((ModPassengerDialog) dialog).getPassenger();
                                            if (passengerMod != null) {
                                                textView.setText(passengerMod.fullName);
                                                textView.setLayoutParams(lp);
                                            } else {
                                                gridPassenger.removeView(textView);
                                                if (gridPassenger.getChildCount() == 1)
                                                    textPassengerDescription.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });


                                    modPassengerDialog.show();
                                }
                            });
                            textView.setSingleLine();
                            textView.setText(passenger.fullName);
                            textView.setTextSize(30);
                            textView.setTextColor(getResources().getColor(R.color.solid_white));
                            //textView.setBackgroundColor(getResources().getColor(R.color.blue_cc));
                            textView.setBackgroundResource(R.drawable.passenger_border);
                            gridPassenger.addView(textView);
                        } else {

                        }
                    }
                });
                addPassengerDialog.show();
            }
        });

        btnAddContractor = (ImageButton) findViewById(R.id.btnAddContractor);
        btnAddContractor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AddContractorDialog addContractorDialog = new AddContractorDialog(AirTicketOrderActivity.this, R.style.MyDialog);
                addContractorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        final Contractor contractor = addContractorDialog.getContractor();
                        if (contractor != null) {
                            textContractorDescription.setVisibility(View.GONE);

                            final TextView textView = new TextView(getApplicationContext());
                            final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.leftMargin = 10;
                            textView.setLayoutParams(lp);

                            textView.setClickable(true);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ModContractorDialog modContractorDialog = new ModContractorDialog(AirTicketOrderActivity.this, R.style.MyDialog);

                                    modContractorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            Contractor contractorMod = ((ModContractorDialog) dialog).getContractor();
                                            if (contractorMod != null) {
                                                textView.setText(contractorMod.fullName);
                                                textView.setLayoutParams(lp);
                                            } else {
                                                gridContractor.removeView(textView);
                                                if (gridContractor.getChildCount() == 1)
                                                    textContractorDescription.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });


                                    modContractorDialog.show();
                                    modContractorDialog.setContractor(contractor);
                                }
                            });
                            textView.setSingleLine();
                            textView.setText(contractor.fullName);
                            textView.setTextSize(30);
                            textView.setTextColor(getResources().getColor(R.color.solid_white));
                            //textView.setBackgroundColor(getResources().getColor(R.color.blue_cc));
                            textView.setBackgroundResource(R.drawable.passenger_border);
                            gridContractor.addView(textView);


                        }
                    }
                });

                addContractorDialog.show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.air_ticket_order, menu);
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


}
