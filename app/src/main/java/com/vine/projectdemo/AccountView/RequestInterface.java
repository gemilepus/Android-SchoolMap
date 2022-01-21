package com.vine.projectdemo.AccountView;

import com.vine.projectdemo.AccountView.models.ServerRequest;
import com.vine.projectdemo.AccountView.models.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RequestInterface {
    @POST("login-register/")
    Call<ServerResponse> operation(@Body ServerRequest request);
}
