package com.example.bookstoreapplication.Helper;

import android.content.Context;
import android.provider.Contacts;

import com.example.bookstoreapplication.Model.BookID;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class MyHelper {

    Realm realm;
    RealmResults<BookID> bookIDS;

    public MyHelper(Realm realm) {
        this.realm = realm;
    }

    public void selectFromRealm(){
        bookIDS = realm.where(BookID.class).findAll();
    }

    public ArrayList<BookID> Refresh(){
        ArrayList<BookID> listed_books = new ArrayList<>();
        for (BookID book : bookIDS){
            listed_books.add(book);
        }

        return listed_books;
    }
}
