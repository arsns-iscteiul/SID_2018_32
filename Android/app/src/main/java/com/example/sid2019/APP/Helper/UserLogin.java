package com.example.sid2019.APP.Helper;

public class UserLogin {

    private static UserLogin instance;
    private String ip;
    private String port;
    private String username;
    private String password;

    public UserLogin(String ip, String port, String username, String password) {
        instance = this;
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static UserLogin getInstance() {
        return instance;
    }
}
