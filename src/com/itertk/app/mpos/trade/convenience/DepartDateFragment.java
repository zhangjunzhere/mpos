package com.itertk.app.mpos.trade.convenience;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.itertk.app.mpos.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepartDateFragment extends Fragment {
    Button btnBack;
    ImageButton btnPre;
    ImageButton btnNext;
    GridView gridDate;
    TextView textMonth;

    DayAdapter dayAdapter;
    Calendar chooseCalendar;
    Calendar todayCalendar;

    TextView textTitle;
    TextView textDisplay;
    String title;

    Day departDay;


    public void setTitle(String title) {
        this.title = title;
    }


    public void setTextView(TextView textDisplay) {
        this.textDisplay = textDisplay;
    }


    public DepartDateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_depart_date, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textTitle = (TextView) view.findViewById(R.id.textTitle);
        textTitle.setText(title);

        chooseCalendar = Calendar.getInstance();
        chooseCalendar.set(Calendar.DATE, 1);
        todayCalendar = Calendar.getInstance();

        textMonth = (TextView) view.findViewById(R.id.textMonth);
        textMonth.setText(chooseCalendar.get(Calendar.YEAR) + "年" + (chooseCalendar.get(Calendar.MONTH) + 1) + "月");

        btnBack = (Button) view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        dayAdapter = new DayAdapter();
        gridDate = (GridView) view.findViewById(R.id.gridDate);
        gridDate.setAdapter(dayAdapter);
        gridDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("onItemClick", "pos=" + position);
                //dayAdapter.setDaySelected(dayAdapter.getItem(position));
                departDay = dayAdapter.getItem(position);

                dayAdapter.notifyDataSetChanged();

                textDisplay.setText(departDay.toString());
//                view.setBackgroundResource(R.color.blue_cc);
//                TextView textView = (TextView) view.findViewById(R.id.textDay);
//                textView.setText(""+dayAdapter.getItem(position).date);
//                textView.setTextColor(getResources().getColor(R.color.solid_white));
//                SpannableString spanString = new SpannableString("\n出发");
//                spanString.setSpan(new RelativeSizeSpan(0.5f), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                textView.append(spanString);
//
//                //departCalendar = chooseCalendar
//
//                textDisplay.setText(textMonth.getText().toString() + dayAdapter.getItem(position) + "日");
            }
        });


        btnPre = (ImageButton) view.findViewById(R.id.btnPre);
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooseCalendar.get(Calendar.YEAR) <= todayCalendar.get(Calendar.YEAR)
                        && chooseCalendar.get(Calendar.MONTH) <= todayCalendar.get(Calendar.MONTH)) {
                    btnPre.setImageResource(R.drawable.pre_2);
                    return;
                }

                chooseCalendar.add(Calendar.MONTH, -1);
                textMonth.setText(chooseCalendar.get(Calendar.YEAR) + "年" + (chooseCalendar.get(Calendar.MONTH) + 1) + "月");
                dayAdapter.notifyDataSetChanged();

                if (chooseCalendar.get(Calendar.YEAR) <= todayCalendar.get(Calendar.YEAR)
                        && chooseCalendar.get(Calendar.MONTH) <= todayCalendar.get(Calendar.MONTH)) {
                    btnPre.setImageResource(R.drawable.pre_2);
                }
            }
        });

        btnNext = (ImageButton) view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCalendar.add(Calendar.MONTH, 1);
                textMonth.setText(chooseCalendar.get(Calendar.YEAR) + "年" + (chooseCalendar.get(Calendar.MONTH) + 1) + "月");
                dayAdapter.notifyDataSetChanged();

                btnPre.setImageResource(R.drawable.pre);
            }
        });

        dayAdapter.notifyDataSetChanged();

    }

    class Day {
        public Day(int year, int month, int date, int position) {
            this.year = year;
            this.month = month;
            this.date = date;
            this.position = position;
        }

        boolean equals(Day day) {
            if (day == null) return false;
            if (year == day.year && month == day.month && date == day.date)
                return true;
            return false;
        }

        public int compareTo(Calendar calendar) {
            if (year > calendar.get(Calendar.YEAR)) {
                return 1;
            } else if (year < calendar.get(Calendar.YEAR)) {
                return -1;
            } else {
                if (month > calendar.get(Calendar.MONTH)) {
                    return 1;
                } else if (month < calendar.get(Calendar.MONTH)) {
                    return -1;
                } else {
                    if (date > calendar.get(Calendar.DATE)) return 1;
                    else if (date < calendar.get(Calendar.DATE)) return -1;
                    else return 0;
                }
            }
        }

        public String toString() {
            String str = String.format("%d-%02d-%02d", year, month + 1, date);
            return str;
        }

        public int year;
        public int month;
        public int date;
        public int dayOfWeek;
        public int position;
    }

    class DayAdapter extends BaseAdapter {
        ArrayList<Day> dayList;


        DayAdapter() {
            dayList = new ArrayList<Day>();
        }


        @Override
        public boolean isEnabled(int position) {
            Day day = dayList.get(position);
            if (day != null && (day.compareTo(todayCalendar) >= 0))
                return true;
            return false;
            //return super.isEnabled(position);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            dayList.clear();

            int maxDays = chooseCalendar.getActualMaximum(Calendar.DATE);
            int dayOfWeek = chooseCalendar.get(Calendar.DAY_OF_WEEK);
            int year = chooseCalendar.get(Calendar.YEAR);
            int month = chooseCalendar.get(Calendar.MONTH);
            int i = 0;
            for (; i < dayOfWeek - 1; i++) {
                dayList.add(null);
            }


            for (int j = 1; j <= maxDays; j++) {
                dayList.add(new Day(year, month, j, i + j));
            }


        }

        @Override
        public int getCount() {
            return dayList.size();
        }

        @Override
        public Day getItem(int position) {
            return dayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            Day day = getItem(position);

            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                view = inflater.inflate(R.layout.day_grid_item, null);

            }
            TextView textView = (TextView) view.findViewById(R.id.textDay);

            if (day == null) {

                view.setBackgroundResource(android.R.color.transparent);
                textView.setText("");
            } else {
                view.setBackgroundResource(R.drawable.image_border);
                textView.setText("" + day.date);

                int compare = day.compareTo(todayCalendar);

                if (compare > 0) {
                    textView.setTextColor(getResources().getColor(R.color.text_normal_color));

                } else if (compare < 0) {
                    textView.setTextColor(getResources().getColor(R.color.hint_color));

                } else {
                    textView.setTextColor(getResources().getColor(R.color.orange));
                    SpannableString spanString = new SpannableString("\n今天");
                    spanString.setSpan(new RelativeSizeSpan(0.5f), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    textView.append(spanString);

                }

                if (day.equals(departDay)) {
                    view.setBackgroundResource(R.color.blue_cc);
                    textView.setText("" + dayAdapter.getItem(position).date);
                    textView.setTextColor(getResources().getColor(R.color.solid_white));
                    SpannableString spanString = new SpannableString("\n" + title.substring(0, 2));
                    spanString.setSpan(new RelativeSizeSpan(0.5f), 0, 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    textView.append(spanString);
                }
            }


            return view;
        }


    }
}
