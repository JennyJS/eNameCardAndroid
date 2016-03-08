package com.scu.jenny.enamecard;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.KVStore;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {
    static private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KVStore.init(this);
        context = getApplicationContext();

        setContentView(R.layout.activity_welcome);
        if (KVStore.getInstance().get("secret", "").length() > 0) {
            new NetworkAsyncTask(this, "Getting user profile...", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonRespose);
                        if (jsonObject.has("firstName")){
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            intent.putExtra(MainPageActivity.userInfoKey, jsonObject.toString());
                            startActivity(intent);
                        } else if (jsonObject.has("phoneNumber")){
                            Intent intent = new Intent(getApplicationContext(), UserNameActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute("GET", "/user");
        } else {
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
        }
    }
}
