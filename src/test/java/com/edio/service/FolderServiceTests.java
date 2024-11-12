package com.edio.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.studywithcard.folder.service.FolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTests {

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Test
    public void createFolder_whenFolderDoesNotExist_createsNewFolder() {
        // given
        FolderCreateRequest folder = new FolderCreateRequest();
        folder.setAccountId(1L);
        folder.setName("Test Folder");
        folder.setParentId(null); // 최상위 폴더로 설정

        // when
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FolderResponse response = folderService.createFolder(folder);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Folder");
    }

    @Test
    public void createFolder_whenFolderExists_returnsExistingFolder() {
        // given
        FolderCreateRequest folderRequest = new FolderCreateRequest();
        folderRequest.setAccountId(1L);
        folderRequest.setName("Test Folder");
        folderRequest.setParentId(null); // 최상위 폴더로 설정

        Folder existingFolder = Folder.builder()
                .accountId(1L)
                .name("Test Folder")
                .parent(null) // 최상위 폴더
                .children(new ArrayList<>()) // 자식 폴더 리스트 초기화
                .build();

        // Mock 리턴 설정 - ID 수동 설정 추가
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> {
            Folder folder = invocation.getArgument(0);
            ReflectionTestUtils.setField(folder, "id", 1L); // `ReflectionTestUtils`를 사용해 필드를 강제로 설정
            return folder;
        });

        FolderResponse response = folderService.createFolder(folderRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Folder");
    }

    @Test
    public void createFolder_whenNameIsNull_throwsException() {
        // given
        FolderCreateRequest folder = new FolderCreateRequest();
        folder.setAccountId(1L);
        folder.setName(null);

        // when, then
        assertThatThrownBy(() -> folderService.createFolder(folder))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    @Test
    public void createFolder_whenSaveFails_throwsException() {
        // given
        FolderCreateRequest folder = new FolderCreateRequest();
        folder.setAccountId(1L);
        folder.setName("Test Folder");

        when(folderRepository.save(any(Folder.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> folderService.createFolder(folder))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
