package com.parse.starter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.starter.R;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    EditText emailEditText;
    EditText passwordEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        ImageView logoImageView = findViewById(R.id.logoImageView);
        RelativeLayout backgroundLayout = findViewById(R.id.backgroundLayout);
        logoImageView.setOnClickListener(this); //setup for: if click onto logo, drop the keyboard
        backgroundLayout.setOnClickListener(this); // "    "

    }

    public void signUpClicked(View view) {
        if (firstNameEditText.getText().toString().matches("")) { //if first name is empty
            Toast.makeText(this, "Name required!", Toast.LENGTH_SHORT).show();
        } else if (emailEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "Email and Password required!", Toast.LENGTH_SHORT).show();
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(emailEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());
            if (lastNameEditText.getText().toString().matches("")) { //if last name is empty THATS FINE
                user.put("name", firstNameEditText.getText().toString());
            } else if (!lastNameEditText.getText().toString().matches("") && !firstNameEditText.getText().toString().matches("")){
                //this else if is not really necessary, the first name was checked above
                user.put("name", firstNameEditText.getText().toString() + " " + lastNameEditText.getText().toString());
            }
            user.put("displayPicture", null);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        showExplorePage();
                    } else {
                        Toast.makeText(SignUpActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    public void showExplorePage() {
        Intent intent = new Intent(getApplicationContext(), ExploreActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logoImageView || view.getId() == R.id.backgroundLayout) {
            if (this.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpClicked(view);
        }
        return false;
    }
}
