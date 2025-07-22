package com.msgapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecureMessage {
    private String from;
    private String to;
    private String text;

    public SecureMessage(String from,String to,String text){
        this.from = from;
        this.to = to;
        this.text = text;
    }

}
