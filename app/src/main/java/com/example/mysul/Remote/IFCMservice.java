package com.example.mysul.Remote;


import com.example.mysul.Model.MyResponse;
import com.example.mysul.Model.Request;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMservice {
    @Headers({
            "Content-Type:applicaiton/json",
            "Authorization:key = AAAAdY6R2b0:APA91bEyoVmcvwzEtMJFIdyj2VU7ev89KYMbcoYwqwnVQPgeD_MBWZtuUCetURoX73LKHlrO3wB1q4tZW7j_R9L06FrafVGvwAmDrMMs5csXGe0p3ZLPtuOfhF-x3Tw7RwVelLD5_Zo1"
    })


    @POST ("fcm/send")
    Observable<MyResponse> sendFriendRequestToUser (@Body Request body);
}
