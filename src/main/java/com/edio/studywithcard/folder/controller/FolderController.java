package com.edio.studywithcard.folder.controller;

import com.edio.common.model.response.SwaggerCommonResponses;
import com.edio.studywithcard.folder.domain.Folder;
import com.edio.studywithcard.folder.model.request.FolderRequest;
import com.edio.studywithcard.folder.model.response.FolderResponse;
import com.edio.studywithcard.folder.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Folder", description = "Folder 관련 API")
@SecurityRequirement(name = "bearerAuth")
@SwaggerCommonResponses
@RestController
@RequestMapping("/api")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @GetMapping("/folder")
    @Operation(summary = "Folder 정보 조회", description = "Folder 정보를 조회합니다.")
    public List<FolderResponse> getFolders(@Parameter(required = true, description = "사용자 ID") Long accountId){
        return folderService.findOneFolder(accountId);
    }

    @PostMapping("/folder")
    @Operation(summary = "Folder 등록", description = "Folder를 등록합니다.")
    public FolderResponse createFolder(@RequestBody FolderRequest folderRequest){
        return folderService.createFolder(folderRequest);
    }

    @PutMapping("/folder/{id}")
    @Operation(summary = "Folder명 수정", description = "Folder명을 수정합니다.")
    public void updateFolder(@PathVariable Long id, @RequestBody FolderRequest folderRequest) {
        folderService.updateFolder(id, folderRequest);
    }

    @DeleteMapping("/folder/{id}")
    @Operation(summary = "Folder 삭제", description = "Folder를 삭제합니다.")
    public void deleteFolder(@PathVariable Long id) {
        folderService.deleteFolder(id);
    }
}
