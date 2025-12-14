package com.payMyBuddy.app.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService implements IPasswordService {

    /**
     * Cryptage du mot de passe
     *
     * @param password mot de passe
     * @return mot de passe crypté
     */
    public String Encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Vérifie le mot de passe
     *
     * @param password        mot de passe
     * @param encodedPassword mot de passe crypté
     * @return Vrai si le mot de passe est correct sinon false
     */
    public boolean checkPassword(String password, String encodedPassword) {
        return BCrypt.checkpw(password, encodedPassword);
    }
}
