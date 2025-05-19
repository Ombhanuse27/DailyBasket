package com.example.agrishop;

public class Usermodel {
    String email;
    String password;
    String name;
    String phone;
    String useraddress;
    String uid;

    int usertype;
    String profileImg;
    public Usermodel() {
    }

    public Usermodel(String email, String password, String name, String phone, String useraddress, String uid, int usertype) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.useraddress = useraddress;
        this.uid = uid;
        this.usertype = usertype;
        this.profileImg = profileImg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUseraddress() {
        return useraddress;
    }

    public void setUseraddress(String useraddress) {
        this.useraddress = useraddress;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}
