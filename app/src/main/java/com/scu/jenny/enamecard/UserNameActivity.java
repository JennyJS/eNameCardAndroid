package com.scu.jenny.enamecard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.KVStore;
import com.scu.jenny.enamecard.storage.User;

import org.json.JSONException;
import org.json.JSONObject;

public class UserNameActivity extends AppCompatActivity {
    EditText editTextFirst;
    EditText editTextLast;
    Button enterUserNameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);
        editTextFirst = (EditText)findViewById(R.id.edit_firtName);
        editTextLast = (EditText)findViewById(R.id.edit_lastName);
        enterUserNameBtn = (Button)findViewById(R.id.btn_user_name);
        enterUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserNameToServer();

            }
        });

    }

    private void saveUserNameToServer(){
        final JSONObject jsonObjectject = new JSONObject();
        try {
            jsonObjectject.put("firstName",editTextFirst.getText());
            jsonObjectject.put("lastName", editTextLast.getText());
            new NetworkAsyncTask(this, "Updating user info..", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    System.out.println("!!!!!!!!!!!" + jsonRespose);

                    try{
                        JSONObject object = new JSONObject(jsonRespose);
                        if (object.has("firstName")){
                            // TODO write to DB
                            DBHelper dbHelper = DBHelper.getInstance();
                            User user = new User(object.getString("firtName"), object.getString("lastName"), object.getString("phoneNumber"));
                            long userPK = DBHelper.getInstance().createUserRecord(user);
                            KVStore.getInstance().set("userPK", userPK);
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Error talking to server", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute("PUT", "/user", jsonObjectject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
