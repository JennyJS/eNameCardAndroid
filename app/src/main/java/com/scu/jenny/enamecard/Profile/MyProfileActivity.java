package com.scu.jenny.enamecard.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.scu.jenny.enamecard.LogInActivity;
import com.scu.jenny.enamecard.R;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.Facebook;
//import com.scu.jenny.enamecard.thirdparty.TwitterActivity;
import com.scu.jenny.enamecard.storage.KVStore;
import com.scu.jenny.enamecard.thirdparty.MediaType;
import com.scu.jenny.enamecard.widget.SlideToUnlock;
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

import io.fabric.sdk.android.Fabric;

public class MyProfileActivity extends AppCompatActivity implements SlideToUnlock.OnUnlockListener {
    private static CallbackManager fbCallbackmanager;
    private static TwitterAuthClient twitterAuthClient;

    private SlideToUnlock slideToUnlock;
    private Context context;

    //    private ListView myListView;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private MediaType mediaType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = getApplicationContext();

        slideToUnlock = (SlideToUnlock) findViewById(R.id.slidetounlock);
        slideToUnlock.setOnUnlockListener(this);

//        myListView = (ListView) findViewById(R.id.list_view);
        gridView = (GridView) findViewById(R.id.gridView);

        // Twitter
//        if(isTwitterLoggedIn()){
//            connectionList.add(new AdapterConnector("icon_twitter.png", "connected.jpg", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    twitterLogin();
//                }
//            }));
//        } else {
//            connectionList.add(new AdapterConnector("icon_twitter.png", "icon_add.png", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    twitterLogin();
//                }
//            }));
//
//        }
        reloadGridView();
    }

    @Override
    public void onUnlock() {
        Toast.makeText(MyProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        KVStore.getInstance().set("secret", null);
        Intent intent = new Intent(MyProfileActivity.this, LogInActivity.class);
        startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.mediaType != null) {
            switch (this.mediaType) {
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

    private void reloadGridView() {
        gridView.invalidateViews();
        final ArrayList<AdapterConnector> connectionList = new ArrayList<>();
        connectionList.add(new AdapterConnector(MediaType.QUORA, null, null));
        connectionList.add(new AdapterConnector(MediaType.TWITTER, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterLogin();
            }
        }));

        // Facebook
        if (isFBLoggedIn()) {
            // get FB URL from DB
            Facebook fb = DBHelper.getInstance().getFBByUserID(KVStore.getCurrentUserPK());
            connectionList.add(new AdapterConnector(MediaType.FACEBOOK, fb == null ? null : fb.imageURL, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFbLogout();
                }
            }));
        } else {
            connectionList.add(new AdapterConnector(MediaType.FACEBOOK, null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFblogin();
                }
            }));
        }

        connectionList.add(new AdapterConnector(MediaType.LINKEDIN, null, null));

        gridView.setAdapter(new GridViewAdapter(this, R.layout.grid_item_layout, connectionList));

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
        this.mediaType = MediaType.TWITTER;
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
        this.mediaType = MediaType.FACEBOOK;
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
                reloadGridView();
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
                                    reloadGridView();
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
