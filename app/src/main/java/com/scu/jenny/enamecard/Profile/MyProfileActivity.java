package com.scu.jenny.enamecard.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISession;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.scu.jenny.enamecard.LogInActivity;
import com.scu.jenny.enamecard.R;
import com.scu.jenny.enamecard.WelcomeActivity;
import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.CurrentUser;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.DrawableManager;
//import com.scu.jenny.enamecard.thirdparty.TwitterActivity;
import com.scu.jenny.enamecard.storage.KVStore;
import com.scu.jenny.enamecard.storage.User;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.fabric.sdk.android.Fabric;

import static com.scu.jenny.enamecard.storage.User.*;

public class MyProfileActivity extends AppCompatActivity implements SlideToUnlock.OnUnlockListener {
    private static CallbackManager fbCallbackmanager;
    private static TwitterAuthClient twitterAuthClient;

    private CircleImageView profileView;
    private SlideToUnlock slideToUnlock;
    private TextView textView;
    public static Context context;

    //    private ListView myListView;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private MediaType mediaType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        context = getApplicationContext();

        profileView = (CircleImageView) findViewById(R.id.profileIV);

        // for move on purpose
        loadProfileImage();
        profileView.setOnClickListener(getProfileViewOnClickListener());

        slideToUnlock = (SlideToUnlock) findViewById(R.id.slidetounlock);
        slideToUnlock.setOnUnlockListener(this);

