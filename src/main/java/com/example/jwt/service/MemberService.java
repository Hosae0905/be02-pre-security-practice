package com.example.jwt.service;

import com.example.jwt.exception.ApplicationException;
import com.example.jwt.exception.ErrorCode;
import com.example.jwt.model.Member;
import com.example.jwt.model.MemberDto;
import com.example.jwt.model.MemberLoginReq;
import com.example.jwt.repository.MemberRepository;
import com.example.jwt.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Configuration
@RequiredArgsConstructor
public class MemberService implements UserDetailsService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.token.expired-time-ms}")
    private Integer expiredTimeMs;

    @Override
    public Member loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> name = memberRepository.findByUsername(username);
        Member member = null;

        if(name.isPresent()) {
            member = name.get();
        }
        return member;
    }

    public Member getMemberByUserName(String userName) {
        Optional<Member> username = memberRepository.findByUsername(userName);

        if (username.isPresent()) {
            return username.get();
        } else {
            return null;
        }

//        return username.orElse(null);
    }

    public void signUp(MemberDto memberDto) {

        memberRepository.findByUsername(memberDto.getUsername()).ifPresent(it -> {
            throw new ApplicationException(ErrorCode.DUPLICATED_USER, String.format("%s는 이미 존재합니다.", memberDto.getUsername()));
        });

        memberRepository.save(Member.builder()
                .username(memberDto.getUsername())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .authority("USER")
                .build());
    }

    public String login(MemberLoginReq memberLoginReq) {
        Member member = memberRepository.findByUsername(memberLoginReq.getUsername()).get();

        if (passwordEncoder.matches(memberLoginReq.getPassword(), member.getPassword())) {
            return JwtUtils.generateAccessToken(member.getUsername(), secretKey, expiredTimeMs);
        } else {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
