package com.edio.studywithcard.folder.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.common.security.CustomUserDetails;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderMoveRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;
import com.edio.studywithcard.folder.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Folder", description = "Folder 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController {

    private final FolderService folderService;

    @Operation(summary = "Folder 조회", description = "Folder를 조회합니다.")
    @GetMapping("/folder")
    public FolderWithDeckResponse getFolderWithDeck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long folderId
    ) {
        return folderService.getFolderWithDeck(userDetails.getAccountId(), folderId);
    }

    @PostMapping("/folder")
    @Operation(summary = "Folder 등록", description = "Folder를 등록합니다.")
    public FolderResponse createFolder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FolderCreateRequest folderCreateRequest) {
        return folderService.createFolder(userDetails.getAccountId(), folderCreateRequest);
    }

    @PatchMapping("/folder/{id}")
    @Operation(summary = "Folder명 수정", description = "Folder명을 수정합니다.")
    public void updateFolder(@PathVariable Long id, @RequestBody FolderUpdateRequest folderUpdateRequest) {
        folderService.updateFolder(id, folderUpdateRequest);
    }

    @PatchMapping("/folder/{id}/move")
    @Operation(summary = "Folder 이동", description = "Folder를 이동합니다.")
    public void moveFolder(@PathVariable Long id, @RequestBody FolderMoveRequest folderMoveRequest) {
        folderService.moveFolder(id, folderMoveRequest.getParentId());
    }

    @DeleteMapping("/folder/{id}")
    @Operation(summary = "Folder 삭제", description = "Folder를 삭제합니다.")
    public void deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }
}
