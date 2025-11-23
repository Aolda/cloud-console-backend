package com.acc.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "페이지네이션 응답")
public class PageResponse<T> {
    @ArraySchema(schema = @Schema(description = "응답 데이터 목록"))
    private List<T> contents;
    @Schema(description = "첫 페이지 여부")
    private Boolean first;
    @Schema(description = "마지막 페이지 여부")
    private Boolean last;
    @Schema(description = "현재 페이지의 데이터 개수")
    private Integer size;
    @Schema(description = "다음 페이지 마커")
    private String nextMarker;
    @Schema(description = "이전 페이지 마커")
    private String prevMarker;
}
