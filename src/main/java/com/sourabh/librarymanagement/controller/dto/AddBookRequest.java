package com.sourabh.librarymanagement.controller.dto;

import com.sourabh.librarymanagement.model.BookPolicy;

import java.time.Instant;

public class AddBookRequest {

    private String title;
    private String author;
    private BookPolicy policy;

    // optional
    private Instant expiryAt;

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public BookPolicy getPolicy() {
        return policy;
    }

    public Instant getExpiryAt() {
        return expiryAt;
    }
}
