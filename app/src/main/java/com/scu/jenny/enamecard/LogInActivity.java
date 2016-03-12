package com.scu.jenny.enamecard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.KVStore;
import com.scu.jenny.enamecard.storage.User;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {
    private static Activity thisActivity;

    Button logInBtn;
    Button getVerificationCodeBtn;
    Button backBtn;

    private static String sessionId;
    EditText phoneNumberEditText;
    EditText verificationCodeEditText;

    LinearLayout loginViewLayout;
    LinearLayout verificationCodeLayout;
    LinearLayout phoneNumberLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisActivity = this;
        // TODO purge DB delte all tables
        DBHelper dbHelper = DBHelper.getInstance();
        getApplicationContext().deleteDatabase(dbHelper.getDatabaseName());
        setContentView(R.layout.activity_log_in);

        logInBtn = (Button) findViewById(R.id.btn_enter_logIn);
        getVerificationCodeBtn = (Button) findViewById(R.id.button);
        backBtn = (Button) findViewById(R.id.back_btn);


        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        verificationCodeEditText = (EditText) findViewById(R.id.verfication_code_edit_text);

        this.loginViewLayout = (LinearLayout) findViewById(R.id.login_view_layout);
        this.verificationCodeLayout = (LinearLayout) findViewById(R.id.verification_code_layout);
        this.phoneNumberLayout = (LinearLayout) findViewById(R.id.phone_number_layout);

        moveViewDown(200);

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneNumberEditText.getText().toString().equals("123")) {
                    Intent intent = new Intent(getApplicationContext(), UserNameActivity.class);
                    startActivity(intent);
                } else {
                    requestVerificationCode();
                }

            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificationCodeEditText.getText().toString().equals("123")) {
                    // for convenience
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                } else if (sessionId == null) {
                    Toast.makeText(getApplicationContext(), "Missing session ID (You need to debug)", Toast.LENGTH_LONG).show();
                } else {
                    requestSecret();
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveViewDown(700);
            }
        });
    }

    private void requestSecret(){
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("session_id", this.sessionId);
            jsonObject.put("verification_code", verificationCodeEditText.getText());
            new NetworkAsyncTask(this, "Verifying phone number...", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    try {
                        JSONObject object = new JSONObject(jsonRespose);
                        if (object.has("secret")){
                            String secret = object.getString("secret");
                            KVStore.getInstance().set("secret", secret);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new NetworkAsyncTask(thisActivity, "Getting user profile...", new FetchUserProfileCallback()).execute("GET", "/user");
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Incorrect Verification Code", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).execute("POST", "/verification", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class FetchUserProfileCallback implements ProcessResponse {
        @Override
        public void process(String jsonRespose) {
            try {
                JSONObject object = new JSONObject(jsonRespose);

                if (object.has("firstName")) {
                    //TODO write to DB
                    User user = new User(object.getString("firstName"), object.getString("lastName"), object.getString("phoneNumber"));
                    DBHelper.getInstance().updateOrCreateUserRecord(user);

                    Intent intent = new Intent(thisActivity, MainPageActivity.class);
                    startActivity(intent);
                } else {
                    // TODO: jump to enter first name UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "User doesn't have first name", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(thisActivity, UserNameActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void requestVerificationCode() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", phoneNumberEditText.getText().length() == 0 ? "4087053056" : phoneNumberEditText.getText());

            new NetworkAsyncTask(this, "Getting SMS code...", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    try {
                        JSONObject object = new JSONObject(jsonRespose);
                        System.out.print(object.toString());
                        if (object.has("session_id")) {
                            LogInActivity.sessionId = (String)object.get("session_id");
                            moveViewUp(700);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Sever error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        System.out.print(jsonRespose);
                        e.printStackTrace();
                    }
                }
            }).execute("POST", "/sign_up", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /*******************************************/
    /****** Animation Run on Main Thread *******/
    /*******************************************/
    private static final int MOVE_DURATION = 500;

    private void moveViewDown(final float moveDownDistance) {
        final TranslateAnimation anim = new TranslateAnimation(0, 0, 0, moveDownDistance);
        anim.setDuration(MOVE_DURATION);

        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                enablePhoneNumberInput();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) loginViewLayout.getLayoutParams();
                params.bottomMargin -= moveDownDistance;
                params.topMargin += moveDownDistance;
                loginViewLayout.setLayoutParams(params);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginViewLayout.startAnimation(anim);
            }
        });
    }

    private void moveViewUp(final int moveUpDistance) {
        final TranslateAnimation anim = new TranslateAnimation(0, 0, 0, -moveUpDistance);
        anim.setDuration(MOVE_DURATION);

        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                enableVerificationInput();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) loginViewLayout.getLayoutParams();
                params.bottomMargin += moveUpDistance;
                params.topMargin -= moveUpDistance;
                loginViewLayout.setLayoutParams(params);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginViewLayout.startAnimation(anim);
            }
        });
    }

    private void enablePhoneNumberInput() {
        phoneNumberLayout.setAlpha(1.0f);
        verificationCodeLayout.setAlpha(0f);
    }

    private void enableVerificationInput() {
        phoneNumberLayout.setAlpha(0f);
        verificationCodeLayout.setAlpha(1.0f);
    }
}