package com.edio.studywithcard.folder.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.common.security.CustomUserDetails;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderMoveRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.AccountFolderResponse;
import com.edio.studywithcard.folder.model.response.FolderAllResponse;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Folder", description = "Folder 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
public interface FolderApiDoc {
    /**
     * @param userDetails 루트 폴더 ID
     * @param folderId    조회 기준 폴더 ID
     * @return
     */
    @Operation(summary = "Folder 전체 조회", description = "Folder 계층 전체를 조회합니다.")
    FolderAllResponse getAllFolders(CustomUserDetails userDetails, Long folderId);

    /**
     * @param userDetails 사용자 ID
     * @return
     */
    @Operation(summary = "사용자 Folder 목록 조회", description = "사용자 Folder 목록을 조회합니다.")
    List<AccountFolderResponse> getUserFolders(CustomUserDetails userDetails);

    /**
     * @param userDetails         사용자 ID
     * @param folderCreateRequest (parentId, name)
     * @return
     */
    @Operation(summary = "Folder 등록", description = "Folder를 등록합니다.")
    FolderResponse createFolder(CustomUserDetails userDetails, FolderCreateRequest folderCreateRequest);

    /**
     * @param id                  folderId
     * @param folderUpdateRequest (name)
     */
    @Operation(summary = "Folder명 수정", description = "Folder명을 수정합니다.")
    void updateFolder(Long id, FolderUpdateRequest folderUpdateRequest);

    /**
     * @param id                folderId
     * @param folderMoveRequest (parentId)
     */
    @Operation(summary = "Folder 이동", description = "Folder를 이동합니다.")
    void moveFolder(Long id, FolderMoveRequest folderMoveRequest);

    /**
     * @param id folderId
     */
    @Operation(summary = "Folder 삭제", description = "Folder를 삭제합니다.")
    void deleteFolder(Long id);
}
