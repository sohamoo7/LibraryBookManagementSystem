package org.example.service;

import org.example.dto.BookRequest;
import org.example.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IBookService {
    BookResponse addOrUpdateBook(BookRequest bookRequest);
    Page<BookResponse> getAllBooks(Pageable pageable);
    Page<BookResponse> getBooksByCategoryAndAvailable(String category, Pageable pageable);
    Page<BookResponse> getBooksByCategory(String category, Pageable pageable);
    Page<BookResponse> getAvailableBooks(Pageable pageable);
    BookResponse updateBook(UUID id, BookRequest bookRequest);
    BookResponse getBookById(UUID id);
    void deleteBook(UUID id);
}
