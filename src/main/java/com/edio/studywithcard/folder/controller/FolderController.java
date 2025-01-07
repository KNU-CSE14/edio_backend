package com.edio.studywithcard.folder.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.common.security.CustomUserDetails;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderMoveRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.AccountFolderResponse;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;
import com.edio.studywithcard.folder.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Folder", description = "Folder 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    /**
     * @param userDetails 사용자 ID
     * @param folderId    조회 기준 폴더 ID
     * @return
     */
    // FIXME: 폴더 조회 시 하위 전체 조회되도록 변경 예정 
    @GetMapping("/folder")
    @Operation(summary = "Folder 조회", description = "Folder를 조회합니다.")
    public FolderWithDeckResponse getFolderWithDeck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long folderId
    ) {
        return folderService.getFolderWithDeck(userDetails.getAccountId(), folderId);
    }

    /**
     * @param userDetails 사용자 ID
     * @return
     */
    @GetMapping("/folder/my-folders")
    @Operation(summary = "사용자 Folder 목록 조회", description = "사용자 Folder 목록을 조회합니다.")
    public List<AccountFolderResponse> getUserFolders(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return folderService.getAccountFolders(userDetails.getAccountId());
    }

    /**
     * @param userDetails         사용자 ID
     * @param folderCreateRequest (parentId, name)
     * @return
     */
    @PostMapping("/folder")
    @Operation(summary = "Folder 등록", description = "Folder를 등록합니다.")
    public FolderResponse createFolder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FolderCreateRequest folderCreateRequest) {
        return folderService.createFolder(userDetails.getAccountId(), folderCreateRequest);
    }

    /**
     * @param id                  folderId
     * @param folderUpdateRequest (name)
     */
    @PatchMapping("/folder/{id}")
    @Operation(summary = "Folder명 수정", description = "Folder명을 수정합니다.")
    public void updateFolder(@PathVariable Long id, @RequestBody FolderUpdateRequest folderUpdateRequest) {
        folderService.updateFolder(id, folderUpdateRequest);
    }

    /**
     * @param id                folderId
     * @param folderMoveRequest (parentId)
     */
    @PatchMapping("/folder/{id}/position")
    @Operation(summary = "Folder 이동", description = "Folder를 이동합니다.")
    public void moveFolder(@PathVariable Long id, @RequestBody FolderMoveRequest folderMoveRequest) {
        folderService.moveFolder(id, folderMoveRequest.parentId());
    }

    /**
     * @param id folderId
     */
    @DeleteMapping("/folder/{id}")
    @Operation(summary = "Folder 삭제", description = "Folder를 삭제합니다.")
    public void deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }
}
