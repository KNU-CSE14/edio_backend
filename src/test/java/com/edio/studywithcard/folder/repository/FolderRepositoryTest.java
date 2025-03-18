package com.edio.studywithcard.folder.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.folder.domain.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(JpaConfig.class)
public class FolderRepositoryTest {

    @Autowired
    private FolderRepository folderRepository;

    private static final List<Long> accountIds = List.of(1L, 2L);
    private static final List<String> folderNames = List.of("testFolder", "testFolder2", "testFolder3");
    private static final Long nonExistentId = 999L;
    private static final String notFoundMessage = "Folder not found";

    private Folder testFolder;
    private Folder testFolder2;
    private Folder testFolder3;

    @BeforeEach
    void setUp() {
        // Given
        testFolder = folderRepository.save(Folder.builder()
                .accountId(accountIds.get(0)) // 1L
                .name(folderNames.get(0))
                .build());
        testFolder2 = folderRepository.save(Folder.builder()
                .accountId(accountIds.get(0)) // 1L
                .name(folderNames.get(1))
                .build());
        testFolder3 = folderRepository.save(Folder.builder()
                .accountId(accountIds.get(1)) // 2L
                .name(folderNames.get(2))
                .build());
    }

    /**
     * 폴더 저장 & 조회
     */
    @Test
    @DisplayName("Save And FindFolder -> (성공)")
    void saveAndFindFolder() {
        // When
        Folder folder = folderRepository.findByIdAndIsDeletedFalse(testFolder.getId())
                .orElseThrow(() -> new AssertionError(notFoundMessage));

        // Then
        assertThat(folder.getName()).isEqualTo(testFolder.getName());
        assertThat(folder.getAccountId()).isEqualTo(testFolder.getAccountId());
    }

    /**
     * 존재하지 않는 폴더 조회 -> (실패)
     */
    @Test
    @DisplayName("FindFolder by Non-existent Id -> (실패)")
    void findFolderByNonExistentId() {
        // When & Then
        assertThrows(AssertionError.class, () -> {
            folderRepository.findByIdAndIsDeletedFalse(nonExistentId)
                    .orElseThrow(() -> new AssertionError(notFoundMessage));
        });
    }

    /**
     * Soft Delete 후 데이터 유지 여부
     */
    /*
        TODO: SQLDelete 사용한 Soft Delete 코드 merge 후 추가 테스트 예정
    @Test
    @DisplayName("Soft Delete And NotFoundFolder -> (성공)")
    void softDeleteFolder(){
        // Given
        folderRepository.save(testFolder);
        entityManager.flush();
        entityManager.clear();

        // When
        folderRepository.delete(testFolder);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Folder> deletedFolder = folderRepository.findByIdAndIsDeletedFalse(testFolder.getId());
        assertThat(deletedFolder).isEmpty();

        Long count = (Long) entityManager.createQuery(
                "SELECT COUNT(f) FROM folder f where f.id = :id")
                .setParameter("id", testFolder.getId())
                .getSingleResult();

        assertThat(count).isEqualTo(1);
    }
    */

    /**
     * 사용자 ID로 폴더 목록 조회
     */
    @Test
    @DisplayName("Find By AccountId -> (성공)")
    void findByAccountId() {
        // Given
        folderRepository.saveAll(List.of(testFolder, testFolder2, testFolder3));

        // When
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(accountIds.get(0));

        // Then
        assertThat(folders).hasSize(2);
        assertThat(folders).extracting(Folder::getName).containsExactlyInAnyOrder(testFolder.getName(), testFolder2.getName());
    }

    /**
     * 존재하지 않는 AccountId로 폴더 목록 조회 -> (실패)
     */
    @Test
    @DisplayName("Find Folders by Non-existent AccountId -> (실패)")
    void findFoldersByNonExistentAccountId() {
        // Given
        folderRepository.saveAll(List.of(testFolder, testFolder2, testFolder3));

        // When
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(nonExistentId);

        // Then
        assertThat(folders).isEmpty();
    }
}
