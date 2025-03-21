package com.edio.studywithcard.folder.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static com.edio.common.TestConstants.User.ACCOUNT_ID;
import static com.edio.common.TestConstants.Folder.FOLDER_ID;
import static com.edio.common.TestConstants.Folder.FOLDER_NAMES;
import static com.edio.common.util.TestDataUtil.createFolder;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTest {

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderServiceImpl folderService;

    private FolderCreateRequest folderCreateRequest;
    private FolderUpdateRequest folderUpdateRequest;
    private Folder mockFolder;

    @BeforeEach
    public void setUp() {
        // 공통 데이터 초기화
        folderCreateRequest = new FolderCreateRequest(null, FOLDER_NAMES.get(0));
        folderUpdateRequest = new FolderUpdateRequest(FOLDER_NAMES.get(2));
        mockFolder = createFolder(FOLDER_ID, FOLDER_NAMES.get(0));
    }

    @Test
    @DisplayName("폴더 생성 및 검증 -> (성공)")
    void 폴더_생성_검증() {
        // Given
        when(folderRepository.save(any(Folder.class))).thenReturn(mockFolder);

        // When
        FolderResponse response = folderService.createFolder(ACCOUNT_ID, folderCreateRequest);

        // Then
        verify(folderRepository, times(1)).save(any(Folder.class));
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(folderCreateRequest.name());
    }

    @Test
    @DisplayName("폴더명이 NULL일 폴더 생성 검증 -> (실패)")
    void 폴더명_NULL_폴더_생성_검증() {
        // Given
        folderCreateRequest = new FolderCreateRequest(null, null);

        // When & Then
        Assertions.assertThatThrownBy(() ->
                folderService.createFolder(ACCOUNT_ID, folderCreateRequest)
        ).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("폴더 업데이트 검증 -> (성공)")
    void 폴더_업데이트_검증() {
        // Given
        when(folderRepository.findByIdAndIsDeletedFalse(FOLDER_ID)).thenReturn(Optional.ofNullable(mockFolder));

        // When
        folderService.updateFolder(FOLDER_ID, folderUpdateRequest);

        // Then
        verify(folderRepository, times(1)).findByIdAndIsDeletedFalse(FOLDER_ID);
        assertThat(mockFolder.getName()).isEqualTo(folderUpdateRequest.name());
    }

    @Test
    @DisplayName("폴더 삭제 및 검증 -> (성공)")
    void 폴더_삭제_검증() {
        // Given
        when(folderRepository.findByIdAndIsDeletedFalse(FOLDER_ID)).thenReturn(Optional.ofNullable(mockFolder));

        // When
        folderService.deleteFolder(FOLDER_ID);

        // Then
        verify(folderRepository, times(1)).findByIdAndIsDeletedFalse(FOLDER_ID);
        verify(folderRepository, times(1)).delete(mockFolder);
    }

    @Test
    @DisplayName("존재하지 않는 폴더 삭제 검증 -> (실패)")
    void 존재하지_않는_폴더_삭제_검증() {
        // When, Then
        assertThatThrownBy(() -> folderService.deleteFolder(FOLDER_ID))
                .isInstanceOf(NoSuchElementException.class);
    }
}
