package com.capstone2025.team7.backend.auth.jwt.filter;

import com.capstone2025.team7.backend.auth.jwt.provider.JwtTokenizer;
import com.capstone2025.team7.backend.auth.dto.LoginDto;
import com.capstone2025.team7.backend.auth.service.MemberDetailsService;
import com.capstone2025.team7.backend.user.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenizer jwtTokenizer) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenizer = jwtTokenizer;
    }

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response){
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper
                .readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected  void successfulAuthentication(HttpServletRequest request,
                                             HttpServletResponse response,
                                             FilterChain chain,
                                             Authentication authResult) {
        MemberDetailsService.MemberDetail memberDetail = (MemberDetailsService.MemberDetail) authResult.getPrincipal();
        User user = memberDetail.getUser();

        String accessToken = delegateAccessToken(user);
        String refreshToken = delegateRefreshToken(user);

        response.addCookie(createCookie(user.getEmail(), refreshToken));
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);
    }

    private String delegateAccessToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getEmail());
        claims.put("roles", user.getRoles());

        String subject = user.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
        String base64encodedSecretKey = jwtTokenizer.encodedBase64SecretKey(jwtTokenizer.getSecretKey());

        String acceessKey = jwtTokenizer.generateAccessToken(claims,
                subject, expiration, base64encodedSecretKey);

        return acceessKey;
    }

    private String delegateRefreshToken(User user){
        String subject = user.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64encodedSecretKey = jwtTokenizer.encodedBase64SecretKey(jwtTokenizer.getSecretKey());

        return jwtTokenizer.generateRefreshToken(
                subject, expiration, base64encodedSecretKey);
    }

    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String > quertParams = new LinkedMultiValueMap<>();
        quertParams.add("access_token", accessToken);
        quertParams.add("refresh_token", refreshToken);
        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(3000)
                .queryParams(quertParams)
                .build()
                .toUri();
    }

    public Cookie createCookie(String userName, String refreshToken) {
        String cookieName = "refreshtoken";
        String cookieValue = refreshToken; //
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // 쿠키 속성 설정
        cookie.setHttpOnly(true);  //httponly 옵션 설정
        cookie.setSecure(true); //https 옵션 설정(디폴트 false/요즘은 브라우저에서 거름)
        cookie.setPath("/"); // 모든 곳에서 쿠키열람이 가능하도록 설정
        cookie.setMaxAge(60 * 60 * 24); //쿠키 만료시간 설정
        return cookie;
    }
}
