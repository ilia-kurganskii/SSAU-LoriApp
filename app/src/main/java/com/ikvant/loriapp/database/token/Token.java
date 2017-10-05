package com.ikvant.loriapp.database.token;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by ikvant.
 */
@Entity
public class Token {
    @PrimaryKey
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
