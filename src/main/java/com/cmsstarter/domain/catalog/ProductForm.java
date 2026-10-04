package com.cmsstarter.domain.catalog;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductForm {

    @NotBlank(message = "상품명을 입력하세요.")
    private String name;

    @NotNull(message = "가격을 입력하세요.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    @Max(value = 100_000_000, message = "가격이 너무 큽니다.")
    private Integer price;

    private Long categoryId;

    private String description;

    @NotNull(message = "재고를 입력하세요.")
    @Min(value = 0, message = "재고는 0 이상이어야 합니다.")
    private Integer stock = 0;

    private ProductStatus status = ProductStatus.ON_SALE;

    private boolean featured;
    private boolean newArrival;
    private boolean best;

    /** 한 줄에 하나: "이름=#색상코드" (예: 블랙=#23252B) */
    private String colors;

    /** 쉼표 구분: S,M,L */
    private String sizes;

    private MultipartFile thumbnailFile;
    private MultipartFile[] imageFiles;
}
