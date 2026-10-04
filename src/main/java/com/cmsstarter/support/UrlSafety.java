package com.cmsstarter.support;

/** 관리자가 입력한 링크 검증. 사이트 내부 경로 또는 http(s) 만 허용한다. */
public final class UrlSafety {

    private UrlSafety() {
    }

    /** @return 안전한 URL, 허용되지 않으면 null */
    public static String safeUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        String u = url.trim();
        boolean internal = u.startsWith("/") && !u.startsWith("//");
        boolean external = u.startsWith("http://") || u.startsWith("https://");
        return internal || external ? u : null;
    }
}
