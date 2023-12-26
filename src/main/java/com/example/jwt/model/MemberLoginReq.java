package com.example.jwt.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginReq {
    private String username;
    private String password;
}
