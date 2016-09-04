package jp.gr.java_conf.ya.yumura; // Copyright (c) 2013-2016 YA <ya.androidapp@gmail.com> All rights reserved. --><!-- This software includes the work that is distributed in the Apache License 2.0

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import jp.gr.java_conf.ya.yumura.Twitter.KeyManage;
import jp.gr.java_conf.ya.yumura.Twitter.OAuthUser;
import jp.gr.java_conf.ya.yumura.Twitter.TwitterAccess;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;

public class CallbackActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callback);
        final Intent intent = getIntent();
        if (intent != null) {
            final Uri uri = intent.getData();
            if (uri != null && uri.toString().startsWith(TwitterAccess.CALLBACK_URL)) {
                final String verifier = uri.getQueryParameter(TwitterAccess.CALLBACK_URL_VERIFIER);
                final String[] savedConsumerKeyAndSecret = KeyManage.loadCurrentConsumerKeyAndSecret();
//                Log.v("Yumura","loadCurrentConsumerKeyAndSecret: "+savedConsumerKeyAndSecret[0]+" , "+savedConsumerKeyAndSecret[1]);

                new Thread(new Runnable() {
                    @Override
                    public final void run() {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public final void run() {
                        try {
                            AccessToken accessToken = TlActivity.oAuthAuthorization.getOAuthAccessToken(TlActivity.requestToken, verifier);
                            KeyManage.addUser(new OAuthUser(accessToken.getScreenName(), savedConsumerKeyAndSecret[0],savedConsumerKeyAndSecret[1], accessToken.getScreenName(), accessToken.getToken(), accessToken.getTokenSecret(), accessToken.getUserId()));

                            final Intent i = new Intent(getApplicationContext(),TlActivity.class);
                            startActivity(i);
                        } catch (Exception e) {
//                            Log.v("Yumura",e.getMessage());
                        }
//                        }
//                    });
                    }
                }).start();
            }
        }
    }

}