package com.cmsstarter.domain.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutForm {

    @NotBlank(message = "받는 분 이름을 입력하세요.")
    @Size(max = 60)
    private String receiverName;

    @NotBlank(message = "연락처를 입력하세요.")
    @Size(max = 30)
    private String receiverPhone;

    @Size(max = 10)
    private String zipcode;

    @NotBlank(message = "배송지 주소를 입력하세요.")
    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String memo;

    @Pattern(regexp = "CARD|TRANS|KAKAO", message = "결제수단을 선택하세요.")
    private String payMethod = "CARD";
}
