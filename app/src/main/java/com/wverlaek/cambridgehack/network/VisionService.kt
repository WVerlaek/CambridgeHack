//package com.wverlaek.cambridgehack.network
//
//import com.wverlaek.cambridgehack.util.Constants
//import retrofit2.Call
//import retrofit2.Retrofit
//import retrofit2.http.GET
//import retrofit2.http.POST
//
///**
// * Created by WVerl on 20-1-2018.
// */
//interface VisionService {
//    @POST("detect?returnFaceId")
//    fun detect(): Call<List<Face>>
//
//
//    fun create(): VisionService {
//        return Retrofit.Builder()
//                .baseUrl(Constants.MS_API_BASE_FACE + Constants.MS_API_FACE_DETECT)
//                .build()
//                .create(VisionService::class.java)
//    }
//}