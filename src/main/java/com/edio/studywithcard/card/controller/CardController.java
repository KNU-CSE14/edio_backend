package com.edio.studywithcard.card.controller;

import com.edio.studywithcard.card.model.request.CardCreateOrUpdateRequest;
import com.edio.studywithcard.card.model.request.CardCreateRequest;
import com.edio.studywithcard.card.model.request.CardDeleteRequest;
import com.edio.studywithcard.card.model.request.CardUpdateRequest;
import com.edio.studywithcard.card.model.response.CardResponse;
import com.edio.studywithcard.card.service.CardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CardController implements CardApiDoc {

    private final CardService cardService;

    private final ObjectMapper objectMapper;

    @PostMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public CardResponse createCard(@RequestPart CardCreateRequest request,
                                   @RequestPart(value = "file", required = false) MultipartFile[] files) {
        return cardService.createCard(request, files);
    }

    @PatchMapping(value = "/card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public void updateCard(@RequestPart CardUpdateRequest request,
                           @RequestPart(value = "file", required = false) MultipartFile[] files) {
        cardService.updateCard(request, files);
    }

    @DeleteMapping("/card")
    @Override
    public void deleteCard(@RequestBody CardDeleteRequest request) {
        cardService.deleteCard(request);
    }

    /*
        다중 처리를 위한 테스트 API
     */
    @PostMapping(value = "/card/multi", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public List<CardResponse> createOrUpdateCard(@RequestPart("request") String request,
                                                 @RequestParam MultiValueMap<String, MultipartFile> fileMap) {
        try {
            List<CardCreateOrUpdateRequest> requestList = objectMapper.readValue(request,
                    new TypeReference<List<CardCreateOrUpdateRequest>>() {
                    });

            for (int i = 0; i < requestList.size(); i++) {
                // 클라이언트는 첫 번째 요청 항목 파일들을 "files[0]"라는 이름으로 보냈다고 가정
                String key = "files[" + i + "]";
                List<MultipartFile> filesForItem = fileMap.get(key);

                if (filesForItem == null || filesForItem.isEmpty()) {
                    log.info("요청 항목 " + i + "에는 파일이 없습니다.");
                } else {
                    log.info("요청 항목 " + i + "에 첨부된 파일 개수: " + filesForItem.size());
                    for (MultipartFile file : filesForItem) {
                        // 각 파일 처리 로직 구현 (예: 파일 저장, 파일명 확인 등)
                        log.info("요청 항목 " + i + "의 파일명: " + file.getOriginalFilename());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
//        return cardService.createOrUpdateCard(requests, fileGroups);
    }
}