        gridView = (GridView) findViewById(R.id.gridView);
        reloadGridView();
        textView = (TextView)findViewById(R.id.user_name);
        textView.setText(CurrentUser.getUserFirstName() + " " + CurrentUser.getUserLastName());
    }

    @Override
    public void onUnlock() {
        Toast.makeText(MyProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        KVStore.getInstance().set("secret", null);
        Intent intent = new Intent(MyProfileActivity.this, WelcomeActivity.class);
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
                case LINKEDIN:
                    LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }


    private void loadProfileImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (CurrentUser.get().imageURL != null) {
                    DrawableManager.getInstance().fetchDrawableOnThread(CurrentUser.get().imageURL, profileView);
                }
            }
        });
    }

    /**********************************************************/
    /********************** UI Rendering **********************/
    /**********************************************************/

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
        final SocialMedia fb = CurrentUser.getFacebook();
        if (fb != null) {
            connectionList.add(new AdapterConnector(MediaType.FACEBOOK, fb.imageURL, new View.OnClickListener() {
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

        final SocialMedia lI = CurrentUser.getLinkedIn();
        if(CurrentUser.getLinkedIn() != null){
            connectionList.add(new AdapterConnector(MediaType.LINKEDIN, lI.imageURL, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //change to FB
                    onFbLogout();
                }
            }));
        } else {
            connectionList.add(new AdapterConnector(MediaType.LINKEDIN, null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLinkedInLogIn();
                }
            }));
        }

        connectionList.add(new AdapterConnector(MediaType.ADD, null, null));
        gridView.setAdapter(new GridViewAdapter(this, R.layout.grid_item_layout, connectionList));
    }


    private void flipViewflipView(ViewFlipper flipper) {
        if(flipper.getDisplayedChild() == 0){
            flipper.setDisplayedChild(1);
        }
        else{
            flipper.setDisplayedChild(0);
        }
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
    /************** Profile View OnClickListener **************/
    /**********************************************************/
    private View.OnClickListener getProfileViewOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GridView gridView = new GridView(MyProfileActivity.this);

                if (CurrentUser.get().socialMedias == null || CurrentUser.get().socialMedias.size() == 0) {
                    Toast.makeText(MyProfileActivity.this, "You haven't linked any social account yet", Toast.LENGTH_LONG).show();
                    return;
                }

                final List<String> imageURLs = new ArrayList<>();
                for (int i = 0; i < CurrentUser.get().socialMedias.size(); i++) {
                    SocialMedia socialMedia = CurrentUser.get().socialMedias.get(i);
                    imageURLs.add(socialMedia.imageURL);
                }

                final AlertDialog alertDialog = new AlertDialog.Builder(MyProfileActivity.this)
                        .setView(gridView)
                        .setTitle("Choose Profile Image").show();

                AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String newImageURL = imageURLs.get(position);
                        if (CurrentUser.get().imageURL == null || !CurrentUser.get().imageURL.equals(newImageURL)) {
                            CurrentUser.get().imageURL = newImageURL;
                            new NetworkAsyncTask(MyProfileActivity.this, "Updating Profile Image", new ProcessResponse() {
                                @Override
                                public void process(String jsonRespose) {
                                    try {
                                        JSONObject userJsonObj = new JSONObject(jsonRespose);
                                        User newUser = User.getUserFromJsonObj(userJsonObj);
                                        DBHelper.getInstance().updateOrCreateUserRecord(newUser);
                                        loadProfileImage();
                                        alertDialog.dismiss();
                                    } catch (JSONException e) {
                                        Toast.makeText(MyProfileActivity.this, "Error from server", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).execute("PUT", "/user", CurrentUser.get().toJson().toString());
                        }
                    }
                };


                gridView.setAdapter(new ProfileImageAdapter(context, R.layout.profile_image_layout, imageURLs));
                gridView.setNumColumns(3);
                gridView.setOnItemClickListener(listener);



            }
        };
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
                new NetworkAsyncTask(MyProfileActivity.this, "Unlinking on server", new ProcessResponse() {
                    @Override
                    public void process(String jsonRespose) {
                        try {
                            JSONObject jsonObject = new JSONObject(jsonRespose);
                            User user = User.getUserFromJsonObj(jsonObject);
                            DBHelper.getInstance().updateOrCreateUserRecord(user);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    reloadGridView();
                                }
                            });
                        } catch (JSONException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MyProfileActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).execute("DELETE", "/user/socialMedia", CurrentUser.getFacebook().toJsonObj().toString());
                DBHelper.getInstance().deleteFBRecordByUserID(KVStore.getCurrentUserPK());
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
                                    final SocialMedia fb = new SocialMedia("facebook", fbID, imageURL);
                                    new NetworkAsyncTask(MyProfileActivity.this, "Setting up FB", new ProcessResponse() {
                                        @Override
                                        public void process(String jsonRespose) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(jsonRespose);
                                                User user = User.getUserFromJsonObj(jsonObject);
                                                DBHelper.getInstance().updateOrCreateUserRecord(user);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(context, "Linked Facebook account", Toast.LENGTH_SHORT).show();
                                                        reloadGridView();
                                                    }
                                                });
                                            } catch (JSONException e) {
                                                Toast.makeText(MyProfileActivity.this, "Record may already exist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).execute("POST", "/user/socialMedia", fb.toJsonObj().toString());
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
    @Override
    public void onBackPressed() {
        System.out.println("Disable back button");
    }

    /**********************************************************/
    /*************** LinkedIn Login / Logout CallBack *********/
    /**********************************************************/

    // CLient ID: 75o2r7dstes1g6   Client Secret: vr0pnEmuhqUQBByK

    public void onLinkedInLogIn(){
        this.mediaType = MediaType.LINKEDIN;
        LISessionManager sessionManager = LISessionManager.getInstance(context);
        LISession session = sessionManager.getSession();
        boolean accessTokenValid = session.isValid();

        if (accessTokenValid) {
            getProfile();
        } else {
            LISessionManager.getInstance(context).init(
                    MyProfileActivity.this,
                    Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE),
                    new AuthListener() {
                        @Override
                        public void onAuthSuccess() {
                            System.out.println("onAuthSuccess");
                            getProfile();
                        }
                        @Override
                        public void onAuthError(LIAuthError error) {
                            System.out.print("Linkedin failed: " + error.toString());
                        }
                    },
                    true
            );
        }
    }

    public void getProfile() {
        APIHelper apiHelper = APIHelper.getInstance(context);
        apiHelper.getRequest(context, "https://api.linkedin.com/v1/people/~:(first-name,last-name,public-profile-url)", new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse s) {
                System.out.println("get linked api reponse" + s.getResponseDataAsString());
            }

            @Override
            public void onApiError(LIApiError error) {
                System.out.println("Error...");
            }
        });

    }




}
