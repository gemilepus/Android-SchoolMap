package com.vine.projectdemo.API;

import com.vine.projectdemo.Model.ServerRequest;
import com.vine.projectdemo.Model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("login-register/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
