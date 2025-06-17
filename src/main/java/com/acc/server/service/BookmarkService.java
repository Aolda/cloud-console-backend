package com.acc.server.service;

import com.acc.server.dto.BookmarkRequest;
import com.acc.server.dto.BookmarkResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookmarkService {

    private final Map<Long, BookmarkResponse> store = new ConcurrentHashMap<>(); //임시로
    private final AtomicLong idGenerator = new AtomicLong(1); // ID 자동 증가

    public void save(BookmarkRequest request) {
        Long id = idGenerator.getAndIncrement();
        BookmarkResponse response = new BookmarkResponse(id, request.getTitle(), request.getUrl());
        store.put(id, response);
        System.out.println("Saved: " + response.getTitle() + ", ID: " + id);
    }

    public BookmarkResponse find(Long id) {
        BookmarkResponse result = store.get(id);
        if (result == null) {
            throw new IllegalArgumentException("해당 ID의 북마크가 존재하지 않습니다: " + id);
        }
        return result;
    }
    public List<BookmarkResponse> findAll() {
        return new ArrayList<>(store.values());
    }
    public void delete(Long id) {
        if (!store.containsKey(id)) {
            throw new NoSuchElementException("해당 북마크가 존재하지 않습니다: " + id);
        }
        store.remove(id);
    }

}
