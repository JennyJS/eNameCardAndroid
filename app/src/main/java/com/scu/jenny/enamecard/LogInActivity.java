package com.scu.jenny.enamecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {
    Button logInBtn;
    Button getVerificationCodeBtn;
    private static String sessionId;
    TextView verficationCodeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInBtn = (Button) findViewById(R.id.btn_enter_logIn);
        getVerificationCodeBtn = (Button) findViewById(R.id.button);
        verficationCodeTextView = (TextView) findViewById(R.id.verfication_code_edit_text);

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVerificationCode();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionId == null || verficationCodeTextView.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), "You need to get verification code first", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
            }
        });
    }


    private void requestVerificationCode() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", "4087053056");

            new NetworkAsyncTask(this, "Getting SMS code...", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    try {
                        JSONObject object = new JSONObject(jsonRespose);
                        if (object.get("status").equals("success")) {
                            LogInActivity.sessionId = (String)object.get("session_id");
                        } else {
                            Toast.makeText(getApplicationContext(), "Sever error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute("GET", "/sign_up", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}