package com.scu.jenny.enamecard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.facebook.appevents.AppEventsLogger;
import com.scu.jenny.enamecard.network.NetworkAsyncTask;
import com.scu.jenny.enamecard.network.ProcessResponse;
import com.scu.jenny.enamecard.storage.DBHelper;
import com.scu.jenny.enamecard.storage.KVStore;
import com.scu.jenny.enamecard.storage.User;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity {
    static private Context context;
    ImageView splash;
    View mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KVStore.init(this);
        context = getApplicationContext();
        DBHelper.init(context);
        setContentView(R.layout.activity_welcome);
        splash= (ImageView) this.findViewById(R.id.logo_image);
        mContentView = this.getWindow().getDecorView().findViewById(android.R.id.content);
        this.fadeSplashOut();
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    private void fadeSplashOut() {
        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(10f)
                .setDuration(6000)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        splash.animate()
                .alpha(0f)
                .setDuration(3000)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        splash.setVisibility(View.GONE);
                        checkLoggedInBefore();
                    }
                });
    }

    public void checkLoggedInBefore(){
        if (KVStore.getInstance().get("secret", "").length() > 0) {
            new NetworkAsyncTask(this, "Getting user profile...", new ProcessResponse() {
                @Override
                public void process(String jsonRespose) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonRespose);
                        if (jsonObject.has("firstName")){
                            User user = User.getUserFromJsonObj(jsonObject);
                            long userPK = DBHelper.getInstance().updateOrCreateUserRecord(user);
                            DBHelper.getInstance().getUserByPhoneNumber(jsonObject.getString("phoneNumber"));
                            KVStore.getInstance().set("userPK", userPK);
                            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                            intent.putExtra(MainPageActivity.userInfoKey, jsonObject.toString());
                            startActivity(intent);
                        } else if (jsonObject.has("phoneNumber")){
                            KVStore.getInstance().set("phoneNumber", jsonObject.getString("phoneNumber"));
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
            //TODO purge DB
            DBHelper dbHelper = DBHelper.getInstance();
            context.deleteDatabase(dbHelper.getDatabaseName());
            Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
            startActivity(intent);
        }

    }

}
