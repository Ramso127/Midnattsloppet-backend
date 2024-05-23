package com.pvt.groupOne.model;

public class UserRequest {
    private String username;
    private String password;
    private String email;
    private String companyname;

    

    public UserRequest() {
    }

    public UserRequest(String username, String password, String email, String companyname) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.companyname = companyname;
    }
    
    public String getCompanyname() {
        return companyname;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String toString() {
        return "Company name: " + getCompanyname() + " Email: " + getEmail() + " Username: " + getUsername()
                + "Password: " + getPassword();
    }

}
