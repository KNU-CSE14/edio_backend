package com.edio.studywithcard.deck.controller;

import com.edio.studywithcard.deck.model.request.DeckCreateRequest;
import com.edio.studywithcard.deck.model.request.DeckMoveRequest;
import com.edio.studywithcard.deck.model.request.DeckUpdateRequest;
import com.edio.studywithcard.deck.model.response.DeckResponse;
import com.edio.studywithcard.deck.service.DeckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DeckController.class)
class DeckControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeckService deckService;

    @DisplayName("GET : " + DeckApiUrls.DECK_URL + " -> (성공)")
    @WithMockUser
    @Test
    void test_getDeck() throws Exception {
        DeckResponse expected = new DeckResponse(1L, 1L, 1L, false, "TestDeck", "etc", false, List.of(), List.of());

        given(deckService.getDeck(eq(expected.id()))).willReturn(expected);

        String response = mockMvc.perform(get(DeckApiUrls.DECK_URL)
                        .param("id", String.valueOf(expected.id()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        DeckResponse actual = objectMapper.readValue(response, DeckResponse.class);

        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);

        then(deckService).should().getDeck(eq(expected.id()));
    }

    @DisplayName("GET : " + DeckApiUrls.DECK_URL + " -> (Deck 조회 실패)")
    @WithMockUser
    @Test
    void test_getDeck_notFoundEntity() throws Exception {
        DeckResponse expected = new DeckResponse(1L, 1L, 1L, false, "TestDeck", "etc", false, List.of(), List.of());

        given(deckService.getDeck(eq(expected.id()))).willThrow(EntityNotFoundException.class);

        mockMvc.perform(get(DeckApiUrls.DECK_URL)
                        .param("id", String.valueOf(expected.id()))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(deckService).should().getDeck(eq(expected.id()));
    }

    @DisplayName("POST : " + DeckApiUrls.DECK_URL + " -> (성공)")
    @WithMockUser
    @ParameterizedTest
    @MethodSource("getCreateDeckTestParams")
    void test_createDeck(DeckCreateRequest request, MockMultipartFile file, DeckResponse expected) throws Exception {

        given(deckService.createDeck(eq(request), eq(file))).willReturn(expected);

        MockMultipartHttpServletRequestBuilder multipartReq = (MockMultipartHttpServletRequestBuilder) multipart(DeckApiUrls.DECK_URL)
                .part(new MockPart("request", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                .with(csrf());

        if (Objects.nonNull(file)) {
            multipartReq = multipartReq.file(file);
        }

        String response = mockMvc.perform(multipartReq)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        DeckResponse actual = objectMapper.readValue(response, DeckResponse.class);
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);

        then(deckService).should().createDeck(eq(request), eq(file));
    }

    @DisplayName("PATCH : " + DeckApiUrls.DECK_URL + " -> (성공)")
    @WithMockUser
    @Test
    void test_updateDeck() throws Exception {
        DeckUpdateRequest request = new DeckUpdateRequest(1L, 1L, null, "Test Deck", "Test desc", false);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        willDoNothing().given(deckService).updateDeck(eq(request), eq(file));

        mockMvc.perform(multipart(HttpMethod.PATCH, DeckApiUrls.DECK_URL)
                        .file(file)
                        .part(new MockPart("request", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        then(deckService).should().updateDeck(eq(request), eq(file));
    }

    @DisplayName("PATCH : " + DeckApiUrls.DECK_URL + " -> (Deck 조회 실패)")
    @WithMockUser
    @Test
    void test_updateDeck_notFoundEntity() throws Exception {
        DeckUpdateRequest request = new DeckUpdateRequest(1L, 1L, null, "Test Deck", "Test desc", false);
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        willThrow(EntityNotFoundException.class).given(deckService).updateDeck(eq(request), eq(file));

        mockMvc.perform(multipart(HttpMethod.PATCH, DeckApiUrls.DECK_URL)
                        .file(file)
                        .part(new MockPart("request", objectMapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8)))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(deckService).should().updateDeck(eq(request), eq(file));
    }

    @DisplayName("PATCH : " + DeckApiUrls.DECK_POSITION_URL + " -> (Deck 조회 실패)")
    @WithMockUser
    @Test
    void test_moveDeck() throws Exception {
        long deckId = 1L;
        long parentFolderId = 1L;
        DeckMoveRequest request = new DeckMoveRequest(deckId, parentFolderId);

        willDoNothing().given(deckService).moveDeck(eq(request));

        mockMvc.perform(patch(DeckApiUrls.DECK_POSITION_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        then(deckService).should().moveDeck(eq(request));
    }

    @DisplayName("PATCH : " + DeckApiUrls.DECK_POSITION_URL + " -> (Deck 조회 실패)")
    @WithMockUser
    @Test
    void test_moveDeck_notFoundEntity() throws Exception {
        long deckId = 1L;
        long parentFolderId = 1L;
        DeckMoveRequest request = new DeckMoveRequest(deckId, parentFolderId);

        willThrow(EntityNotFoundException.class).given(deckService).moveDeck(eq(request));

        mockMvc.perform(patch(DeckApiUrls.DECK_POSITION_URL)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(deckService).should().moveDeck(eq(request));
    }

    @DisplayName("DELETE : " + DeckApiUrls.DECK_DELETE_URL + " -> (Deck 조회 실패)")
    @WithMockUser
    @Test
    void test_deleteDeck() throws Exception {
        long deckId = 1L;

        willDoNothing().given(deckService).deleteDeck(deckId);

        mockMvc.perform(delete(DeckApiUrls.DECK_URL + "/" + deckId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());

        then(deckService).should().deleteDeck(deckId);
    }

    @DisplayName("DELETE : " + DeckApiUrls.DECK_DELETE_URL + " -> (Deck 조회 실패)")
    @WithMockUser
    @Test
    void test_deleteDeck_notFoundEntity() throws Exception {
        long deckId = 1L;

        willThrow(EntityNotFoundException.class).given(deckService).deleteDeck(deckId);

        mockMvc.perform(delete(DeckApiUrls.DECK_URL + "/" + deckId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());

        then(deckService).should().deleteDeck(deckId);
    }

    private static Stream<Arguments> getCreateDeckTestParams() {
        DeckResponse expected = new DeckResponse(1L, 1L, 1L, false, "TestDeck", "etc", false, List.of(), List.of());

        DeckCreateRequest request = new DeckCreateRequest(expected.folderId(), expected.categoryId(), expected.name(), expected.description(), expected.isShared());
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        return Stream.of(
                Arguments.of(request, file, expected),
                Arguments.of(request, null, expected)
        );
    }
}