package com.acc.local.service.modules.auth;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.global.exception.auth.AuthErrorCode;
import com.acc.global.exception.auth.AuthServiceException;
import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.dto.auth.CreateNoticeResponse;
import com.acc.local.dto.auth.ListNoticesResponse;
import com.acc.local.entity.NoticeEntity;
import com.acc.local.entity.UserDbExtraEntity;
import com.acc.local.repository.ports.NoticeRepositoryPort;
import com.acc.local.repository.ports.UserRepositoryPort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeModuleTest {

    @Mock
    private NoticeRepositoryPort noticeRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private NoticeModule noticeModule;

    // ----------------------------------------------------
    // 공지사항 생성 테스트
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 공지사항을 생성하고 저장된 정보를 반환받을 수 있다.")
    void whenAdminCreateNotice_thenReturnCreateNoticeResponse() {

        // given
        CreateNoticeRequest request = CreateNoticeRequest.builder()
                .title("시스템 점검 안내")
                .content("내일 새벽 시스템 점검이 있습니다.")
                .startsAt(LocalDateTime.now())
                .endsAt(LocalDateTime.now().plusDays(1))
                .build();

        String creatorId = "user-1";

        // Notice 생성 → 엔티티 저장 mock
        NoticeEntity savedEntity = NoticeEntity.builder()
                .noticeId("notice-111")
                .noticeTitle("시스템 점검 안내")
                .noticeDescription("내일 새벽 시스템 점검이 있습니다.")
                .noticeUserId(creatorId)
                .createdAt(LocalDateTime.now())
                .startsAt(request.startsAt())
                .endsAt(request.endsAt())
                .build();

        when(noticeRepositoryPort.save(any()))
                .thenReturn(savedEntity);

        when(userRepositoryPort.findUserDetailById(creatorId))
                .thenReturn(Optional.of(
                        UserDbExtraEntity.builder()
                                .userId(creatorId)
                                .userName("홍길동")
                                .build()));

        // when
        CreateNoticeResponse response =
                noticeModule.adminCreateNotice(request, creatorId);

        // then
        assertNotNull(response);
        assertEquals("notice-111", response.noticeId());
        assertEquals("시스템 점검 안내", response.title());
        assertEquals("홍길동", response.createdBy());
        verify(noticeRepositoryPort).save(any());
        verify(userRepositoryPort).findUserDetailById(creatorId);
    }

    // ----------------------------------------------------
    // 사용자 이름 조회 실패 테스트
    // ----------------------------------------------------
    @Test
    @DisplayName("공지 생성 시 작성자 정보를 찾을 수 없으면 USER_NOT_FOUND 예외 발생")
    void whenCreatorNotFound_thenThrowException() {

        // given
        CreateNoticeRequest request = CreateNoticeRequest.builder()
                .title("공지")
                .content("내용")
                .startsAt(LocalDateTime.now())
                .endsAt(LocalDateTime.now().plusHours(1))
                .build();

        String creatorId = "unknown-user";

        when(userRepositoryPort.findUserDetailById(creatorId))
                .thenReturn(Optional.empty());

        // Notice 저장은 호출되기 전에 예외 발생 → mock 필요 없음

        // when & then
        AuthServiceException ex = assertThrows(AuthServiceException.class,
                () -> noticeModule.adminCreateNotice(request, creatorId));

        assertEquals(AuthErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    // ----------------------------------------------------
    // 공지사항 목록 조회 테스트 (마커 기반 페이지네이션)
    // ----------------------------------------------------
    @Test
    @DisplayName("관리자는 공지사항 목록을 조회하고 DTO 리스트로 반환받을 수 있다.")
    void whenAdminListNotices_thenReturnPageResponse() {

        // given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setMarker(null);
        pageRequest.setDirection(PageRequest.Direction.next);
        pageRequest.setLimit(10);

        LocalDateTime now = LocalDateTime.now();
        ListNoticesResponse r1 = new ListNoticesResponse("n1", "공지1", "내용1", "사용자1", now, now, now.plusHours(1));
        ListNoticesResponse r2 = new ListNoticesResponse("n2", "공지2", "내용2", "사용자2", now, now, now.plusHours(1));

        PageResponse<ListNoticesResponse> mockResponse = PageResponse.<ListNoticesResponse>builder()
                .contents(List.of(r1, r2))
                .first(true)
                .last(true)
                .size(2)
                .nextMarker(null)
                .prevMarker(null)
                .build();

        when(noticeRepositoryPort.findAllNotices(eq(null), eq("next"), eq(10), any()))
                .thenReturn(mockResponse);

        // when
        PageResponse<ListNoticesResponse> response =
                noticeModule.adminListNotices(pageRequest, null);

        // then
        assertEquals(2, response.getContents().size());
        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        assertNull(response.getNextMarker());
        assertEquals("사용자1", response.getContents().get(0).createdBy());

        verify(noticeRepositoryPort).findAllNotices(eq(null), eq("next"), eq(10), any());
    }

    // ----------------------------------------------------
    // 마커 기반 페이지네이션 다음 페이지 조회 테스트
    // ----------------------------------------------------
    @Test
    @DisplayName("마커 기반으로 다음 페이지를 조회할 수 있다.")
    void whenAdminListNotices_withMarker_thenReturnNextPage() {

        // given
        PageRequest pageRequest = new PageRequest();
        pageRequest.setMarker("n2");
        pageRequest.setDirection(PageRequest.Direction.next);
        pageRequest.setLimit(10);

        LocalDateTime now = LocalDateTime.now();
        ListNoticesResponse r3 = new ListNoticesResponse("n3", "공지3", "내용3", "사용자3", now, now, now.plusHours(1));

        PageResponse<ListNoticesResponse> mockResponse = PageResponse.<ListNoticesResponse>builder()
                .contents(List.of(r3))
                .first(false)
                .last(true)
                .size(1)
                .nextMarker(null)
                .prevMarker("n3")
                .build();

        when(noticeRepositoryPort.findAllNotices(eq("n2"), eq("next"), eq(10), any()))
                .thenReturn(mockResponse);

        // when
        PageResponse<ListNoticesResponse> response =
                noticeModule.adminListNotices(pageRequest, null);

        // then
        assertEquals(1, response.getContents().size());
        assertFalse(response.getFirst());
        assertTrue(response.getLast());
        assertNull(response.getNextMarker());
        assertEquals("n3", response.getPrevMarker());

        verify(noticeRepositoryPort).findAllNotices(eq("n2"), eq("next"), eq(10), any());
    }

    @Test
    @DisplayName("공지사항 목록은 PageRequest가 null이어도 기본 페이지 값으로 조회한다.")
    void whenAdminListNoticesWithNullPage_thenUseDefaultPageRequest() {
        PageResponse<ListNoticesResponse> mockResponse = PageResponse.<ListNoticesResponse>builder()
                .contents(List.of())
                .first(true)
                .last(true)
                .size(0)
                .nextMarker(null)
                .prevMarker(null)
                .build();
        when(noticeRepositoryPort.findAllNotices(eq(null), eq("next"), eq(10), any()))
                .thenReturn(mockResponse);

        PageResponse<ListNoticesResponse> response = noticeModule.adminListNotices(null, null);

        assertTrue(response.getFirst());
        assertTrue(response.getLast());
        verify(noticeRepositoryPort).findAllNotices(eq(null), eq("next"), eq(10), any());
    }
}
