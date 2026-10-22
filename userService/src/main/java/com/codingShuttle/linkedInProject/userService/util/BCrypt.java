package com.codingShuttle.linkedInProject.userService.util;

import static org.mindrot.jbcrypt.BCrypt.*;

public class BCrypt {

    public static String hashPassword(String plainPassword) {
        return hashpw(plainPassword, gensalt());
    }

    public static boolean matchPassword(String plainPassword, String hashedPassword) {
        return checkpw(plainPassword, hashedPassword);
    }
}
