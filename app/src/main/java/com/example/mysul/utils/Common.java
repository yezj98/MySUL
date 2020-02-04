package com.example.mysul.utils;

import com.example.mysul.Model.User;
import com.example.mysul.Remote.IFCMservice;
import com.example.mysul.Remote.Retrofitclient;

public class Common {
    public static final String USER_INFORMATION = "UserInformation";
    public static final String USER_UID_SAVE_KEY = "SAVEUid" ;
    public static final String TOKENS = "Tokens" ;
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptlist";
    public static final String FROM_UID = "FromUID";
    public static final String TO_UID = "ToUID" ;
    public static final String TO_NAME = "ToNAME";
    public static User loggeduser;

    public static IFCMservice getFCMservice () {
        return Retrofitclient.getClient("https://fcm.googleapis.com/").create(IFCMservice.class);
    }
}
