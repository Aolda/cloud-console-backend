package com.acc.server.controller;

import com.acc.server.dto.BookmarkRequest;
import com.acc.server.dto.BookmarkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "북마크 API", description = "북마크 관련 REST API")
@SecurityRequirement(name = "basicAuth")
public interface BookmarkControllerDocs {

    @Operation(summary = "북마크 저장", description = "새로운 북마크를 저장합니다.")
    @ApiResponse(responseCode = "201", description = "북마크 저장 성공")
    @PostMapping
    void save(@RequestBody BookmarkRequest request);

    @Operation(summary = "북마크 조회", description = "ID로 북마크를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "북마크 조회 성공")
    @GetMapping("/{id}")
    BookmarkResponse get(@PathVariable Long id);

    @Operation(summary = "전체 북마크 조회", description = "모든 북마크를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "북마크 전체 조회 성공")
    @GetMapping
    List<BookmarkResponse> getAll();

    @Operation(summary = "북마크 삭제", description = "ID로 북마크를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "북마크 삭제 성공")
    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);

}
