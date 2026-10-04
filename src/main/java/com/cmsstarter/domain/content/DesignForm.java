package com.cmsstarter.domain.content;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

/** 관리자 > 디자인 설정 폼 */
@Getter
@Setter
public class DesignForm {

    private String brandName;
    private String primaryColor;
    private String backgroundColor;
    private String fontPreset;
    private MultipartFile logoFile;
    private boolean removeLogo;
}
