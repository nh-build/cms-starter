# cms-starter

외주 납품용 **CMS 쇼핑몰 스타터 템플릿**입니다. 레포를 복사한 뒤 브랜드·이미지·콘텐츠만 바꿔서 빠르게 납품하는 것을 목표로 합니다.

- **스택**: Java 21 · Spring Boot 3.5 · Spring Security · Spring Data JPA · Thymeleaf · MySQL 8 · Flyway · Gradle · Docker
- **스토어프론트**: 메인(섹션 블록) · 상품 목록(카테고리/정렬/검색) · 상품 상세(색상/사이즈) · 장바구니 · 주문/결제 · 회원가입/로그인 · 주문내역
- **관리자(CMS)** `/admin`: 대시보드 · 상품(CRUD+이미지+재고/상태) · 카테고리 · 배너 · **메인 레이아웃(드래그 정렬/표시 토글)** · 주문(목록/상태) · 회원(읽기 전용)
- **결제**: 포트원(아임포트) V1 **테스트 모드**. 서버에서 금액을 계산하고, 키가 있으면 결제 검증까지 수행합니다.

## 빠른 시작 (로컬)

```bash
cp .env.example .env          # 값을 채운다 (.env 는 커밋 금지)
docker compose up -d db       # MySQL (호스트 포트 3307)
./gradlew bootRun             # http://localhost:8081
```

- 스토어: http://localhost:8081
- 관리자: http://localhost:8081/admin/login — `.env` 의 `ADMIN_EMAIL` / `ADMIN_PASSWORD` 로 최초 1회 자동 생성됩니다. (`ADMIN_PASSWORD` 가 비어 있으면 생성하지 않음)
- 데모 상품/배너/카테고리는 `V2__seed_demo.sql` 이 넣습니다.

### Docker 로 전체 실행

```bash
cp .env.example .env
docker compose up -d --build   # app(8081) + mysql
```

업로드 이미지는 `uploads` 볼륨, DB 는 `db-data` 볼륨에 저장됩니다.

## 새 프로젝트에 적용하는 법 (납품 체크리스트)

1. **레포 복사** 후 `git init` 으로 새 히스토리를 시작하고 `.env` 를 만든다.
2. **브랜드** — `.env`(또는 `application.yml` 의 `brand.*`)만 바꾼다.

   | 키 | 설명 |
   |---|---|
   | `BRAND_NAME` | 사이트명 (로고가 없으면 텍스트 로고로 사용) |
   | `BRAND_LOGO_PATH` | 로고 이미지 경로. 파일은 `src/main/resources/static/brand/` 에 두고 `/brand/logo.svg` 처럼 지정 |
   | `BRAND_PRIMARY_COLOR` / `BRAND_BACKGROUND_COLOR` | 포인트색 / 배경색. 파생 색(연한 톤, hover)은 CSS 에서 자동 계산 |
   | `BRAND_FONT_FAMILY` / `brand.font-css-url` | 폰트 (기본 Pretendard CDN) |
   | `BRAND_TAGLINE`, `BRAND_COMPANY`, `BRAND_CONTACT_EMAIL` | 푸터 문구 |
   | `SHIPPING_FEE`, `FREE_SHIPPING_OVER` | 배송비 / 무료배송 기준 |

   값은 `<style>:root{--brand:…}</style>` 로 모든 페이지(스토어/관리자)에 주입됩니다. CSS 는 `var(--brand)` 만 참조하므로 색을 바꾸려고 CSS 를 수정할 일이 없습니다. 공통 디자인 토큰(둥근 정도, 선 색)은 `static/css/brand.css` 한 곳에 있습니다.
