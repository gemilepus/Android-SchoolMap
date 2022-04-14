package com.vine.projectdemo.API;

import com.vine.projectdemo.AccountView.models.ServerRequest;
import com.vine.projectdemo.Model.JSONResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RequestInterfaceAll {
    @POST("login-register/")
    Call<JSONResponse> operation(@Body ServerRequest request);
}