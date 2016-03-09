package com.scu.jenny.enamecard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyProfileActivity extends AppCompatActivity {
    private ListView myListView;
    public static CallbackManager fbCallbackmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        myListView = (ListView)findViewById(R.id.list_view);
        final List<Connections> connectionList = new ArrayList<>();
        connectionList.add(new Connections("icon_qora.png", "icon_add.png", null));
        connectionList.add(new Connections("icon_twitter.png", "icon_add.png", null));
        connectionList.add(new Connections("icon_facebook.png", "icon_add.png", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFblogin();
            }
        }));
        connectionList.add(new Connections("icon_linkedin.png", "icon_add.png", null));

        myListView.setAdapter(new CustomAdapter(this, R.layout.customized_row, connectionList));
    }

    private boolean isFBLinked() {

    }

    private void onFblogin() {
        fbCallbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

        LoginManager.getInstance().registerCallback(fbCallbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Success");
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                                System.out.println("str_firstname" + str_firstname);
                                                System.out.println("str_lastname" + str_lastname);

                                                // Store ID somewhere
                                                
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        Log.d("Cancel", "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("Error", error.toString());
                    }
                });
    }
}