package com.scu.jenny.enamecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.KVStore;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {
    Button logInBtn;
    Button getVerificationCodeBtn;
    private static String sessionId;
    EditText phoneNumberEditText;
    EditText verificationCodeEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KVStore.init(this);

        setContentView(R.layout.activity_log_in);

        logInBtn = (Button) findViewById(R.id.btn_enter_logIn);
        getVerificationCodeBtn = (Button) findViewById(R.id.button);
        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        verificationCodeEditText = (EditText) findViewById(R.id.verfication_code_edit_text);

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVerificationCode();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionId == null || phoneNumberEditText.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "You need to get verification code first", Toast.LENGTH_LONG).show();
                    return;
                }
                requestSecret();
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
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Incorrect Verification Code", Toast.LENGTH_LONG).show();
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

    private void requestVerificationCode() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", phoneNumberEditText.getText());

            new NetworkAsyncTask(this, "Getting SMS code...", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    try {
                        JSONObject object = new JSONObject(jsonRespose);
                        System.out.print(object.toString());
                        if (object.has("session_id")) {
                            LogInActivity.sessionId = (String)object.get("session_id");
                        } else {
                            Toast.makeText(getApplicationContext(), "Sever error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute("POST", "/sign_up", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}