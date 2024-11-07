package com.edio.studywithcard.folder.service;

import com.edio.common.exception.NotFoundException;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.repository.FolderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements FolderService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FolderRepository folderRepository;

    public FolderServiceImpl(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    /*
       Folder 조회
    */
    @Transactional(readOnly = true)
    @Override
    public List<FolderResponse> findOneFolder(Long accountId) {
        List<Folder> folders = folderRepository.findByAccountIdAndIsDeleted(accountId, false);

        // 날짜 내림차순
        folders.sort((f1, f2) -> f2.getUpdatedAt().compareTo(f1.getUpdatedAt()));

        // Folder ID를 기준으로 FolderResponse를 매핑
        Map<Long, FolderResponse> folderMap = folders.stream()
                .collect(Collectors.toMap(Folder::getId, FolderResponse::from));

        // 최상위 폴더들을 저장할 리스트
        List<FolderResponse> rootFolders = new ArrayList<>();

        // 폴더 계층 구조를 구성
        for (Folder folder : folders) {
            FolderResponse folderResponse = folderMap.get(folder.getId());
            if (folder.getParentId() == null) {
                // 최상위 폴더인 경우
                rootFolders.add(folderResponse);
            } else {
                // 하위 폴더인 경우, 부모 폴더의 children 리스트에 추가
                FolderResponse parentFolder = folderMap.get(folder.getParentId());
                if (parentFolder != null) {
                    parentFolder.getChildren().add(folderResponse); // children 리스트에 추가
                }
            }
        }
        return rootFolders;
    }

    /*
        Folder 등록
     */
    @Override
    @Transactional
    public FolderResponse createFolder(FolderRequest folderRequest) {
        Folder savedFolder = folderRepository.findByAccountIdAndNameAndIsDeleted(folderRequest.getAccountId(), folderRequest.getName(), false)
                .orElseGet(() -> {
                    Folder newFolder = Folder.builder()
                            .accountId(folderRequest.getAccountId())
                            .parentId(folderRequest.getParentId())
                            .name(folderRequest.getName())
                            .build();
                    return folderRepository.save(newFolder);
                });
        return FolderResponse.from(savedFolder);
    }

    /*
        Folder 수정
     */
    @Override
    @Transactional
    public void updateFolder(Long id, FolderRequest folderRequest) {
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.updateFields(folderRequest.getName(), folderRequest.getParentId());

        folderRepository.save(existingFolder);
    }

    /*
        Folder 삭제
     */
    @Override
    @Transactional
    public void deleteFolder(Long id) {
        Folder existingFolder = folderRepository.findByIdAndIsDeleted(id, false)
                .orElseThrow(() -> new NotFoundException(Folder.class, id));

        existingFolder.deleteeFields(false);

        folderRepository.save(existingFolder);
    }
}
