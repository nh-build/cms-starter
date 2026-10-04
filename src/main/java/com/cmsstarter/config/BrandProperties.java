package com.cmsstarter.config;

import java.util.regex.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/** 사이트 브랜드 설정. application.yml 의 brand.* 를 읽어 모든 페이지에 CSS 변수로 주입한다. */
@Getter
@Setter
@ConfigurationProperties(prefix = "brand")
public class BrandProperties {

    private static final Pattern HEX = Pattern.compile("^#[0-9a-fA-F]{3,8}$");
    private static final Pattern FONT = Pattern.compile("^[\\p{L}0-9 ,'\"\\-_]+$");

    private String name = "LUMO";
    private String tagline = "";
    private String logoPath = "";
    private String primaryColor = "#4E90C4";
    private String backgroundColor = "#F7FBFE";
    private String fontFamily = "sans-serif";
    private String fontCssUrl = "";
    private String company = "";
    private String contactEmail = "";
    private int shippingFee = 3000;
    private int freeShippingOver = 50000;

    public boolean hasLogo() {
        return logoPath != null && !logoPath.isBlank();
    }

    /** <style> 안에 그대로 출력되므로 허용된 형식만 통과시킨다. */
    public String getCssVars() {
        String primary = HEX.matcher(primaryColor).matches() ? primaryColor : "#4E90C4";
        String bg = HEX.matcher(backgroundColor).matches() ? backgroundColor : "#F7FBFE";
        String font = FONT.matcher(fontFamily).matches() ? fontFamily : "sans-serif";
        return ":root{--brand:" + primary + ";--bg:" + bg + ";--font:" + font + ";}";
    }
}
