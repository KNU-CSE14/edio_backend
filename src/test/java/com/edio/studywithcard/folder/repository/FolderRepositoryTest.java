package com.edio.studywithcard.folder.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.folder.domain.Folder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.edio.common.TestConstants.Folder.FOLDER_NAMES;
import static com.edio.common.TestConstants.NON_EXISTENT_ID;
import static com.edio.common.TestConstants.User.ACCOUNT_IDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaConfig.class)
public class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    private Folder testFolder;
    private Folder testFolder2;
    private Folder testFolder3;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Given
        testFolder = folderRepository.save(Folder.builder()
                .accountId(ACCOUNT_IDS.get(0)) // 1L
                .name(FOLDER_NAMES.get(0))
                .build());
        testFolder2 = folderRepository.save(Folder.builder()
                .accountId(ACCOUNT_IDS.get(0)) // 1L
                .name(FOLDER_NAMES.get(1))
                .build());
        testFolder3 = folderRepository.save(Folder.builder()
                .accountId(ACCOUNT_IDS.get(1)) // 2L
                .name(FOLDER_NAMES.get(2))
                .build());
    }

    @Test
    @DisplayName("폴더 ID로 조회 -> (성공)")
    void saveAndFindFolder() {
        // When
        Folder folder = folderRepository.findByIdAndIsDeletedFalse(testFolder.getId())
                .orElseThrow();

        // Then
        assertThat(folder.getName()).isEqualTo(testFolder.getName());
        assertThat(folder.getAccountId()).isEqualTo(testFolder.getAccountId());
    }

    @Test
    @DisplayName("존재하지 않는 덱 ID로 조회 -> (실패)")
    void findFolderByNonExistentId() {
        // When & Then
        assertThatThrownBy(() ->
                folderRepository.findByIdAndIsDeletedFalse(NON_EXISTENT_ID)
                        .orElseThrow()
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("폴더 Soft Delete 동작 확인 -> (성공)")
    void softDeleteFolder() {
        // When
        folderRepository.delete(testFolder);
        entityManager.flush(); // DB에 반영
        entityManager.clear(); // 1차 캐시 초기화

        Optional<Folder> folder;
        // Then
        folder = folderRepository.findById(testFolder.getId());
        assertThat(folder).isPresent();
        assertThat(folder.get().isDeleted()).isTrue();

        // isDeleted = false 조건으로 조회
        folder = folderRepository.findByIdAndIsDeletedFalse(testFolder.getId());
        assertThat(folder).isEmpty();
    }

    @Test
    @DisplayName("사용자 ID로 조회 -> (성공)")
    void findByAccountId() {
        // Given
        folderRepository.saveAll(List.of(testFolder, testFolder2, testFolder3));

        // When
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(ACCOUNT_IDS.get(0));

        // Then
        assertThat(folders).hasSize(2);
        assertThat(folders).extracting(Folder::getName).containsExactlyInAnyOrder(testFolder.getName(), testFolder2.getName());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 -> (실패)")
    void findFoldersByNonExistentAccountId() {
        // Given
        folderRepository.saveAll(List.of(testFolder, testFolder2, testFolder3));

        // When
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(NON_EXISTENT_ID);

        // Then
        assertThat(folders).isEmpty();
    }
}
