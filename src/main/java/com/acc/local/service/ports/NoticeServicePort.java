package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.*;

public interface NoticeServicePort {

    CreateNoticeResponse adminCreateNotice(CreateNoticeRequest request, String requesterId);

    GetNoticeResponse adminGetNotice(String noticeId, String requesterId);

    PageResponse<ListNoticesResponse> adminListNotices(PageRequest page, NoticeFilterRequest filter, String requesterId);
}
