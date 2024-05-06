package com.pvt.groupOne.model;

public class Image {
    
    private String userName;
    private String base64Image;

    public Image(){

    }
    
    public Image(String userName, String base64Image) {
        this.userName = userName;
        this.base64Image = base64Image;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getBase64Image() {
        return base64Image;
    }
    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }


}
