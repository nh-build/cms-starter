-- 스토어프론트 상단 네비 메뉴. target_type: CATEGORY(카테고리 연결) | URL(직접 입력)
CREATE TABLE menu_item (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    label       VARCHAR(40)  NOT NULL,
    target_type VARCHAR(10)  NOT NULL,
    category_id BIGINT       NULL,
    url         VARCHAR(255) NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    visible     BIT          NOT NULL DEFAULT 1,
    CONSTRAINT fk_menu_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- 기존 하드코딩 네비와 동일한 기본 메뉴
INSERT INTO menu_item (label, target_type, url, sort_order, visible) VALUES
    ('전체상품', 'URL', '/products', 1, 1),
    ('신상품', 'URL', '/products?sort=new', 2, 1),
    ('베스트', 'URL', '/products?sort=best', 3, 1),
    ('이벤트', 'URL', '/#event', 4, 1);
