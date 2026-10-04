package com.cmsstarter.domain.order;

public enum OrderStatus {
    PENDING("결제대기", "pending"),
    PAID("결제완료", "paid"),
    SHIPPING("배송중", "shipping"),
    DELIVERED("배송완료", "done"),
    CANCELED("취소", "canceled");

    private final String label;
    private final String css;

    OrderStatus(String label, String css) {
        this.label = label;
        this.css = css;
    }

    public String getLabel() {
        return label;
    }

    public String getCss() {
        return css;
    }
}
