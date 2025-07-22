package com.msgapp.dtos.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    private String email;
    private String userName;
    private String password;
}
