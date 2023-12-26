package com.example.jwt.service;

import com.example.jwt.model.Member;
import com.example.jwt.repository.MemberRepository;
import com.example.jwt.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final MemberRepository memberRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token.expired-time-ms}")
    private Integer expiredTimeMs;
    @Value("${oauth.client_id}")
    private String clientId;

    private String getUserInfo(String accessToken) {
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers1.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity request2 = new HttpEntity<>(headers1);
        RestTemplate restTemplate2 = new RestTemplate();
        ResponseEntity<Object> response2 = restTemplate2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request2,
                Object.class
        );

        System.out.println(response2);

        String nickname = "" + response2;
        String username = nickname.split(",")[3].split("nickname=")[1].split("}")[0];
        System.out.println("username = " + username);

        return username;
    }

    public String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:8080/member/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestBody = new HttpEntity<>(params, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                requestBody,
                Object.class
        );
        String result = "" + response;
        String accessToken = result.split(",")[1].split("=")[1];

        return getUserInfo(accessToken);
    }

    public void socialSignUp(String userName) {     // email
        memberRepository.save(Member.builder()
                .username(userName)
                .authority("USER")
                .build());
    }

    public String socialLogin(String userName) {
        String token = JwtUtils.generateAccessToken(userName, secretKey, expiredTimeMs);
        if(JwtUtils.validate(token, userName, secretKey)) {
            return token;
        } else {
            return null;
        }
    }
}
