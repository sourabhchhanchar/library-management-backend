package com.sourabh.librarymanagement.controller;

import com.sourabh.librarymanagement.model.Book;
import com.sourabh.librarymanagement.model.User;
import com.sourabh.librarymanagement.repository.BookRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/books")
public class UserBookController {

    private final BookRepository bookRepository;
    private final MongoTemplate mongoTemplate;

    public UserBookController(BookRepository bookRepository,
                              MongoTemplate mongoTemplate) {
        this.bookRepository = bookRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @PostMapping("/{bookId}/borrow")
    public ResponseEntity<String> borrowBook(@PathVariable String bookId) {

        // 1. Get logged-in user email from JWT
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. Fetch user
        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        // 3. Fetch book
        Book book = bookRepository.findById(bookId).orElse(null);

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book not found");
        }

        // 4. Check availability
        if (!book.isAvailable()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Book is already borrowed");
        }

        // 5. Update book
        book.setAvailable(false);
        bookRepository.save(book);

        // 6. Update user
        user.getBorrowedBookIds().add(bookId);
        mongoTemplate.save(user);

        return ResponseEntity.ok("Book borrowed successfully");
    }

    @PostMapping("/{bookId}/return")
    public ResponseEntity<String> returnBook(@PathVariable String bookId) {

        // 1. Get logged-in user email from JWT
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. Fetch user
        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        // 3. Check user actually borrowed this book
        if (!user.getBorrowedBookIds().contains(bookId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("This book was not borrowed by the user");
        }

        // 4. Fetch book
        Book book = bookRepository.findById(bookId).orElse(null);

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Book not found");
        }

        // 5. Update book
        book.setAvailable(true);
        bookRepository.save(book);

        // 6. Update user
        user.getBorrowedBookIds().remove(bookId);
        mongoTemplate.save(user);

        return ResponseEntity.ok("Book returned successfully");
    }

    @GetMapping("/borrowed")
    public ResponseEntity<?> listBorrowedBooks() {

        // 1. Get logged-in user email from JWT
        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 2. Fetch user
        User user = mongoTemplate.findOne(
                new Query(Criteria.where("email").is(email)),
                User.class
        );

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found");
        }

        // 3. If no borrowed books
        if (user.getBorrowedBookIds().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        // 4. Fetch books by IDs
        List<Book> books = bookRepository.findAllById(
                user.getBorrowedBookIds()
        );

        return ResponseEntity.ok(books);
    }

}
