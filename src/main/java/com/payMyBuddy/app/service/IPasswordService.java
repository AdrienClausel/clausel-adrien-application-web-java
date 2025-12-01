package com.payMyBuddy.app.service;

public interface IPasswordService {
    public String Encode(String password);

    public boolean checkPassword(String password, String encodedPassword);
}
