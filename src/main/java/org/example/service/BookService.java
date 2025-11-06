
package org.example.service;

import org.example.dto.BookRequest;
import org.example.dto.BookResponse;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Book;
import org.example.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public BookResponse addOrUpdateBook(BookRequest bookRequest) {
        Optional<Book> existingBook = bookRepository.findByTitleAndAuthorAndDeletedFalse(
                bookRequest.getTitle(), bookRequest.getAuthor());

        Book bookToSave;
        if (existingBook.isPresent()) {
            bookToSave = existingBook.get();
            bookToSave.setTotalCopies(bookToSave.getTotalCopies() + bookRequest.getTotalCopies());
            bookToSave.setAvailableCopies(bookToSave.getAvailableCopies() + bookRequest.getTotalCopies());
        } else {
            bookToSave = modelMapper.map(bookRequest, Book.class);
            bookToSave.setAvailableCopies(bookRequest.getTotalCopies());
        }

        Book savedBook = bookRepository.save(bookToSave);
        return modelMapper.map(savedBook, BookResponse.class);
    }

    public Page<BookResponse> getAllBooks(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return bookRepository.findByDeletedFalse(pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    public List<BookResponse> getBooksByCategory(String category) {
        return bookRepository.findByCategoryAndDeletedFalse(category).stream()
                .map(book -> modelMapper.map(book, BookResponse.class))
                .collect(Collectors.toList());
    }

    public List<BookResponse> getAvailableBooks() {
        return bookRepository.findByIsAvailableTrueAndDeletedFalse().stream()
                .map(book -> modelMapper.map(book, BookResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        book.setDeleted(true);
        bookRepository.save(book);
    }
}