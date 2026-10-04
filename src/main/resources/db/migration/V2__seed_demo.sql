-- 데모 데이터. 납품 프로젝트에서는 이 파일을 비우거나 삭제(신규 DB 기준)한다.
INSERT INTO category (name, slug, sort_order, visible) VALUES
    ('아우터', 'outer', 1, 1),
    ('니트', 'knit', 2, 1),
    ('팬츠', 'pants', 3, 1),
    ('액세서리', 'acc', 4, 1);

INSERT INTO product (category_id, name, price, description, stock, status, featured, is_new, is_best, created_at) VALUES
    ((SELECT id FROM category WHERE slug = 'outer'), '오버핏 울 코트', 129000, '부드러운 울 혼방 소재의 오버핏 코트입니다.', 42, 'ON_SALE', 1, 1, 1, NOW(6)),
    ((SELECT id FROM category WHERE slug = 'knit'), '베이직 니트', 39000, '어디에나 어울리는 데일리 니트입니다.', 0, 'SOLD_OUT', 1, 1, 1, NOW(6)),
    ((SELECT id FROM category WHERE slug = 'pants'), '슬랙스 팬츠', 49000, '깔끔한 핏의 슬랙스입니다.', 15, 'ON_SALE', 1, 0, 1, NOW(6)),
    ((SELECT id FROM category WHERE slug = 'acc'), '울 머플러', 29000, '따뜻한 울 머플러입니다.', 120, 'ON_SALE', 1, 1, 0, NOW(6));

INSERT INTO product_option (product_id, type, value, color_hex, sort_order)
SELECT p.id, 'COLOR', c.v, c.hex, c.o FROM product p
JOIN (SELECT '블랙' v, '#23252B' hex, 1 o UNION ALL SELECT '베이지', '#B8A88F', 2 UNION ALL SELECT '그레이', '#C9CDD6', 3) c;

INSERT INTO product_option (product_id, type, value, sort_order)
SELECT p.id, 'SIZE', s.v, s.o FROM product p
JOIN (SELECT 'S' v, 1 o UNION ALL SELECT 'M', 2 UNION ALL SELECT 'L', 3) s;

INSERT INTO banner (eyebrow, title, subtitle, cta_label, cta_url, sort_order, visible) VALUES
    ('NEW SEASON', '가을 신상 컬렉션', '올 가을, 가장 먼저 만나보세요', '지금 보기', '/products?sort=new', 1, 1),
    ('EVENT', '첫 구매 무료배송', '회원가입하고 혜택 받아가세요', '자세히 보기', '/products', 2, 1);
