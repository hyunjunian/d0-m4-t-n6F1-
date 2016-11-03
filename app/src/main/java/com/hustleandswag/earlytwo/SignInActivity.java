package com.hustleandswag.earlytwo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hustleandswag.earlytwo.models.User;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "gwly2opKOO7dRTBgeInk3lnoi";
    private static final String TWITTER_SECRET = "ao6A2JzyhwGK85BfRLg9FcDVVIo68Kfdm4FtIl8MpCENE4hzbt";

    private static final String TAG = "SignInActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private TextView mSignInHelp;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUsernameField;
    private Button mSignInButton;
    private Button mSignUpButton;
    private TextView mToSignInButton;
    private DigitsAuthButton digitsButton;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        // Views
        mSignInHelp = (TextView) findViewById(R.id.sign_in_help);
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mUsernameField = (EditText) findViewById(R.id.field_username);
        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mSignUpButton = (Button) findViewById(R.id.button_sign_up);
        mToSignInButton = (TextView) findViewById(R.id.to_sign_in);

        // Click listeners
        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mToSignInButton.setOnClickListener(this);

        // Digit
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.CustomDigitsTheme);
        Fabric.with(this, new TwitterCore(authConfig), digitsBuilder.build());


        digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setText(R.string.sign_up);
        digitsButton.setBackgroundColor(getResources().getColor(R.color.button_bg));
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model
                Toast.makeText(getApplicationContext(), "Authentication successful for "
                        + phoneNumber, Toast.LENGTH_LONG).show();
                phone = phoneNumber;

                updateUI(false); //no for Sign in
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(false); //no for Sign in
        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess();
        }
    }

    private void signIn() {
        Log.d(TAG, "signIn");
        if (!validateForm(false)) { // for sign in
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            onAuthSuccess();
                        } else {
                            Toast.makeText(SignInActivity.this, "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm(true)) {// for sign up
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        if(!phoneCheck()){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                            hideProgressDialog();

                            if (task.isSuccessful()) {
                                FirebaseUser user = task.getResult().getUser();
                                writeNewUser(user.getUid(), user.getEmail(), phone);
                            } else {
                                Toast.makeText(SignInActivity.this, "Sign Up Failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SignInActivity.this, mDatabase.child("phones").child(phone).getKey(),
                    Toast.LENGTH_SHORT).show();
            hideProgressDialog();
        }
    }

    private void onAuthSuccess() {
        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();
    }

    private boolean validateForm(boolean i) {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (i && TextUtils.isEmpty(mUsernameField.getText().toString())) {
            mUsernameField.setError("Required");
            result = false;
        } else {
            mUsernameField.setError(null);
        }
        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String email, String phone) {
        String username = mUsernameField.getText().toString();
        User user = new User(username, email, phone);

        mDatabase.child("users").child(userId).setValue(user);
        mDatabase.child("phones").child(phone).setValue(user);
        onAuthSuccess();
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_sign_in) {
            signIn();
        } else if (i == R.id.button_sign_up) {
            signUp();
        } else if (i == R.id.to_sign_in) {
            updateUI(true); //for sign in
        }
    }

    private boolean phoneCheck (){
        final boolean[] i = {false};
        mDatabase.child("phones").child(phone).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        if(dataSnapshot.exists()) i[0] = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
        return i[0];
    }

    private void updateUI(boolean i){
        if(i) {
            mSignInHelp.setVisibility(View.GONE);
            mEmailField.setVisibility(View.VISIBLE);
            mPasswordField.setVisibility(View.VISIBLE);
            mUsernameField.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.VISIBLE);
            mSignUpButton.setVisibility(View.GONE);
            mToSignInButton.setVisibility(View.GONE);
            digitsButton.setVisibility(View.GONE);
        }
        else {
            if (phone != null) {
                mSignInHelp.setVisibility(View.VISIBLE);
                mSignInHelp.setText("이메일과 비밀번호를 설정해 주세요.");
                mEmailField.setVisibility(View.VISIBLE);
                mPasswordField.setVisibility(View.VISIBLE);
                mUsernameField.setVisibility(View.VISIBLE);
                mSignInButton.setVisibility(View.GONE);
                mSignUpButton.setVisibility(View.VISIBLE);
                mToSignInButton.setVisibility(View.GONE);
                digitsButton.setVisibility(View.GONE);
            } else {
                mSignInHelp.setVisibility(View.GONE);
                mEmailField.setVisibility(View.GONE);
                mPasswordField.setVisibility(View.GONE);
                mUsernameField.setVisibility(View.GONE);
                mSignInButton.setVisibility(View.GONE);
                mSignUpButton.setVisibility(View.GONE);
                mToSignInButton.setVisibility(View.VISIBLE);
                digitsButton.setVisibility(View.VISIBLE);
            }
        }
    }
}
