//package com.scu.jenny.enamecard.thirdparty;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import com.scu.jenny.enamecard.R;
//import com.twitter.sdk.android.core.Callback;
//import com.twitter.sdk.android.core.Result;
//import com.twitter.sdk.android.core.TwitterException;
//import com.twitter.sdk.android.core.identity.TwitterLoginButton;
//import com.twitter.sdk.android.tweetcomposer.TweetComposer;
//
//
///**
// * Created by jenny on 3/9/16.
// */
//public class TwitterActivity extends Activity {
//    private TwitterLoginButton twitterButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.twitter_login_btn);
//        setUpViews();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        twitterButton.onActivityResult(requestCode, resultCode, data);
//    }
//
//    private void setUpViews() {
//        setUpTwitterButton();
//    }
//
//    private void setUpTwitterButton() {
//        twitterButton = (TwitterLoginButton) findViewById(R.id.twitter_button);
//        twitterButton.setCallback(new Callback() {
//            @Override
//            public void success(Result result) {
//                Toast.makeText(getApplicationContext(),
//                        getResources().getString(R.string.app_name),
//                        Toast.LENGTH_SHORT).show();
//
//                setUpViewsForTweetComposer();
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                Toast.makeText(getApplicationContext(),
//                        getResources().getString(R.string.app_name),
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void setUpViewsForTweetComposer() {
//        TweetComposer.Builder builder = new TweetComposer.Builder(this)
//                .text("Just setting up my Fabric!");
//        builder.show();
//    }
//}
