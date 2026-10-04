package com.cmsstarter.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UrlSafetyTest {

    @Test
    void allowsInternalPathsAndHttpUrls() {
        assertThat(UrlSafety.safeUrl("/products?sort=new")).isEqualTo("/products?sort=new");
        assertThat(UrlSafety.safeUrl("/#event")).isEqualTo("/#event");
        assertThat(UrlSafety.safeUrl(" https://example.com/a ")).isEqualTo("https://example.com/a");
        assertThat(UrlSafety.safeUrl("http://example.com")).isEqualTo("http://example.com");
    }

    @Test
    void blocksDangerousOrBlankValues() {
        assertThat(UrlSafety.safeUrl("javascript:alert(1)")).isNull();
        assertThat(UrlSafety.safeUrl("data:text/html,x")).isNull();
        assertThat(UrlSafety.safeUrl("//evil.example.com")).isNull();
        assertThat(UrlSafety.safeUrl("products")).isNull();
        assertThat(UrlSafety.safeUrl("  ")).isNull();
        assertThat(UrlSafety.safeUrl(null)).isNull();
    }
}
