package com.acc.server.dto;

import lombok.Getter;

@Getter
public class BookmarkResponse {
    private Long id;
    private String title;
    private String url;

    public BookmarkResponse(Long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

}
