package com.acc.local.service.ports;

import com.acc.global.common.PageRequest;
import com.acc.global.common.PageResponse;
import com.acc.local.dto.auth.CreateNoticeRequest;
import com.acc.local.dto.auth.CreateNoticeResponse;
import com.acc.local.dto.auth.ListNoticesResponse;

public interface NoticeServicePort {

    CreateNoticeResponse adminCreateNotice(CreateNoticeRequest request, String requesterId);

    PageResponse<ListNoticesResponse> adminListNotices(PageRequest page, String requesterId);
}
