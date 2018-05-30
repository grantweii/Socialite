package com.parse.starter;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    FrameLayout backgroundPhotoLayout;
    ImageButton photoButton;
    Button privateButton;
    Button publicButton;
    EditText titleEditText;
    EditText locationEditText;
    TextView dateTextView;
    DatePickerDialog.OnDateSetListener dateSetListener;
    TimePickerDialog.OnTimeSetListener startTimeSetListener;
    TimePickerDialog.OnTimeSetListener endTimeSetListener;

    boolean privateClicked = false;
    boolean publicClicked = false;
    boolean eventPhoto = false;

    TextView startTimeTextView;
    TextView endTimeTextView;
    EditText descriptionEditText;

    Drawable ogButtonBackground;

    static final int EXTERNAL_STORAGE_PERMISSION_REQUEST = 102;
    static final int EXTERNAL_STORAGE_REQUEST_CODE = 202;

    ParseObject event;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        setTitle("Host Event");

        backgroundPhotoLayout = findViewById(R.id.backgroundPhotoLayout);
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

        ogButtonBackground = privateButton.getBackground();

        event = null;

    }

    public void parseEventInfo() {
        int successes = 0;

        if (!titleEditText.getText().toString().matches("")) {
            successes++;
        } else {
            Toast.makeText(this, "Event title is required!", Toast.LENGTH_SHORT).show();
        }

        if (!locationEditText.getText().toString().matches("")) {
            successes++;
        } else {
            Toast.makeText(this, "Event location is required!", Toast.LENGTH_SHORT).show();
        }

        if (!dateTextView.getText().toString().matches("Date")) {
            successes++;
        } else {
            Toast.makeText(this, "Event date is required!", Toast.LENGTH_SHORT).show();
        }

        if (!startTimeTextView.getText().toString().matches("Start Time")) {
            successes++;
        } else {
            Toast.makeText(this, "Event start time is required!", Toast.LENGTH_SHORT).show();
        }

        if (!endTimeTextView.getText().toString().matches("End Time")) {
            successes++;
        } else {
            Toast.makeText(this, "Event end time is required!", Toast.LENGTH_SHORT).show();
        }

        if (eventPhoto) {
            putEventPhoto();
        }

        if (successes >= 5) {
            event = new ParseObject("Event");
            event.put("email", ParseUser.getCurrentUser().getEmail());
            event.put("title", titleEditText.getText().toString());
            event.put("location", locationEditText.getText().toString());
            event.put("date", dateTextView.getText().toString());
            event.put("startTime", startTimeTextView.getText().toString());
            event.put("endTime", endTimeTextView.getText().toString());
            event.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(CreateEventActivity.this,"Event created!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CreateEventActivity.this,"Failed to create event!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.i("successes", Integer.toString(successes));
        }

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

    public void hostClicked(View view) {
        parseEventInfo();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void privateButtonClicked(View view) {
        //check if public has been clicked and change to false if it has
        if (publicClicked) {
            publicClicked = false;
            publicButton.setBackground(ogButtonBackground);
        }
        privateClicked = true;
        privateButton.setBackground(getResources().getDrawable(R.drawable.button_color));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void publicButtonClicked(View view) {
        if (privateClicked) {
            privateClicked = false;
            privateButton.setBackground(ogButtonBackground);
        }
        publicClicked = true;
        publicButton.setBackground(getResources().getDrawable(R.drawable.button_color));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void photoButtonClicked(View view) {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST);
        } else {
            selectPhoto();
        }
    }

    public void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, EXTERNAL_STORAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedPhoto = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedPhoto);

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )); //sets imageview layout programmatically

                imageView.setImageBitmap(bitmap);
                backgroundPhotoLayout.addView(imageView);
                eventPhoto = true;
//                Log.i("Image selected", "Good work");
//
//                //upload file onto parse server
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//                ParseFile file = new ParseFile("eventPhoto.png", byteArray);
//                ParseObject temp = new ParseObject("temp");
//                temp.put("eventPhoto", file);
////                event.put("eventPhoto", file);
////                event.saveInBackground(new SaveCallback() {
//                temp.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if (e == null) {
//                            Toast.makeText(CreateEventActivity.this, "Photo has been uploaded!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(CreateEventActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(CreateEventActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectPhoto();
                } else {
                    Toast.makeText(CreateEventActivity.this, "You must accept external storage permissions to upload a photo!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void putEventPhoto() {
        //upload file onto parse server
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("eventPhoto.png", byteArray);
        event.put("eventPhoto", file);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("event photo", "uploaded onto parse server");
//                    Toast.makeText(CreateEventActivity.this, "Photo has been uploaded!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("event photo", "error uploading onto parse server");
//                    Toast.makeText(CreateEventActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
