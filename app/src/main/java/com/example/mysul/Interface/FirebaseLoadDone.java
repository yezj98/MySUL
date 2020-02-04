package com.example.mysul.Interface;

import java.util.List;

public interface FirebaseLoadDone {
    void FirebaseLoadUserName (List <String> lstName);
    void FirebaseFailure (String message);
}
