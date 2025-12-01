package com.payMyBuddy.app.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService implements IPasswordService {

    public String Encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }
}
