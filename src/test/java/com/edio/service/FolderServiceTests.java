package com.edio.service.unit;

import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import com.edio.studywithcard.folder.service.FolderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    public void createFolder_whenFolderDoesNotExist_createsNewFolder() {
        // given
        Folder folder = Folder.builder()
                .accountId(1L)
                .name("Test Folder")
                .build();

        // when
        when(folderRepository.findByAccountIdAndNameAndStatus(folder.getAccountId(), folder.getName(),true))
                .thenReturn(Optional.empty());
        when(folderRepository.save(any(Folder.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        FolderResponse response = folderService.createFolder(folder);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Folder");
    }

    @Test
    public void createFolder_whenFolderExists_returnsExistingFolder() {
        // given
        Folder existingFolder = Folder.builder()
                .accountId(1L)
                .name("Test Folder")
                .build();

        when(folderRepository.findByAccountIdAndNameAndStatus(existingFolder.getAccountId(), existingFolder.getName(), true))
                .thenReturn(Optional.of(existingFolder));

        // when
        FolderResponse response = folderService.createFolder(existingFolder);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Folder");
    }

    @Test
    public void createFolder_whenNameIsNull_throwsException() {
        // given
        Folder folder = Folder.builder()
                .accountId(1L)
                .name(null)
                .build();

        // when, then
        assertThatThrownBy(() -> folderService.createFolder(folder))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("null");
    }

    @Test
    public void createFolder_whenSaveFails_throwsException() {
        // given
        Folder folder = Folder.builder()
                .accountId(1L)
                .name("Test Folder")
                .build();

        when(folderRepository.save(any(Folder.class))).thenThrow(new RuntimeException("Database error"));

        // when, then
        assertThatThrownBy(() -> folderService.createFolder(folder))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Database error");
    }
}
