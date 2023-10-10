package com.example.config;

import com.example.entity.RegistrationSource;
import com.example.entity.UserEntity;
import com.example.entity.UserRole;
import com.example.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    String FRONTEND_URL = "http://localhost:3000";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        if(RegistrationSource.GITHUB.equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()) || RegistrationSource.GOOGLE.equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();
            String email = attributes.getOrDefault("email", " ").toString();
            String name = attributes.getOrDefault("name", " ").toString();
            userService.findByEmail(email)
                            .ifPresentOrElse(user -> {
                                DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(user.getRole().name())),
                                        attributes, "id");
                                Authentication secureAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(user.getRole().name())),
                                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                                SecurityContextHolder.getContext().setAuthentication(secureAuth);
                            }, () -> {
                                UserEntity user = new UserEntity();
                                user.setRole(UserRole.ADMIN);
                                user.setEmail(email);
                                user.setName(name);
                                user.setSource(RegistrationSource.GITHUB);
                                userService.save(user);

                                DefaultOAuth2User newUser = new DefaultOAuth2User(List.of(new SimpleGrantedAuthority(user.getRole().name())),
                                        attributes, "id");
                                Authentication secureAuth = new OAuth2AuthenticationToken(newUser, List.of(new SimpleGrantedAuthority(user.getRole().name())),
                                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
                                SecurityContextHolder.getContext().setAuthentication(secureAuth);

                            });
        }
        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl(FRONTEND_URL);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
