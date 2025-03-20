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
import java.util.NoSuchElementException;

import static com.edio.common.TestConstants.User.ACCOUNT_IDS;
import static com.edio.common.TestConstants.Folder.FOLDER_NAMES;
import static com.edio.common.TestConstants.NON_EXISTENT_ID;
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
