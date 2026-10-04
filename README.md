# cms-starter

외주 납품용 **CMS 쇼핑몰 스타터 템플릿**입니다. 레포를 복사한 뒤 브랜드·이미지·콘텐츠만 바꿔서 빠르게 납품하는 것을 목표로 합니다.

- **스택**: Java 21 · Spring Boot 3.5 · Spring Security · Spring Data JPA · Thymeleaf · MySQL 8 · Flyway · Gradle · Docker
- **스토어프론트**: 메인(섹션 블록) · 상품 목록(카테고리/정렬/검색) · 상품 상세(색상/사이즈) · 장바구니 · 주문/결제 · 회원가입/로그인 · 주문내역 · 반응형(MENU 드로어)
- **관리자(CMS)** `/admin`: 대시보드 · 상품(CRUD+이미지+재고/상태) · 카테고리 · 배너 · 주문(목록/상태) · 회원(읽기 전용) · **디자인**: 메뉴 관리 / 메인 레이아웃 / 디자인 설정(사이트명·로고·색상·폰트)
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
2. **브랜드/디자인** — 코드 수정 없이 **관리자 > 디자인 > 디자인 설정**(`/admin/design`)에서 바꾼다.
   - 사이트명, 로고(PNG/JPG/WebP), 포인트색/배경색, 폰트(5종 프리셋)가 스토어와 관리자에 즉시 반영된다.
   - 처음 띄울 때의 **기본값**은 `.env`(또는 `application.yml` 의 `brand.*`)에서 정한다. DB 에 저장된 값이 있으면 그 값이 우선하고, "기본값으로 되돌리기" 를 누르면 DB 값을 지우고 기본값으로 돌아간다.

   | 기본값 키 | 설명 |
   |---|---|
   | `BRAND_NAME` | 사이트명 (로고가 없으면 텍스트 로고) |
   | `BRAND_LOGO_PATH` | 기본 로고 경로. 파일은 `src/main/resources/static/brand/` 에 두고 `/brand/logo.png` 처럼 지정 |
   | `BRAND_PRIMARY_COLOR` / `BRAND_BACKGROUND_COLOR` | 포인트색 / 배경색. 파생 색(연한 톤, hover)과 포인트색 위의 글자색(흰색/어두운 색)은 자동 계산 |
   | `BRAND_FONT_PRESET` | `PRETENDARD`(기본) · `NANUM_GOTHIC` · `NANUM_SQUARE_ROUND` · `NOTO_SANS_KR` · `IBM_PLEX_SANS_KR` |
   | `SHIPPING_FEE`, `FREE_SHIPPING_OVER` | 배송비 / 무료배송 기준 |
   | (`application.yml`) `brand.tagline/company/contact-email` | 푸터 문구 — 관리자 화면 대상이 아니므로 yml 에서 수정 |

   색상은 `<style>:root{--brand, --bg, --on-brand, --font-family}</style>` 로 모든 페이지에 주입된다. CSS 는 이 변수만 참조하므로 색·폰트를 바꾸려고 CSS 를 수정할 일이 없다. 공통 디자인 토큰(둥근 정도, 선 색)은 `static/css/brand.css` 한 곳에 있다.
3. **메뉴** — `관리자 > 디자인 > 메뉴 관리`. 상단 네비 항목을 추가/수정/삭제하고 드래그로 순서를, 스위치로 표시/숨김을 바꾼다. 연결 대상은 카테고리 선택 또는 직접 URL(`/` 로 시작하거나 `http(s)://`). 표시할 메뉴가 없으면 기본 메뉴(전체상품)가 나온다.
4. **메인 레이아웃** — `관리자 > 디자인 > 메인 레이아웃`. 섹션 블록 순서/표시, 히어로 프리셋(썸네일 선택), 상품 그리드 열 수를 클릭으로 바꾼다. (아래 참고)
5. **콘텐츠** — 상품, 카테고리, 배너는 관리자에서 입력한다. 코드를 건드리지 않는다.
6. **데모 데이터 제거** — 신규 DB 라면 `src/main/resources/db/migration/V2__seed_demo.sql` 을 비우거나 삭제한다. (이미 적용된 DB 에서는 파일을 수정하지 말고 새 `V4__...sql` 을 추가. `V3__menu.sql` 의 기본 메뉴 4개는 관리자에서 지우거나 바꾸면 된다)
7. **환경별 설정** — DB 접속, 포트, 업로드 경로, 관리자 계정, 포트원 키는 모두 `.env` / 환경변수.
8. **결제 전환** — 포트원 가입 후 `PORTONE_IMP_CODE`, `PORTONE_PG`, `PORTONE_API_KEY`, `PORTONE_API_SECRET` 을 채우고 `PORTONE_VERIFY_ENABLED=true` 로 바꾼다.
9. **템플릿 구조** — 헤더/푸터/상품카드/페이지네이션은 `templates/fragments/layout.html`, 헤더 네비와 MENU 드로어는 `fragments/nav.html`, 메인 섹션은 `fragments/sections.html`, 관리자 공통은 `fragments/admin.html`. 헤더 라벨(MENU/Search/Login/Bag 등)은 `messages.properties` 한 곳에서 바꾼다. 새 섹션 타입은 `SectionType` enum + `sections.html` 의 fragment + `shop/home.html` 의 case 한 줄이면 추가된다.

