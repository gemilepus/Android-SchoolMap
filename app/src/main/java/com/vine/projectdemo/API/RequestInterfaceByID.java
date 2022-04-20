package com.vine.projectdemo.API;

import com.vine.projectdemo.Model.ServerRequest;
import com.vine.projectdemo.Model.JSONResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RequestInterfaceByID {
    @POST("login-register/")
    Call<JSONResponse> operation(@Query("id") String id,@Body ServerRequest request);
}