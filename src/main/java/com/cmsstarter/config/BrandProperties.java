package com.cmsstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * 사이트 브랜드의 <b>기본값</b> (application.yml 의 brand.*).
 * 관리자 > 디자인 설정에서 저장한 값이 있으면 그 값이 우선한다 (참고: SiteBrand).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "brand")
public class BrandProperties {

    private String name = "LUMO";
    private String tagline = "";
    private String logoPath = "";
    private String primaryColor = "#4E90C4";
    private String backgroundColor = "#F7FBFE";
    /** FontPreset 이름 (PRETENDARD, NANUM_GOTHIC, NANUM_SQUARE_ROUND, NOTO_SANS_KR, IBM_PLEX_SANS_KR) */
    private String fontPreset = "PRETENDARD";
    private String company = "";
    private String contactEmail = "";
    private int shippingFee = 3000;
    private int freeShippingOver = 50000;
}
