package com.sourabh.librarymanagement.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "books")
public class Book {

    @Id
    private String id;

    private String title;
    private String author;

    private boolean available = true;

    // nullable → only for expiry-based books
    private Instant expiryAt;

    private BookPolicy policy;

    public Book() {}

    // getters & setters

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Instant getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(Instant expiryAt) {
        this.expiryAt = expiryAt;
    }

    public BookPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(BookPolicy policy) {
        this.policy = policy;
    }
}
