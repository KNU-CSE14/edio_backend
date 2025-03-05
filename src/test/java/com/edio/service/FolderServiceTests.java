package com.edio.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.studywithcard.folder.service.FolderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTests {

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderServiceImpl folderService;

    // 공통 필드
    private FolderCreateRequest folderCreateRequest;
    private FolderUpdateRequest folderUpdateRequest;
    private Folder existingFolder;
    private Folder parentFolder;

    @BeforeEach
    public void setUp() {
        // 공통 데이터 초기화
        folderCreateRequest = new FolderCreateRequest(null, "Test Folder");
        folderUpdateRequest = new FolderUpdateRequest("Updated Folder Name");
        existingFolder = createFolder(1L, "Old Folder Name");
        parentFolder = createFolder(2L, "Parent Folder");
    }

    // 헬퍼 메서드: Folder 생성
    private Folder createFolder(Long id, String name) {
        Folder folder = Folder.builder()
                .accountId(1L)
                .name(name)
                .parentFolder(null)
//                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(folder, "id", id);
        ReflectionTestUtils.setField(folder, "isDeleted", false);
        return folder;
    }

    // 헬퍼 메서드: Mock 설정
    private void mockFindFolder(Folder folder) {
        when(folderRepository.findByIdAndIsDeletedFalse(1L)).thenReturn(Optional.ofNullable(folder));
    }

    /*
        CREATE
     */
    @Test
    public void createFolder_whenFolderDoesNotExist_createsNewFolder() {
        // Mock 설정
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        FolderResponse response = folderService.createFolder(1L, folderCreateRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo(folderCreateRequest.name());
    }

    @Test
    public void createFolder_whenNameIsNull_throwsException() {
        // given
        folderCreateRequest = new FolderCreateRequest(null, null);

        // when, then
        assertThatThrownBy(() -> folderService.createFolder(1L, folderCreateRequest))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    /*
        UPDATE
     */
    @Test
    public void updateFolder_whenFolderExists_updatesFolder() {
        // Mock 설정
        mockFindFolder(existingFolder);

        // when
        folderService.updateFolder(1L, folderUpdateRequest);

        // then
        assertThat(existingFolder.getName()).isEqualTo(folderUpdateRequest.name());
    }


    /*
        DELETE
     */
    @Test
    public void deleteFolder_whenFolderExists_deletesFolder() {
        // Mock 설정
        mockFindFolder(existingFolder);

        // when
        folderService.deleteFolder(1L);

        verify(folderRepository, times(1)).findByIdAndIsDeletedFalse(1L);
        verify(folderRepository, times(1)).delete(existingFolder);
    }

    @Test
    public void deleteFolder_whenFolderDoesNotExist_throwsException() {
        // Mock 설정
        when(folderRepository.findByIdAndIsDeletedFalse(1L))
                .thenThrow(new EntityNotFoundException("Not Found with ID: 1"));

        // when, then
        assertThatThrownBy(() -> folderService.deleteFolder(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Not Found with ID: 1");
    }
}
