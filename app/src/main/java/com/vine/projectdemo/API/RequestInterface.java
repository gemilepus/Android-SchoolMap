package com.vine.projectdemo.API;

import com.vine.projectdemo.Model.JSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {
    @GET("login-register/api/")
    Call<JSONResponse> getJSON();
}