## 메인 레이아웃 (드래그 + 썸네일 선택)

`관리자 > 메인 레이아웃` 에서 블록(메인 배너, 추천 상품, 카테고리, 신상품, 베스트, 이벤트)을 SortableJS 로 드래그해 순서를 바꾸고, 스위치로 표시/숨김을 전환합니다. 클릭 즉시 `home_section` / `site_setting` 테이블에 저장되고 스토어프론트 메인이 그대로 렌더링합니다.

| 히어로 프리셋 | 동작 |
|---|---|
| 가로 배너형 | 컨테이너 안에 둥근 배너 1개 (첫 번째 노출 배너) |
| **풀 이미지형** | 화면 폭 전체 + 화면 높이만큼 큰 이미지(첫 번째 노출 배너). **헤더가 이미지 위에 투명하게 겹침** (왼쪽 MENU · 가운데 로고 · 오른쪽 Search/Login/Bag), 스크롤하면 흰 배경으로 전환 |
| 슬라이드형 | 노출 중인 모든 배너를 자동/수동 슬라이드 |
| 배너 없음 | 히어로 영역 없음 |

- **풀 이미지형 옵션**: 헤더 글자색(밝게/어둡게 — 밝게일 때는 상단에 옅은 그라데이션 스크림 자동 적용), 히어로 문구 표시/숨김. 이미지와 문구는 `배너 관리` 에서 등록·교체한다. 권장 이미지: 가로 2400px 이상, 16:10, 5MB 이하.
- 헤더 오버레이는 **'메인 배너' 블록이 맨 위에 있을 때만** 적용된다 (아래로 내리면 일반 헤더로 돌아간다).
- 로고는 이미지 위에 그대로 놓이므로, 밝은 글자 헤더를 쓸 때는 흰색 로고를 올리면 잘 어울린다.
- 모바일(≤760px)에서는 MENU 버튼으로 여는 드로어에 메뉴·검색·계정 링크가 모인다.

## 한글 설정값 주의

로컬 `./gradlew bootRun` 은 `.env` 를 `.properties` 형식(ISO-8859-1)으로 읽기 때문에 **`.env` 에 한글을 넣으면 깨집니다.** 한글 문구(`BRAND_TAGLINE`, `ADMIN_NAME` 등)는 `application.yml` 의 기본값을 직접 수정하세요. (Docker `env_file`/IDE 환경변수로 주입하면 UTF-8 로 정상 동작합니다.)

## 보안 메모

- 비밀번호는 BCrypt 해시로만 저장합니다. 시크릿(DB, 관리자, 포트원)은 `.env` 로만 관리하며 `.gitignore` 에 포함돼 있습니다. **`.env.example` 에는 가짜 값만 넣으세요.**
- `/admin/**` 는 `ROLE_ADMIN` 만 접근. CSRF 보호가 켜져 있고 드래그 정렬 API 도 토큰을 헤더로 보냅니다.
- 업로드는 확장자/Content-Type 검증 후 UUID 파일명으로 저장합니다. (SVG 는 스크립트를 담을 수 있어 허용하지 않음)
- 메뉴/배너 링크는 `/` 로 시작하거나 `http(s)://` 만 허용하고(`javascript:` 차단), 브랜드 색상은 검증된 HEX 만 `<style>` 에 출력합니다.
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

- 디자인 설정은 메모리에 캐시되고 저장 시 즉시 갱신됩니다. **앱을 여러 대로 늘리면** 다른 인스턴스는 재시작/캐시 만료 정책이 필요합니다(현재는 단일 인스턴스 기준).
- 웹폰트는 외부 CDN(jsDelivr, Google Fonts)에서 로드합니다. CDN 로드에 실패하면 시스템 기본 폰트로 표시됩니다. 오프라인/사내망 배포라면 폰트 파일을 `static/` 으로 옮기고 `FontPreset` 의 URL 을 바꾸세요.
- 상품 옵션별 재고는 없고 상품 단위 재고만 관리합니다.
- 비회원 장바구니/주문 없음 (로그인 필요).
- 환불은 주문 상태를 '취소'로 바꾸면 재고만 복구하며, 포트원 결제 취소는 포트원 콘솔에서 처리합니다.
- 업로드 이미지는 로컬 디스크(Docker 볼륨)에 저장합니다. S3 등으로 바꾸려면 `FileStorage` 만 교체하면 됩니다.
