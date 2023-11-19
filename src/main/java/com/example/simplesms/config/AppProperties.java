package com.example.simplesms.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.ArrayList;
import java.util.List;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private Token token;
    private OAuth2 oauth2;

    public AppProperties(Token token, OAuth2 oauth2) {
        this.token = token;
        this.oauth2 = oauth2;
    }

    @Getter
    public static class Token {
        private String accessTokenSecret;
        private long accessTokenExpirationMsec;

        public Token(String accessTokenSecret, long accessTokenExpirationMsec) {
            this.accessTokenSecret = accessTokenSecret;
            this.accessTokenExpirationMsec = accessTokenExpirationMsec;
        }
    }

    @Getter
    public static class OAuth2 {
        private List<String> authorizedRedirectUris = new ArrayList<>();

        public OAuth2(List<String> authorizedRedirectUris) {
            this.authorizedRedirectUris = authorizedRedirectUris;
        }
    }
}