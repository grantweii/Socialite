package com.parse.starter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    ImageButton photoButton;
    Button privateButton;
    Button publicButton;
    EditText titleEditText;
    EditText locationEditText;
    TextView dateTextView;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener startTimeSetListener;
    TimePickerDialog.OnTimeSetListener endTimeSetListener;

    TextView startTimeTextView;
    TextView endTimeTextView;
    EditText descriptionEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dateTextView = findViewById(R.id.dateTextView);
        createDatePickerDialog();
        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        createTimePickerDialog();

        photoButton = findViewById(R.id.photoButton);
        privateButton = findViewById(R.id.privateButton);
        publicButton = findViewById(R.id.publicButton);
        titleEditText = findViewById(R.id.titleEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);

    }

    public void createDatePickerDialog() {
        final Calendar c = Calendar.getInstance();

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(CreateEventActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //make background transparent
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(year, month, dayOfMonth);
//                int intDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                String stringDayOfWeek = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                String stringMonth = c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

                String date = stringDayOfWeek + " " + dayOfMonth + " " + stringMonth;
                dateTextView.setText(date);
            }
        };
    }

    public void createTimePickerDialog() {
        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(CreateEventActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        startTimeSetListener, hour, minute, false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //make background transparent
                dialog.show();
            }
        });

        startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourString = "";
                String minuteString = "";
                String AM_PM = "";

                if(hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }

                if (hourOfDay > 12) { //change from 24 hour format to 12 hour first
                    hourOfDay -= 12;
                }

                if (hourOfDay == 0) { //for 12am (hourOfday = 0)
                    hourString = "12";
                } else if (hourOfDay < 10) { //now add 0 if less than 10
                    hourString = "0" + hourOfDay;
                } else { //10 or 11
                    hourString = Integer.toString(hourOfDay);
                }

                if (minute < 10) {
                    minuteString = "0" + minute;
                } else {
                    minuteString = Integer.toString(minute);
                }

                String time = hourString + ":" + minuteString + " " + AM_PM;
                startTimeTextView.setText(time);
            }
        };

        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(CreateEventActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        endTimeSetListener, hour, minute, false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //make background transparent
                dialog.show();
            }
        });

        endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourString = "";
                String minuteString = "";
                String AM_PM = "";

                if(hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }

                if (hourOfDay > 12) { //change from 24 hour format to 12 hour first
                    hourOfDay -= 12;
                }

                if (hourOfDay == 0) { //for 12am (hourOfday = 0)
                    hourString = "12";
                } else if (hourOfDay < 10) { //now add 0 if less than 10
                    hourString = "0" + hourOfDay;
                } else { //10 or 11
                    hourString = Integer.toString(hourOfDay);
                }

                if (minute < 10) {
                    minuteString = "0" + minute;
                } else {
                    minuteString = Integer.toString(minute);
                }

                String time = hourString + ":" + minuteString + " " + AM_PM;
                endTimeTextView.setText(time);
            }
        };
    }

}
