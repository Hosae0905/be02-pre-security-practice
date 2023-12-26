package com.example.jwt.controller;

import com.example.jwt.model.Member;
import com.example.jwt.model.MemberDto;
import com.example.jwt.service.MemberService;
import com.example.jwt.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final SocialLoginService socialLoginService;

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<Object> create(@RequestBody MemberDto memberDto) {
         memberService.signUp(memberDto);
        return ResponseEntity.ok().body("ok");
    }


    @RequestMapping(method = RequestMethod.GET, value = "/login")
    public ResponseEntity<Object> login(String username) {
        String result = memberService.login(username);
        return ResponseEntity.ok().body(result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/kakao")
    public ResponseEntity<Object> socialLogin(String code) {
        String userName = socialLoginService.getAccessToken(code);
        Member user = memberService.getMemberByUserName(userName);

        if (user == null) {
            socialLoginService.socialSignUp(userName);
        }
        return ResponseEntity.ok().body(socialLoginService.socialLogin(userName));
    }
}
