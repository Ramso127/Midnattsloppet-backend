package com.pvt.groupOne.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupImage")
public class GroupImage {
    @Id
    private String groupName;

    @Lob
    @Column(length = 150000)
    private String base64Image;

    private int length;

    public GroupImage(String groupName, String base64Image, int length) {
        this.groupName = groupName;
        this.base64Image = base64Image;
        this.length = length;
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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
