package com.edio.studywithcard.folder.repository;

import com.edio.common.config.JpaConfig;
import com.edio.studywithcard.folder.domain.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaConfig.class)
public class FolderRepositoryTest {

    /**
     * 1. 폴더 저장 & 조회
     * 2. Soft Delete 후 데이터 유지 여부
     * 3. 사용자 ID로 폴더 목록 조회
     */

    @Autowired
    private FolderRepository folderRepository;

    private Folder testFolder;
    private Folder testFolder2;
    private Folder testFolder3;

    @BeforeEach
    void setUp(){
        testFolder = folderRepository.save(Folder.builder()
                .accountId(1L)
                .name("testFolder")
                .build());
        testFolder2 = folderRepository.save(Folder.builder()
                .accountId(1L)
                .name("testFolder2")
                .build());
        testFolder3 = folderRepository.save(Folder.builder()
                .accountId(2L)
                .name("testFolder3")
                .build());
    }

    @Test
    @DisplayName("Save And FindFolder -> (성공)")
    void saveAndFindFolder(){
        // Given
        folderRepository.save(testFolder);

        // When
        Optional<Folder> findFolder = folderRepository.findByIdAndIsDeletedFalse(testFolder.getId());

        // Then
        assertThat(findFolder).isPresent();
        assertThat(findFolder.get().getName()).isEqualTo("testFolder");
        assertThat(findFolder.get().getAccountId()).isEqualTo(1L);
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
    @DisplayName("Find By AccountId -> (성공)")
    void findByAccountId() {
        // Given
        folderRepository.save(testFolder);
        folderRepository.save(testFolder2);
        folderRepository.save(testFolder3);

        // When
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeletedFalse(1L);

        // Then
        assertThat(folders).hasSize(2);
        assertThat(folders).extracting(Folder::getName).containsExactlyInAnyOrder("testFolder", "testFolder2");
    }

}
