package com.cmsstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "portone")
public class PortOneProperties {

    private String impCode = "imp00000000";
    private String pg = "html5_inicis";
    private String apiKey = "";
    private String apiSecret = "";
    private boolean verifyEnabled = false;
}
