package com.parse.starter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    LinearLayout parentLinearLayout;
    ImageButton profilePicImageButton;
    LinearLayout nameLinearLayout;
    TextView nameTextView;
    EditText isLookingForEditText;
    LinearLayout hobbiesScrollLayout;
    LinearLayout interestedInScrollLayout;
    LinearLayout wentToScrollLayout;
    Button saveButton;

    boolean profileChanged = false;
    boolean profilePicChanged = false;
    boolean isLookingForChanged = false;

    static final int EXTERNAL_STORAGE_PERMISSION_REQUEST = 102;
    static final int EXTERNAL_STORAGE_REQUEST_CODE = 202;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitle("Edit Profile");

        parentLinearLayout = findViewById(R.id.parentLinearLayout);
        profilePicImageButton = findViewById(R.id.profilePicImageButton);
        nameLinearLayout = findViewById(R.id.nameLinearLayout);
        nameTextView = findViewById(R.id.nameTextView);
        isLookingForEditText = (EditText) findViewById(R.id.isLookingForEditText);
        hobbiesScrollLayout = findViewById(R.id.hobbiesScrollLayout);
        interestedInScrollLayout = findViewById(R.id.interestedInScrollLayout);
        wentToScrollLayout = findViewById(R.id.wentToScrollLayout);
        saveButton = findViewById(R.id.saveButton);

        getName();
        getProfilePic();
        getIsLookingFor();
        getHobbies();
        getIsInterestedIn();
        getWentTo();
    }


    // not sure if onResume is necessary
    @Override
    protected void onResume() {
        super.onResume();
        if (ParseUser.getCurrentUser().getString("isLookingFor") != null) {
            Log.i("edit text", "non null");
            //wasnt null previously and if is different now
            if (!ParseUser.getCurrentUser().getString("isLookingFor").equals(isLookingForEditText.getText().toString())) {
                Log.i("edit text", "changed from non null to ANOTher non null");
                profileChanged = true;
                saveButton.setVisibility(View.VISIBLE);
            }
            //if previously was null but now it isnt
        } else if (ParseUser.getCurrentUser().getString("isLookingFor") == null &&
                isLookingForEditText.getText().toString() != "") {
            Log.i("edit text", "changed from NULL to non null");
            profileChanged = true;
            saveButton.setVisibility(View.VISIBLE);
        } else if (profilePicChanged){
            profileChanged = true;
            saveButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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
                                TextView eventTextView = new TextView(EditProfileActivity.this);
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
                                TextView eventTextView = new TextView(EditProfileActivity.this);
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
                TextView hobbyTextView = new TextView(EditProfileActivity.this);
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
            isLookingForEditText.setText(statement);
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
                        profilePicImageButton.setImageBitmap(bitmap);
                        profilePicChanged = true;
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

    /**
     * changes profile pic when it is clicked
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void profilePicClicked(View view) {
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
                profilePicImageButton.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(EditProfileActivity.this, "You must accept external storage permissions to upload a photo!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     * uploads profile pic onto server
     */
    public void saveProfilePic() {
        //upload file onto parse server
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        ParseFile file = new ParseFile("displayPicture.png", byteArray);
        ParseUser.getCurrentUser().put("displayPicture", file);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("profile pic", "uploaded onto parse server");
//                    Toast.makeText(CreateEventActivity.this, "Photo has been uploaded!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("profile pic", "error uploading onto parse server");
//                    Toast.makeText(CreateEventActivity.this, "There is an issue uploading the photo. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * saves profile pic and is looking for if they have been changed
     * if no changes, show toast and go back to ProfileActivity
     * @param view
     */
    public void saveClicked(View view) {
        if (profilePicChanged) {
            saveProfilePic();
        }
        saveIsLookingFor();
        if (!(isLookingForChanged || profileChanged)) {
            Toast.makeText(EditProfileActivity.this, "No changes have been made.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
    }

    /**
     * saves is looking for and changes back to ProfileActivity page
     */
    public void saveIsLookingFor() {
        if (ParseUser.getCurrentUser().getString("isLookingFor") != isLookingForEditText.getText().toString()) {
            isLookingForChanged = true;
            ParseUser.getCurrentUser().put("isLookingFor", isLookingForEditText.getText().toString());
            ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("is looking for", "saved");
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent);
                    } else {
                        Log.i("is looking for", "could not save, e = null");
                    }
                }
            });
        }
    }


}
