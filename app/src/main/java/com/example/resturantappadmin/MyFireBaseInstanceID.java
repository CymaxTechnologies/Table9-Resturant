package com.example.resturantappadmin;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFireBaseInstanceID extends FirebaseInstanceIdService {
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }
}
