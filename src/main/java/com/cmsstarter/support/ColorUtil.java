package com.cmsstarter.support;

import java.util.Locale;
import java.util.regex.Pattern;

/** 브랜드 색상 검증/대비 계산 (WCAG 상대 휘도 기준). */
public final class ColorUtil {

    private static final Pattern HEX = Pattern.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");
    public static final String INK = "#23252B";
    public static final String WHITE = "#FFFFFF";
    /** 배경색으로 허용하는 최소 휘도. 본문(어두운 글자)이 읽히려면 충분히 밝아야 한다. */
    public static final double MIN_BACKGROUND_LUMINANCE = 0.6;

    private ColorUtil() {
    }

    public static boolean isHex(String s) {
        return s != null && HEX.matcher(s.trim()).matches();
    }

    /** #abc → #AABBCC. 유효하지 않으면 null. */
    public static String normalize(String s) {
        if (!isHex(s)) {
            return null;
        }
        String h = s.trim().substring(1).toUpperCase(Locale.ROOT);
        if (h.length() == 3) {
            h = "" + h.charAt(0) + h.charAt(0) + h.charAt(1) + h.charAt(1) + h.charAt(2) + h.charAt(2);
        }
        return "#" + h;
    }

    public static double luminance(String hex) {
        String n = normalize(hex);
        if (n == null) {
            throw new IllegalArgumentException("올바른 색상 코드가 아닙니다: " + hex);
        }
        double r = channel(Integer.parseInt(n.substring(1, 3), 16));
        double g = channel(Integer.parseInt(n.substring(3, 5), 16));
        double b = channel(Integer.parseInt(n.substring(5, 7), 16));
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    /** 흰 글자를 쓸 수 있는 최소 대비(UI/큰 글자 기준 3:1). 기본 포인트색(#4E90C4)은 이 기준을 통과해 흰 글자를 유지한다. */
    private static final double MIN_CONTRAST_WITH_WHITE = 3.0;

    /** 배경색 위에 올릴 글자색. 흰색 대비가 충분하면 흰색, 아니면 어두운 색. */
    public static String onColor(String backgroundHex) {
        double withWhite = 1.05 / (luminance(backgroundHex) + 0.05);
        return withWhite >= MIN_CONTRAST_WITH_WHITE ? WHITE : INK;
    }

    public static boolean isLightEnoughForBackground(String hex) {
        return luminance(hex) >= MIN_BACKGROUND_LUMINANCE;
    }

    private static double channel(int v) {
        double c = v / 255.0;
        return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    }
}
