# edio_backend

> Edio 프로젝트 내 Backend 모듈

# 요구 사항

AI 기반 맞춤형 학습 지원 서비스 내 각종 API 제공

# 개발 환경

- Java 17
- spring-boot 3.3.4
    - spring-data-jpa
    - spring-security
    - oauth-client
- springdoc.swagger 2.6.0
- mysql

## 외부 주입 변수

**DB_USERNAME** : DB 접속 계정 정보  
**DB_PASSWORD** : DB 접속 비밀번호  
**DB_URL** : DB 접속 URL

**GOOGLE_CLIENT_ID** : google 로그인 관련 클라이언트 키
**GOOGLE_CLIENT_SECRET** : google 로그인 관련 시크릿 키

**JWT_SECRET** : JWT 토큰 발급용 해시 값

# API docs

[Swagger](http://ec2-43-203-169-54.ap-northeast-2.compute.amazonaws.com:8080/swagger-ui/index.html)

# 스키마 구조

[스키마.drawio](https://drive.google.com/file/d/1lzDTaKRvREPghpfdLgRk63Ca7JFktPcs/view?usp=sharing)

```mermaid
erDiagram
    MEMBER {
        bigint id PK
        varchar email "이메일"
        varchar full_name "성명"
        varchar first_name "이름 (구글)"
        varchar last_name "성 (구글)"
        varchar profile_url "구글 프로필 사진 URL"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    ACCOUNT {
        bigint id PK
        bigint memeber_id FK "MEMBER"
        varchar login_id UK "계정 고유 식별키"
        varchar password "비밀번호"
        tinyint is_deleted "삭제 여부"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    FOLDER {
        bigint id PK
        bigint account_id FK "ACCOUNT"
        bigint parent_folder_id "부모 폴더 식별키"
        varchar name "폴더명"
        tinyint is_deleted "삭제 여부"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    DECK {
        bigint id PK
        bigint folder_id FK "FOLDER"
        bigint category_id FK "CATEGORY"
        varchar name "덱 이름"
        varchar description "덱 설명"
        varchar image_url "덱 이미지 URL"
        tinyint is_favorite "즐겨찾기 여부"
        tinyint is_deleted "삭제 여부"
        varchar deck_type "덱 타입 (공유 덱 / 본인 덱)"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    CARD {
        bigint id PK
        bigint deck_id FK "DECK"
        varchar name "카드명"
        varchar description "카드 설명"
        tinyint is_deleted "삭제 여부"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    ATTACHEMENT {
        bigint id PK
        varchar file_name "파일명"
        varchar file_type "파일 타입"
        varchar file_path "파일 경로"
        varchar file_size "파일 크기"
        varchar file_target "덱 or 카드"
        tinyint is_deleted "삭제 여부"
        tinyint is_shared "공유 여부"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    ATTACHMENT_DECK_TARGET {
        bigint id PK
        bigint deck_id FK "DECK"
        bigint attachment_id FK "ATTACHMENT"
    }

    ATTACHMENT_CARD_TARGET {
        bigint id PK
        bigint card_id FK "CARD"
        bigint attachment_id FK "ATTACHMENT"
    }

    CATEGORY {
        bigint id PK
        varchar category_name "카테고리명"
        tinyint is_deleted "삭제 여부"
        datetime created_at "생성 일자"
        datetime updated_at "수정 일자"
    }

    ACCOUNT ||--|| MEMBER: member_id
    FOLDER }o--|| ACCOUNT: acccount_id
    FOLDER }o--|o FOLDER: parent_folder_id
    DECK }|--|| FOLDER: folder_id
    DECK }o--|| ATTACHMENT_DECK_TARGET: deck_id
    DECK }|--|| CATEGORY: category_id
    CARD }|--|| DECK: deck_id
    CARD }o--|| ATTACHMENT_CARD_TARGET: card_id
    ATTACHEMENT }|--|o ATTACHMENT_CARD_TARGET: attachment_id
    ATTACHEMENT }|--|o ATTACHMENT_DECK_TARGET: attachment_id
```