package com.cmsstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String uploadDir = "./uploads";
    private Admin admin = new Admin();

    @Getter
    @Setter
    public static class Admin {
        private String email = "admin@example.com";
        private String password = "";
        private String name = "관리자";
    }
}
