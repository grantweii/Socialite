package com.parse.starter;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    LinearLayout parentLinearLayout;
    ImageView profilePicImageView;
    LinearLayout nameLinearLayout;
    TextView nameTextView;
    TextView isLookingForText;
    LinearLayout hobbiesScrollLayout;
    LinearLayout interestedInScrollLayout;
    LinearLayout wentToScrollLayout;
    Button saveButton;


    static final int EXTERNAL_STORAGE_PERMISSION_REQUEST = 102;
    static final int EXTERNAL_STORAGE_REQUEST_CODE = 202;

    Bitmap bitmap;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitle("Profile");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 switch (item.getItemId()) {
                     case R.id.explore:
                         Intent intent = new Intent(getApplicationContext(), ExploreActivity.class);
                         startActivity(intent);
                         return true;
                     //messages and notification pages not implemented yet
                     case R.id.messages:
                         //                intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                         //                startActivity(intent);
                         return true;
                     case R.id.notifications:
                         //                intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                         //                startActivity(intent);
                         return true;
                     case R.id.profile:
//                         intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                         startActivity(intent);
                         return true;
                 }

                 return false;
             }
         });

        parentLinearLayout = findViewById(R.id.parentLinearLayout);
        profilePicImageView = findViewById(R.id.profilePicImageView);
        nameLinearLayout = findViewById(R.id.nameLinearLayout);
        nameTextView = findViewById(R.id.nameTextView);
        isLookingForText = findViewById(R.id.lookingForText);
        hobbiesScrollLayout = findViewById(R.id.hobbiesScrollLayout);
        interestedInScrollLayout = findViewById(R.id.interestedInScrollLayout);
        wentToScrollLayout = findViewById(R.id.wentToScrollLayout);
        saveButton = findViewById(R.id.saveButton);

