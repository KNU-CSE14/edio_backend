package com.edio.common;

import com.edio.user.domain.enums.AccountLoginType;
import com.edio.user.domain.enums.AccountRole;

import java.util.List;

public final class TestConstants {
    private TestConstants() {
    }

    /**
     * 공통적으로 많이 쓰이는 기본 값
     */
    public static final Long DEFAULT_ID = 1L;
    public static final Long NON_EXISTENT_ID = 999L;
    public static final List<Long> NON_EXISTENT_IDS = List.of(999L, 1000L);
    public static final String DEFAULT_EMAIL = "testUser@example.com";
    public static final String NON_EXISTENT_LOGIN_ID = "nonexistent@gmail.com";
    public static final String DEFAULT_NAME = "Hong Gildong";
    public static final String DEFAULT_GIVEN_NAME = "Gildong";
    public static final String DEFAULT_FAMILY_NAME = "Hong";
    public static final String DEFAULT_PROFILE_URL = "http://example.com/profile.jpg";
    public static final String DEFAULT_PASSWORD = "oauth_password";
    public static final AccountRole DEFAULT_ROLE = AccountRole.ROLE_USER;
    public static final AccountLoginType DEFAULT_LOGIN_TYPE = AccountLoginType.GOOGLE;

    /**
     * 계정 관련 상수
     */
    public static class Account {
        public static final Long ACCOUNT_ID = DEFAULT_ID;
        public static final Long MEMBER_ID = DEFAULT_ID;
        public static final String EMAIL = DEFAULT_EMAIL;
        public static final String NAME = DEFAULT_NAME;
        public static final String GIVEN_NAME = DEFAULT_GIVEN_NAME;
        public static final String FAMILY_NAME = DEFAULT_FAMILY_NAME;
        public static final String PROFILE_URL = DEFAULT_PROFILE_URL;
        public static final String PASSWORD = DEFAULT_PASSWORD;
        public static final AccountRole ROLE = DEFAULT_ROLE;
        public static final AccountLoginType LOGIN_TYPE = DEFAULT_LOGIN_TYPE;
        public static final String INVALID_TOKEN = "invalid.token.value";
        public static final String NON_EXISTENT_LOGIN_ID = TestConstants.NON_EXISTENT_LOGIN_ID;
        public static final Long NON_EXISTENT_ID = TestConstants.NON_EXISTENT_ID;
        public static final List<Long> ACCOUNT_IDS = List.of(1L, 2L);
    }

    /**
     * 파일 관련 상수
     */
    public static class File {
        public static final String FILE_NAME = "test.jpg";
        public static final Long FILE_SIZE = 1024L;
        public static final String FILE_KEY = "image/test.jpg";
        public static final String FILE_PATH = "https://%s.s3.%s.amazonaws.com/%s";
        public static final String FILE_TYPE = "image/jpeg";
        public static final String FILE_TARGET = "CARD";
        public static final String FOLDER_TARGET = "IMAGE";
        public static final String S3_FOLDER_NAME = "image";
        public static final String BUCKET_NAME = "edio-file-bucket";
        public static final String REGION = "ap-northeast-2";
        public static final String MOCK_FILE_TYPE = "file";
        public static final boolean EMPTY_FLAG = false;
    }

    /*
        폴더 관련 상수
     */
    public static class Folder {
        public static final Long FOLDER_ID = DEFAULT_ID;
        public static final Long ROOT_FOLDER_ID = DEFAULT_ID;
        public static final String FOLDER_NAME = "Default";
        public static final List<String> FOLDER_NAMES = List.of("testFolder", "testFolder2", "testFolder3");
    }

    /*
        덱 관련 상수
     */
    public static class Deck {
        public static final Long DECK_ID = DEFAULT_ID;
        public static final String DECK_NAME = "testDeck";
        public static final String DECK_DESCRIPTION = "deckDescription";
        public static final List<String> DECK_NAMES = List.of("testDeck", "newDeck", "updateDeck");
        public static final List<String> DECK_DESCRIPTIONS = List.of("testDescription", "newDescription", "updateDescription");
        public static final boolean IS_SHARED = false;
        public static final boolean IS_FAVORITE = false;
    }

    /*
     * 카테고리 관련 상수
     */
    public static class Category {
        public static final Long CATEGORY_ID = DEFAULT_ID;
        public static final String CATEGORY_NAME = "testCategory";
    }

    /*
     * 카드 관련 상수
     */
    public static class Card {
        public static final Long CARD_ID = DEFAULT_ID;
        public static final List<String> CARD_NAMES = List.of("testCard", "testCard2");
        public static final List<String> CARD_DESCRIPTIONS = List.of("cardDescription", "cardDescription2");
    }

    /*
     * 첨부파일 관련 상수
     */
    public static class Attachment {
        public static final String IMAGE_MIME_JPEG = "image/jpeg";
        public static final String AUDIO_MIME_MPEG = "audio/mpeg";
        public static final String OLD_IMAGE_KEY = "oldImageKey";
        public static final String OLD_AUDIO_KEY = "oldAudioKey";
        public static final String ATTACHMENT_FIELD = "attachment";
        public static final String ATTACHMENT_CARD_TARGET_FIELD = "attachmentCardTargets";
    }
}


