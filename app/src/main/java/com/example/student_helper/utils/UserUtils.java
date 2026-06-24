package com.example.student_helper.utils;

import com.google.firebase.auth.FirebaseAuth;

public class UserUtils {

    public static String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return "guest";
    }
}