//        //don't really know how this works
//        //but it gets the height of the action bar (which is different depending on the device)
//        final TypedArray styledAttributes = getApplicationContext().getTheme().obtainStyledAttributes(
//                new int[] { android.R.attr.actionBarSize });
//        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();
//
//        parentLinearLayout.setPadding(0,mActionBarSize,0,0);

        getName();
        getProfilePic();
        getIsLookingFor();
        getHobbies();
        getIsInterestedIn();
        getWentTo();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (ParseUser.getCurrentUser().getString("isLookingFor") != null) {
//            Log.i("edit text", "non null");
//            //wasnt null previously and if is different now
//            if (!ParseUser.getCurrentUser().getString("isLookingFor").equals(isLookingForEditText.getText().toString())) {
//                Log.i("edit text", "changed from non null to ANOTher non null");
//                profileChanged = true;
//                saveButton.setVisibility(View.VISIBLE);
//            }
//            //if previously was null but now it isnt
//        } else if (ParseUser.getCurrentUser().getString("isLookingFor") == null &&
//                isLookingForEditText.getText().toString() != "") {
//            Log.i("edit text", "changed from NULL to non null");
//            profileChanged = true;
//            saveButton.setVisibility(View.VISIBLE);
//        } else if (profilePicChanged){
//            profileChanged = true;
//            saveButton.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }

    private void getWentTo() {
        if (ParseUser.getCurrentUser().getList("pastEvents") != null) {
            List<String> pastEventIds = ParseUser.getCurrentUser().getList("pastEvents");
            for (final String eventId: pastEventIds) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null && objects.size() == 1) {
                            //should only be 1 object with this ID
                            for (ParseObject event: objects) {
                                //for now display event as textviews, add functionality later
                                TextView eventTextView = new TextView(ProfileActivity.this);
                                eventTextView.setText(event.getString("title"));
                                wentToScrollLayout.addView(eventTextView);
                            }
                        } else {
                            Log.i("event objectId null for", eventId);
                        }
                    }
                });
            }
        }
    }

    private void getIsInterestedIn() {
        if (ParseUser.getCurrentUser().getList("interestedEvents") != null) {
            List<String> interestedEventIds = ParseUser.getCurrentUser().getList("interestedEvents");
            for (final String eventId: interestedEventIds) {
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
                query.whereEqualTo("objectId", eventId);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null && objects.size() == 1) {
                            //should only be 1 object with this ID
                            for (ParseObject event: objects) {
                                //for now display event as textviews, add functionality later
                                TextView eventTextView = new TextView(ProfileActivity.this);
                                eventTextView.setText(event.getString("title"));
                                interestedInScrollLayout.addView(eventTextView);
                            }
                        } else {
                            Log.i("event objectId null for", eventId);
                        }
                    }
                });
            }
        }
    }

    private void getHobbies() {
        if (ParseUser.getCurrentUser().getList("hobbies") != null) {
            List<String> hobbies = ParseUser.getCurrentUser().getList("hobbies");
            //for now we will display the hobbies as simple textviews
            for (String hobby: hobbies) {
                TextView hobbyTextView = new TextView(ProfileActivity.this);
                hobbyTextView.setText(hobby);
                hobbiesScrollLayout.addView(hobbyTextView);
            }
        } else {
            Log.i("getHobbies", "is null, hence no listed hobbies");
        }
    }

    private void getIsLookingFor() {
        if (ParseUser.getCurrentUser().getString("isLookingFor") != null) {
            String statement = ParseUser.getCurrentUser().getString("isLookingFor");
            isLookingForText.setText("\"" + statement +"\"");
            Log.i("isLookingForText", "is not null");
        }
    }

    private void getProfilePic() {
        if (ParseUser.getCurrentUser().getParseFile("displayPicture") != null) {
            ParseFile dp = ParseUser.getCurrentUser().getParseFile("displayPicture");
            dp.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                        profilePicImageView.setImageBitmap(bitmap);
                    } else {
                        Log.i("getProfilePic", "could not get dp bitmap");
                    }
                }
            });
        }
        //else default pic which is already set
    }

    private void getName() {
        String name = ParseUser.getCurrentUser().getString("name");
        nameTextView.setText(name);
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    public void profilePicClicked(View view) {
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_REQUEST);
//        } else {
//            selectPhoto();
//        }
//    }
//
//    public void selectPhoto() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, EXTERNAL_STORAGE_REQUEST_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == EXTERNAL_STORAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            Uri selectedPhoto = data.getData();
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedPhoto);
//                profilePicImageButton.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(ProfileActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        switch (requestCode) {
//            case EXTERNAL_STORAGE_PERMISSION_REQUEST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    selectPhoto();
//                } else {
//                    Toast.makeText(ProfileActivity.this, "You must accept external storage permissions to upload a photo!", Toast.LENGTH_SHORT).show();
//                }
//        }
//    }
//
//    public void saveProfilePic() {
//        //upload file onto parse server
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        ParseFile file = new ParseFile("displayPicture.png", byteArray);
//        ParseUser.getCurrentUser().put("displayPicture", file);
//        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                if (e == null) {
//                    Log.i("profile pic", "uploaded onto parse server");
////                    Toast.makeText(CreateEventActivity.this, "Photo has been uploaded!", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.i("profile pic", "error uploading onto parse server");
////                    Toast.makeText(CreateEventActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    public void saveClicked(View view) {
//        if (profilePicChanged) {
//            saveProfilePic();
//        }
//        saveIsLookingFor();
//    }
//
//    public void saveIsLookingFor() {
//        if (ParseUser.getCurrentUser().getString("isLookingFor") != isLookingForEditText.getText().toString()) {
//            ParseUser.getCurrentUser().put("isLookingFor", isLookingForEditText.getText().toString());
//            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.i("is looking for", "saved");
//                    } else {
//                        Log.i("is looking for", "could not save, e = null");
//                    }
//                }
//            });
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.profile_toolbar, menu);
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editProfile:
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.hostEvent:
                intent = new Intent(getApplicationContext(), CreateEventActivity.class);
                startActivity(intent);
                return true;
            //profile is here for now
//            case R.id.profile:
//                intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivity(intent);
//                return true;
            case R.id.logout:
                ParseUser.logOut();
                intent = new Intent(getApplicationContext(), WelcomePageActivity.class); //logged out, move back to login page
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
