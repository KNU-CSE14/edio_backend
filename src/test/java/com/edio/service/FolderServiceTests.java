package com.edio.service;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.studywithcard.folder.service.FolderServiceImpl;
import jakarta.persistence.EntityManager;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTests {

    @Mock
    private FolderRepository folderRepository;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Mock
    private EntityManager entityManager;

    // 공통 필드
    private FolderCreateRequest folderCreateRequest;
    private FolderUpdateRequest folderUpdateRequest;
    private Folder existingFolder;
    private Folder parentFolder;

    @BeforeEach
    public void setUp() {
        // 공통 데이터 초기화
        folderCreateRequest = new FolderCreateRequest();
        folderCreateRequest.setName("Test Folder");

        folderUpdateRequest = new FolderUpdateRequest();
        folderUpdateRequest.setName("Updated Folder Name");
        folderUpdateRequest.setParentId(2L);

        existingFolder = createFolder(1L, "Old Folder Name", null);
        parentFolder = createFolder(2L, "Parent Folder", null);
    }

    // 헬퍼 메서드: Folder 생성
    private Folder createFolder(Long id, String name, Folder parent) {
        Folder folder = Folder.builder()
                .accountId(1L)
                .name(name)
                .parentFolder(parent)
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(folder, "id", id);
        return folder;
    }

    // 헬퍼 메서드: Mock 설정
    private void mockFindFolder(Long folderId, Folder folder) {
        when(folderRepository.findByIdAndIsDeleted(folderId, false)).thenReturn(Optional.ofNullable(folder));
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
        assertThat(response.getName()).isEqualTo(folderCreateRequest.getName());
    }

    /*
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
                .parentFolder(null) // 최상위 폴더
                .childrenFolders(new ArrayList<>()) // 자식 폴더 리스트 초기화
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
     */

    @Test
    public void createFolder_whenNameIsNull_throwsException() {
        // given
        folderCreateRequest.setName(null);

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
        mockFindFolder(1L, existingFolder);
        when(entityManager.getReference(Folder.class, folderUpdateRequest.getParentId())).thenReturn(parentFolder);

        // when
        folderService.updateFolder(1L, folderUpdateRequest);

        // then
        assertThat(existingFolder.getName()).isEqualTo(folderUpdateRequest.getName());
    }

    @Test
    public void updateFolder_whenParentFolderDoesNotExist_throwsException() {
        // Mock 설정
        mockFindFolder(1L, existingFolder);
        doThrow(new EntityNotFoundException("Folder not found"))
                .when(entityManager).getReference(Folder.class, folderUpdateRequest.getParentId());

        // when, then
        assertThatThrownBy(() -> folderService.updateFolder(1L, folderUpdateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Folder not found");
    }

    /*
        DELETE
     */
    @Test
    public void deleteFolder_whenFolderExists_deletesFolder() {
        // Mock 설정
        mockFindFolder(1L, existingFolder);

        // when
        folderService.deleteFolder(1L);

        // then
        assertThat(existingFolder.isDeleted()).isTrue();
    }

    @Test
    public void deleteFolder_whenFolderDoesNotExist_throwsException() {
        // Mock 설정
        mockFindFolder(1L, null);

        // when, then
        assertThatThrownBy(() -> folderService.deleteFolder(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Folder not found");
    }
}
