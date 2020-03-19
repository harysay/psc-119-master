package com.kominfo.harysay.psc.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User implements Parcelable {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "email")
    private String email;

    @NonNull
    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "password")
    private String password;

    @Ignore
    @ColumnInfo(name = "profile_picture")
    private String profile_picture;

    public User(String email, String username, String name, String password, String profile_picture) {
        this.email = email;
        this.username = username;
        this.name = name;
        this.password = password;
        this.profile_picture = profile_picture;
    }

    public User() {
    }

    protected User(Parcel in) {
        email = in.readString();
        username = in.readString();
        name = in.readString();
        password = in.readString();
        profile_picture = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    @Override
    public String toString() {
        return "Users{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(username);
        dest.writeString(name);
        dest.writeString(password);
        dest.writeString(profile_picture);
    }
}
