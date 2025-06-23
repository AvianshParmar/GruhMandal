package com.example.gruhmandal.modeladmin;

import android.content.Context;

import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import android.os.AsyncTask;


public class FCMHelper {

    public interface TokenCallback {
        void onTokenReceived(String token);
        void onError(Exception e);
    }

    public static void getAccessToken(Context context, TokenCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    InputStream serviceAccount = context.getAssets().open("gruh-mandal-378fe58d9f62.json");

                    GoogleCredentials googleCredentials = GoogleCredentials
                            .fromStream(serviceAccount)
                            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
                    googleCredentials.refreshIfExpired();

                    return googleCredentials.getAccessToken().getTokenValue();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null) {
                    callback.onTokenReceived(token);
                } else {
                    callback.onError(new Exception("Failed to get token"));
                }
            }
        }.execute();
    }
}
