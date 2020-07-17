package com.example.bookstoreapplication.Model;

import androidx.core.content.PermissionChecker;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserID extends RealmObject {

    @PrimaryKey
    @Required
    private String Username;
    @Required
    private String Password;

    public UserID() {

    }

    public UserID(String Username, String Password)
    {
        this.Username = Username;
        this.Password = Password;
    }


    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }
}
