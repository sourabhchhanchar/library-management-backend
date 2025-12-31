package com.sourabh.librarymanagement.controller;

import com.sourabh.librarymanagement.controller.dto.AddBookRequest;
import com.sourabh.librarymanagement.model.Book;
import com.sourabh.librarymanagement.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookRepository bookRepository;

    public AdminBookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody AddBookRequest request) {

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPolicy(request.getPolicy());
        book.setExpiryAt(request.getExpiryAt());
        book.setAvailable(true);

        Book saved = bookRepository.save(book);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }
}
