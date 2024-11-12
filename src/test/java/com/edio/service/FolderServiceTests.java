package com.edio.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
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
import java.util.List;
import java.util.Optional;

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

    /*
        CREATE
     */
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

    /*
        UPDATE
     */
    @Test
    public void updateFolder_whenFolderExists_updatesFolder() {
        // given
        Long folderId = 1L;
        FolderUpdateRequest updateRequest = new FolderUpdateRequest();
        updateRequest.setName("Updated Folder Name");
        updateRequest.setParentId(2L); // 부모 폴더 설정

        Folder existingFolder = Folder.builder()
                .accountId(1L)
                .name("Old Folder Name")
                .build();
        ReflectionTestUtils.setField(existingFolder, "id", folderId);

        Folder newParentFolder = Folder.builder()
                .accountId(1L)
                .name("New Parent Folder")
                .build();
        ReflectionTestUtils.setField(newParentFolder, "id", 2L);

        when(folderRepository.findByIdAndIsDeleted(folderId, false)).thenReturn(Optional.of(existingFolder));
        when(folderRepository.findById(updateRequest.getParentId())).thenReturn(Optional.of(newParentFolder));

        // when
        folderService.updateFolder(folderId, updateRequest);

        // then
        assertThat(existingFolder.getName()).isEqualTo("Updated Folder Name");
        assertThat(existingFolder.getParent()).isEqualTo(newParentFolder);
    }

    @Test
    public void updateFolder_whenParentFolderIsNull_updatesToNoParent() {
        // given
        Long folderId = 1L;
        FolderUpdateRequest updateRequest = new FolderUpdateRequest();
        updateRequest.setName("Updated Folder Name");
        updateRequest.setParentId(null); // 부모 폴더 해제

        Folder existingFolder = Folder.builder()
                .accountId(1L)
                .name("Old Folder Name")
                .parent(Folder.builder().name("Old Parent Folder").build()) // 기존 부모 폴더 설정
                .build();
        ReflectionTestUtils.setField(existingFolder, "id", folderId);

        when(folderRepository.findByIdAndIsDeleted(folderId, false)).thenReturn(Optional.of(existingFolder));

        // when
        folderService.updateFolder(folderId, updateRequest);

        // then
        assertThat(existingFolder.getName()).isEqualTo("Updated Folder Name");
        assertThat(existingFolder.getParent()).isNull();
    }

    @Test
    public void updateFolder_whenParentFolderDoesNotExist_throwsException() {
        // given
        Long folderId = 1L;
        FolderUpdateRequest updateRequest = new FolderUpdateRequest();
        updateRequest.setName("Updated Folder Name");
        updateRequest.setParentId(2L); // 존재하지 않는 부모 폴더 ID 설정

        Folder existingFolder = Folder.builder()
                .accountId(1L)
                .name("Old Folder Name")
                .build();
        ReflectionTestUtils.setField(existingFolder, "id", folderId);

        when(folderRepository.findByIdAndIsDeleted(folderId, false)).thenReturn(Optional.of(existingFolder));
        when(folderRepository.findById(updateRequest.getParentId())).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> folderService.updateFolder(folderId, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Folder not found");
    }

    /*
        DELETE
     */
    @Test
    public void deleteFolder_whenFolderExists_deletesFolder() {
        // given
        Long folderId = 1L;
        Folder existingFolder = Folder.builder()
                .accountId(1L)
                .name("Folder to be deleted")
                .build();
        ReflectionTestUtils.setField(existingFolder, "id", folderId);

        when(folderRepository.findByIdAndIsDeleted(folderId, false)).thenReturn(Optional.of(existingFolder));

        // when
        folderService.deleteFolder(folderId);

        // then
        assertThat(existingFolder.isDeleted()).isTrue();
    }

    @Test
    public void deleteFolder_whenFolderDoesNotExist_throwsException() {
        // given
        Long folderId = 1L;

        when(folderRepository.findByIdAndIsDeleted(folderId, false)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> folderService.deleteFolder(folderId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Folder not found");
    }

    /*
        SELECT
     */
    @Test
    public void getFolder_whenFolderExists_returnsFolder() {
        // given
        Long accountId = 1L;
        Folder rootFolder = Folder.builder()
                .accountId(accountId)
                .name("Root Folder")
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(rootFolder, "id", 1L);

        Folder childFolder = Folder.builder()
                .accountId(accountId)
                .name("Child Folder")
                .parent(rootFolder)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(childFolder, "id", 2L);
        rootFolder.getChildren().add(childFolder);

        List<Folder> rootFolders = List.of(rootFolder);
        when(folderRepository.findAllByAccountIdAndParentIsNullAndIsDeleted(accountId, false)).thenReturn(rootFolders);

        // when
        List<FolderResponse> response = folderService.findOneFolder(accountId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(1);
        assertThat(response.get(0).getName()).isEqualTo("Root Folder");
        assertThat(response.get(0).getChildren().size()).isEqualTo(1);
        assertThat(response.get(0).getChildren().get(0).getName()).isEqualTo("Child Folder");
    }
}
