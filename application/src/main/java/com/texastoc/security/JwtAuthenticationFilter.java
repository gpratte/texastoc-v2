package com.texastoc.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texastoc.model.user.Player;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider JwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider JwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.JwtTokenProvider = JwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            Player player = new ObjectMapper()
                .readValue(req.getInputStream(), Player.class);

            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    player.getEmail(),
                    player.getPassword(),
                    new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        final String token = JwtTokenProvider.generateToken(auth);

        // return token in header
        //res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);

        // return token in body as json
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"token\":\"" + token + "\"}"
        );
    }

}
