package com.ikvant.loriapp.database.token;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ikvant.
 */
@Entity
public class Token {
    @PrimaryKey
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
