package com.sourabh.librarymanagement.repository;

import com.sourabh.librarymanagement.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookRepository extends MongoRepository<Book, String> {
}
