CREATE TABLE member (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    name          VARCHAR(60)  NOT NULL,
    phone         VARCHAR(30),
    role          VARCHAR(20)  NOT NULL,
    created_at    DATETIME(6)  NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE category (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(60)  NOT NULL,
    slug       VARCHAR(80)  NOT NULL UNIQUE,
    sort_order INT          NOT NULL DEFAULT 0,
    visible    BIT          NOT NULL DEFAULT 1
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE product (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT       NULL,
    name        VARCHAR(150) NOT NULL,
    price       INT          NOT NULL,
    description TEXT,
    thumbnail   VARCHAR(255),
    stock       INT          NOT NULL DEFAULT 0,
    status      VARCHAR(20)  NOT NULL,
    featured    BIT          NOT NULL DEFAULT 0,
    is_new      BIT          NOT NULL DEFAULT 0,
    is_best     BIT          NOT NULL DEFAULT 0,
    created_at  DATETIME(6)  NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE SET NULL,
    INDEX idx_product_status (status),
    INDEX idx_product_category (category_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE product_image (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT       NOT NULL,
    path       VARCHAR(255) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_pimg_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- type: COLOR | SIZE. value: 표시값, color_hex: 색상 스와치용
CREATE TABLE product_option (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT      NOT NULL,
    type       VARCHAR(10) NOT NULL,
    value      VARCHAR(60) NOT NULL,
    color_hex  VARCHAR(9),
    sort_order INT         NOT NULL DEFAULT 0,
    CONSTRAINT fk_popt_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE cart_item (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT      NOT NULL,
    product_id BIGINT      NOT NULL,
    color      VARCHAR(60),
    size       VARCHAR(60),
    quantity   INT         NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_cart_member FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE orders (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no       VARCHAR(40)  NOT NULL UNIQUE,
    member_id      BIGINT       NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    items_total    INT          NOT NULL,
    shipping_fee   INT          NOT NULL,
    total          INT          NOT NULL,
    pay_method     VARCHAR(20)  NOT NULL,
    imp_uid        VARCHAR(80),
    receiver_name  VARCHAR(60)  NOT NULL,
    receiver_phone VARCHAR(30)  NOT NULL,
    zipcode        VARCHAR(10),
    address        VARCHAR(255) NOT NULL,
    memo           VARCHAR(255),
    created_at     DATETIME(6)  NOT NULL,
    paid_at        DATETIME(6),
    CONSTRAINT fk_orders_member FOREIGN KEY (member_id) REFERENCES member (id),
    INDEX idx_orders_status (status),
    INDEX idx_orders_created (created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE order_item (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT       NOT NULL,
    product_id   BIGINT       NULL,
    product_name VARCHAR(150) NOT NULL,
    thumbnail    VARCHAR(255),
    color        VARCHAR(60),
    size         VARCHAR(60),
    unit_price   INT          NOT NULL,
    quantity     INT          NOT NULL,
    CONSTRAINT fk_oitem_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE banner (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    eyebrow    VARCHAR(80),
    title      VARCHAR(150) NOT NULL,
    subtitle   VARCHAR(255),
    cta_label  VARCHAR(40),
    cta_url    VARCHAR(255),
    image      VARCHAR(255),
    sort_order INT          NOT NULL DEFAULT 0,
    visible    BIT          NOT NULL DEFAULT 1
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- 메인 섹션 블록. type: HERO | FEATURED | NEW | BEST | CATEGORY | EVENT
CREATE TABLE home_section (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    type       VARCHAR(20)  NOT NULL,
    title      VARCHAR(100) NOT NULL,
    sort_order INT          NOT NULL DEFAULT 0,
    visible    BIT          NOT NULL DEFAULT 1
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE site_setting (
    setting_key   VARCHAR(60)  PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

INSERT INTO site_setting (setting_key, setting_value) VALUES
    ('hero.preset', 'BANNER'),
    ('grid.columns', '4');

INSERT INTO home_section (type, title, sort_order, visible) VALUES
    ('HERO', '메인 배너', 1, 1),
    ('FEATURED', '추천 상품', 2, 1),
    ('CATEGORY', '카테고리', 3, 1),
    ('NEW', '신상품', 4, 1),
    ('BEST', '베스트', 5, 1),
    ('EVENT', '이벤트', 6, 0);