3. **콘텐츠** — 코드가 아니라 관리자에서 입력한다: 상품, 카테고리, 배너, 메인 섹션 순서/표시, 히어로 프리셋, 그리드 열 수.
4. **데모 데이터 제거** — 신규 DB 라면 `src/main/resources/db/migration/V2__seed_demo.sql` 을 비우거나 삭제한다. (이미 적용된 DB 에서는 파일을 수정하지 말고 새 `V3__...sql` 을 추가)
5. **환경별 설정** — DB 접속, 도메인 포트, 업로드 경로, 관리자 계정, 포트원 키는 모두 `.env` / 환경변수.
6. **결제 전환** — 포트원 가입 후 `PORTONE_IMP_CODE`, `PORTONE_PG`, `PORTONE_API_KEY`, `PORTONE_API_SECRET` 을 채우고 `PORTONE_VERIFY_ENABLED=true` 로 바꾼다.
7. **레이아웃 바꾸기** — 헤더/푸터/상품카드/페이지네이션은 `templates/fragments/layout.html`, 메인 섹션은 `templates/fragments/sections.html`, 관리자 공통은 `fragments/admin.html`. 새 섹션 타입은 `SectionType` enum + `sections.html` 의 fragment + `shop/home.html` 의 case 한 줄이면 추가된다.

## 메인 섹션 블록 (드래그 레이아웃)

`관리자 > 메인 레이아웃` 에서 블록(메인 배너, 추천 상품, 카테고리, 신상품, 베스트, 이벤트)을 SortableJS 로 드래그해 순서를 바꾸고, 스위치로 표시/숨김을 전환합니다. 변경은 즉시 `home_section` 테이블에 저장되고 스토어프론트 메인이 그대로 렌더링합니다.
같은 화면에서 **히어로 프리셋**(가로 배너 / 슬라이드 / 없음)과 **상품 그리드 열 수**(2/3/4단)를 고릅니다 (`site_setting` 테이블).

## 한글 설정값 주의

로컬 `./gradlew bootRun` 은 `.env` 를 `.properties` 형식(ISO-8859-1)으로 읽기 때문에 **`.env` 에 한글을 넣으면 깨집니다.** 한글 문구(`BRAND_TAGLINE`, `ADMIN_NAME` 등)는 `application.yml` 의 기본값을 직접 수정하세요. (Docker `env_file`/IDE 환경변수로 주입하면 UTF-8 로 정상 동작합니다.)

## 보안 메모

- 비밀번호는 BCrypt 해시로만 저장합니다. 시크릿(DB, 관리자, 포트원)은 `.env` 로만 관리하며 `.gitignore` 에 포함돼 있습니다. **`.env.example` 에는 가짜 값만 넣으세요.**
- `/admin/**` 는 `ROLE_ADMIN` 만 접근. CSRF 보호가 켜져 있고 드래그 정렬 API 도 토큰을 헤더로 보냅니다.
- 업로드는 확장자/Content-Type 검증 후 UUID 파일명으로 저장합니다.
- ⚠️ `PORTONE_VERIFY_ENABLED=false`(기본)에서는 클라이언트가 보낸 `imp_uid` 를 서버가 포트원에 조회하지 않고 신뢰합니다. **개발/데모 전용**이며, 운영에서는 반드시 `true` 로 켜세요. (주문 금액은 항상 서버가 계산합니다.)

## 프로젝트 구조

```
src/main/java/com/cmsstarter/
  config/    BrandProperties, SecurityConfig(admin/shop 2개 체인), AdminSeeder, GlobalModelAdvice
  domain/    member · catalog · cart · order · content   (엔티티/리포지토리/서비스)
  web/       shop/ (스토어프론트), admin/ (CMS)
  support/   FileStorage (이미지 업로드)
src/main/resources/
  db/migration/   V1__init.sql (스키마) · V2__seed_demo.sql (데모 데이터)
  templates/      fragments/ · shop/ · admin/
  static/         css/ · js/ · brand/
```

## 테스트

```bash
./gradlew test
```

## 로드맵 / 알려진 한계

- 상품 옵션별 재고는 없고 상품 단위 재고만 관리합니다.
- 비회원 장바구니/주문 없음 (로그인 필요).
- 환불은 주문 상태를 '취소'로 바꾸면 재고만 복구하며, 포트원 결제 취소는 포트원 콘솔에서 처리합니다.
- 업로드 이미지는 로컬 디스크(Docker 볼륨)에 저장합니다. S3 등으로 바꾸려면 `FileStorage` 만 교체하면 됩니다.
