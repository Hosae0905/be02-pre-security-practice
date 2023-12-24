package com.example.jwt.config.filter;

import com.example.jwt.model.Member;
import com.example.jwt.service.MemberService;
import com.example.jwt.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final MemberService memberService;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String token;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.split(" ")[1];
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        String username = JwtUtils.getUsername(token, secretKey);
        Member member = memberService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(member.getUsername(), null);

        // 인증 과정 수행 회원 엔티티를 받아온다.
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        if(!JwtUtils.validate(token, member.getUsername(), secretKey)) {
          filterChain.doFilter(request, response);
          return;
        }

        // 인가하는 코드를 작성
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}