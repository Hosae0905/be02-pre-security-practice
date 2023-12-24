package com.example.jwt.controller;

import com.example.jwt.model.MemberDto;
import com.example.jwt.service.MemberService;
import com.example.jwt.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class JwtController {



    private final MemberService memberService;


    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<Object> create(@RequestBody MemberDto memberDto) {
         memberService.signUp(memberDto);
        return ResponseEntity.ok().body("ok");
    }


    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public ResponseEntity<Object> login(String username, String token) {
        String result = memberService.login(token, username);
        return ResponseEntity.ok().body(result);
    }
}
