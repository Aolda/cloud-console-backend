package com.acc.server.controller;

import com.acc.server.dto.BookmarkRequest;
import com.acc.server.dto.BookmarkResponse;
import com.acc.server.service.BookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookmarks")
public class BookmarkController implements BookmarkControllerDocs {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService){
        this.bookmarkService = bookmarkService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public void save(@RequestBody BookmarkRequest request){
        bookmarkService.save(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @Override
    public BookmarkResponse get(@PathVariable Long id){
        return bookmarkService.find(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @Override
    public List<BookmarkResponse> getAll(){
        return bookmarkService.findAll();
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void delete(@PathVariable Long id) {
        bookmarkService.delete(id);
    }
}
