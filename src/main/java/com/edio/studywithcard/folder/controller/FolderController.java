package com.edio.studywithcard.folder.controller;

import com.edio.common.security.CustomUserDetails;
import com.edio.studywithcard.folder.model.request.FolderCreateRequest;
import com.edio.studywithcard.folder.model.request.FolderMoveRequest;
import com.edio.studywithcard.folder.model.request.FolderUpdateRequest;
import com.edio.studywithcard.folder.model.response.AccountFolderResponse;
import com.edio.studywithcard.folder.model.response.FolderAllResponse;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.model.response.FolderWithDeckResponse;
import com.edio.studywithcard.folder.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FolderController implements FolderApiDoc {

    private final FolderService folderService;

    @GetMapping("/folder/all")
    @Override
    public FolderAllResponse getAllFolders(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long folderId
    ) {
        return folderService.getAllFolders(userDetails.getRootFolderId(), folderId);
    }

    @GetMapping("/folder")
    @Override
    public FolderWithDeckResponse getFolderWithDeck(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Long folderId
    ) {
        return folderService.getFolderWithDeck(userDetails.getRootFolderId(), folderId);
    }

    @GetMapping("/folder/my-folders")
    @Override
    public List<AccountFolderResponse> getUserFolders(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return folderService.getAccountFolders(userDetails.getAccountId());
    }

    @PostMapping("/folder")
    @Override
    public FolderResponse createFolder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody FolderCreateRequest folderCreateRequest) {
        return folderService.createFolder(userDetails.getAccountId(), folderCreateRequest);
    }

    @PatchMapping("/folder/{id}")
    @Override
    public void updateFolder(@PathVariable Long id, @RequestBody FolderUpdateRequest folderUpdateRequest) {
        folderService.updateFolder(id, folderUpdateRequest);
    }

    @PatchMapping("/folder/{id}/position")
    @Override
    public void moveFolder(@PathVariable Long id, @RequestBody FolderMoveRequest folderMoveRequest) {
        folderService.moveFolder(id, folderMoveRequest.parentId());
    }

    @DeleteMapping("/folder/{id}")
    @Override
    public void deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }
}
