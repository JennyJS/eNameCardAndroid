package com.scu.jenny.enamecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class LogInActivity extends AppCompatActivity {
    Button logInBtn;
    Button getVerificationCodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInBtn = (Button) findViewById(R.id.btn_enter_logIn);
        getVerificationCodeBtn = (Button) findViewById(R.id.button);

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVerificationCode();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                startActivity(intent);
            }
        });
    }


    private void requestVerificationCode() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_number", "4087053056");
            new NetworkAsyncTask(this, "Getting SMS code...").execute("GET", "/sign_up", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}