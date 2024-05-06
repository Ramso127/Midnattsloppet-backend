package com.pvt.groupOne.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupImage")
public class GroupImage {
    @Id
    private String groupName;
    private String base64Image;

    public GroupImage(String groupName, String base64Image) {
        this.groupName = groupName;
        this.base64Image = base64Image;
    }

    public GroupImage() {
    }

    public String getgroupName() {
        return groupName;
    }

    public void setgroupName(String userName) {
        this.groupName = userName;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

}
