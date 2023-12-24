package com.example.jwt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberDto {
    private Integer id;
    private String username;
    private String password;
    private String authority;
}
