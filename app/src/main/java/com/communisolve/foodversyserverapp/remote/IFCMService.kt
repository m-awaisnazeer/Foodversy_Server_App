package com.communisolve.foodversyserverapp.remote

import com.communisolve.foodversy.model.FCMResponse
import com.communisolve.foodversy.model.FCMSendData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAEER2YgI:APA91bHpmVU9NNtm-StdKs3WgrSFCEzrkkKNDYPuLUzJaWuJj8fSj0ZeIK6QNFuB4YjR30deU9fh_jQIDNZuY0LtRGDeJ-uNgCs6u7SZcGxcnhv5fdNtmZdegyxg5nLfSfAlv3bUFpld"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData): Observable<FCMResponse>
}