package com.cmsstarter.domain.content;

import lombok.Value;

/** 헤더 네비에 그려질 최종 링크 */
@Value
public class NavLink {
    String label;
    String href;
}
