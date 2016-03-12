package com.scu.jenny.enamecard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
    private static CallbackManager fbCallbackmanager;
    private static TwitterAuthClient twitterAuthClient;
    private Context context;

    public enum LoginType {
        FACEBOOK,
        TWITTER,
        LINKEDIN
    }

    private ListView myListView;
    private LoginType loginType;
    private ImageView logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = getApplicationContext();

        logoutBtn = (ImageView) findViewById(R.id.logoutBtn);
        myListView = (ListView) findViewById(R.id.list_view);




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
        reloadListView();
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

    private void reloadListView() {
        myListView.invalidateViews();
        final List<Connections> connectionList = new ArrayList<>();
        connectionList.add(new Connections("icon_qora.png", null, null));
        connectionList.add(new Connections("icon_twitter.png", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterLogin();
            }
        }));

        // Facebook
        if (isFBLoggedIn()) {
            // get FB URL from DB
            Facebook fb = DBHelper.getInstance().getFBByUserID(KVStore.getCurrentUserPK());
            connectionList.add(new Connections("icon_facebook.png", fb == null ? null : fb.imageURL, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFbLogout();
                }
            }));
        } else {
            connectionList.add(new Connections("icon_facebook.png", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFblogin();
                }
            }));
        }

        connectionList.add(new Connections("icon_linkedin.png", null, null));

        myListView.setAdapter(new CustomAdapter(this, R.layout.customized_row, connectionList));

    }

    public boolean isFBLoggedIn() {
        return DBHelper.getInstance().getFBByUserID(KVStore.getCurrentUserPK()) != null;
    }
//
//    private boolean isTwitterLoggedIn() {
//        TwitterSession session = Twitter.getSessionManager().getActiveSession();
//        TwitterAuthToken authToken = session.getAuthToken();
//        String token = authToken.token;
//        String secret = authToken.secret;
//        return secret != null;
//    }



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



    /**********************************************************/
    /*************** FB Login / Logout CallBack ***************/
    /**********************************************************/

    private void onFblogin() {
        this.loginType = LoginType.FACEBOOK;
        fbCallbackmanager = CallbackManager.Factory.create();
        // Set permissions
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(fbCallbackmanager, new FBCallBack());
    }

    private void onFbLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);

        builder.setMessage("Log out Facebook?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //delete facebook record
                DBHelper.getInstance().deleteFBRecordByUserID(KVStore.getCurrentUserPK());
                reloadListView();
                Toast.makeText(context, "Unlinked Facebook account", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
                                Toast.makeText(context, "FBLogin failed", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    final String fbID = json.getString("id");
                                    final String imageURL = "https://graph.facebook.com/" + fbID + "/picture?type=large";
                                    final Facebook fb = new Facebook(KVStore.getCurrentUserPK(), fbID, imageURL);
                                    DBHelper.getInstance().createFBRecord(fb);
                                    Toast.makeText(context, "Linked Facebook account", Toast.LENGTH_SHORT).show();
                                    reloadListView();
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
