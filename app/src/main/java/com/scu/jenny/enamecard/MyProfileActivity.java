package com.scu.jenny.enamecard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.Facebook;

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

        myListView = (ListView) findViewById(R.id.list_view);
        final List<Connections> connectionList = new ArrayList<>();

        connectionList.add(new Connections("icon_qora.png", "icon_add.png", null));
        connectionList.add(new Connections("icon_twitter.png", "icon_add.png", null));
        if (isLoggedIn()){
            connectionList.add(new Connections("icon_facebook.png", "connected.jpg", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFblogin();
                }
            }));
        } else {
            connectionList.add(new Connections("icon_facebook.png", "icon_add.png", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFblogin();
                }
            }));
        }

        connectionList.add(new Connections("icon_linkedin.png", "icon_add.png", null));

        myListView.setAdapter(new CustomAdapter(this, R.layout.customized_row, connectionList));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    private void onFblogin() {
        fbCallbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

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
                                            System.out.println("@@@@@@@@@@@@@@@@@@ERROR");
                                        } else {
                                            System.out.println("!!!!!!!!!!!!!!!!!Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);
                                                String str_id = json.getString("id");
                                                String imageURL = "https://graph.facebook.com/" + str_id + "/picture?type=large";
                                                // Store ID to DB
                                                DBHelper.init(getApplicationContext());
                                                DBHelper db = DBHelper.getInstance();
                                                Facebook fb = new Facebook(str_id, imageURL);
                                                db.createFBRecord(fb);

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