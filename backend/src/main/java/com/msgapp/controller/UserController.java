package com.msgapp.controller;

import com.msgapp.dtos.requests.AuthRequest;
import com.msgapp.dtos.requests.UserCreateRequest;
import com.msgapp.dtos.BaseResponse;
import com.msgapp.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    BaseResponse<String> RegisterUser(@RequestBody UserCreateRequest request){
        return userService.SaveUser(request);
    }

    @PostMapping("/authenticate")
    BaseResponse<String> Authenticate(@RequestBody AuthRequest request){
        return userService.Authenticate(request);
    }

}
