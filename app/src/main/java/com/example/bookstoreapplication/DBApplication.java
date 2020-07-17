package com.example.bookstoreapplication;

import android.app.Application;

import com.example.bookstoreapplication.Model.UserID;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DBApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().name("RealmData.realm").build();
        Realm.setDefaultConfiguration(configuration);
    }
}

