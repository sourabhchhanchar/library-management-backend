package com.sourabh.librarymanagement.scheduler;

import com.sourabh.librarymanagement.model.Book;
import com.sourabh.librarymanagement.model.User;
import com.sourabh.librarymanagement.repository.BookRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class BookExpiryScheduler {

    private final BookRepository bookRepository;
    private final MongoTemplate mongoTemplate;

    public BookExpiryScheduler(BookRepository bookRepository,
                               MongoTemplate mongoTemplate) {
        this.bookRepository = bookRepository;
        this.mongoTemplate = mongoTemplate;
    }

    // Runs every 5 minutes
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void autoReturnExpiredBooks() {

        // 1. Find expired & borrowed books
        Query query = new Query(
                Criteria.where("expiryAt").lt(Instant.now())
                        .and("available").is(false)
        );

        List<Book> expiredBooks = mongoTemplate.find(query, Book.class);

        for (Book book : expiredBooks) {

            // 2. Find user who borrowed this book
            Query userQuery = new Query(
                    Criteria.where("borrowedBookIds").is(book.getId())
            );

            User user = mongoTemplate.findOne(userQuery, User.class);

            if (user != null) {
                user.getBorrowedBookIds().remove(book.getId());
                mongoTemplate.save(user);
            }

            // 3. Mark book as available
            book.setAvailable(true);
            bookRepository.save(book);
        }
    }

    // for only testing purpose @Scheduled(cron = "0 * * * * *")

    // Runs every day at 10 PM
    @Scheduled(cron = "0 0 22 * * *")
    public void autoReturnReadOnlyBooks() {

        Query query = new Query(
                Criteria.where("policy").is("READ_ONLY")
                        .and("available").is(false)
        );

        List<Book> readOnlyBooks = mongoTemplate.find(query, Book.class);

        for (Book book : readOnlyBooks) {

            Query userQuery = new Query(
                    Criteria.where("borrowedBookIds").is(book.getId())
            );

            User user = mongoTemplate.findOne(userQuery, User.class);

            if (user != null) {
                user.getBorrowedBookIds().remove(book.getId());
                mongoTemplate.save(user);
            }

            book.setAvailable(true);
            bookRepository.save(book);
        }
    }

}
