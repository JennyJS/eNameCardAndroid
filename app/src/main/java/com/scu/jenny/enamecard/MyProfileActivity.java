package com.scu.jenny.enamecard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
//import com.scu.jenny.enamecard.thirdparty.TwitterActivity;
import com.scu.jenny.enamecard.storage.KVStore;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MyProfileActivity extends AppCompatActivity {
    private ListView myListView;
    public static CallbackManager fbCallbackmanager;
    private static TwitterAuthClient twitterAuthClient;
    private LoginType loginType;
    private ImageView logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        logoutBtn = (ImageView) findViewById(R.id.logoutBtn);
        myListView = (ListView) findViewById(R.id.list_view);
        final List<Connections> connectionList = new ArrayList<>();

        connectionList.add(new Connections("icon_qora.png", "icon_add.png", null));

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                KVStore.getInstance().set("secret", null);
                Intent intent = new Intent(MyProfileActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });

        // Twitter
//        if(isTwitterLoggedIn()){
//            connectionList.add(new Connections("icon_twitter.png", "connected.jpg", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    twitterLogin();
//                }
//            }));
//        } else {
//            connectionList.add(new Connections("icon_twitter.png", "icon_add.png", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    twitterLogin();
//                }
//            }));
//
//        }

        connectionList.add(new Connections("icon_twitter.png", "icon_add.png", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterLogin();
            }
        }));



        // Facebook
        if (isFBLoggedIn()) {
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
        if (this.loginType != null) {
            switch (this.loginType) {
                case FACEBOOK:
                    fbCallbackmanager.onActivityResult(requestCode, resultCode, data);
                    break;
                case TWITTER:
                    twitterAuthClient.onActivityResult(requestCode, resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }

    public boolean isFBLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
//
//    private boolean isTwitterLoggedIn() {
//        TwitterSession session = Twitter.getSessionManager().getActiveSession();
//        TwitterAuthToken authToken = session.getAuthToken();
//        String token = authToken.token;
//        String secret = authToken.secret;
//        return secret != null;
//    }

    public enum LoginType {
        FACEBOOK,
        TWITTER,
        LINKEDIN
    }

    private void twitterLogin() {
        this.loginType = LoginType.TWITTER;
        final TwitterAuthConfig authConfig = new TwitterAuthConfig("jXx0mVmLepyR6M7OC8fDmyePL", "4br7HIvYi3oQUCjXsHBswLtIyPfUlTtLf3muP4jszeNXhAy4Gr");
        Fabric.with(this, new Twitter(authConfig));
        twitterAuthClient = new TwitterAuthClient();
        twitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                Log.i("Twitter", "success");
                System.out.println("@@@@@@@@@@@ TwitterID: " + twitterSessionResult.data.getUserId() + twitterSessionResult.data.getUserName());

            }

            @Override
            public void failure(TwitterException e) {
                Log.e("Twitter", "failed");
            }
        });

//        Twitter.logIn(MyProfileActivity.this, );
    }

    private void onFblogin() {
        this.loginType = LoginType.FACEBOOK;
        fbCallbackmanager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(fbCallbackmanager, new FBCallBack());
    }


    class FBCallBack implements FacebookCallback<LoginResult> {
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
    }
}
