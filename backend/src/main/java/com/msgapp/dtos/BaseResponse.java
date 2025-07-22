package com.msgapp.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseResponse<T> {

    private int statusCode;
    private String message;
//    private T data;

    public BaseResponse(int statusCode,String message){
        this.statusCode= statusCode;
        this.message = message;
    }

//    public BaseResponse(HttpStatus httpStatus,T data){
//        this.statusCode = httpStatus.value();
//        this.data = data;
//    }
    public BaseResponse(HttpStatus httpStatus,String message){
        this.statusCode = httpStatus.value();
        this.message = message;
    }

}
