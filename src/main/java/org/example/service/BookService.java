
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

    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findByDeletedFalse(pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }
    
    public Page<BookResponse> getBooksByCategoryAndAvailable(String category, Pageable pageable) {
        return bookRepository.findByCategoryAndAvailableCopiesGreaterThan(category, 0, pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    public Page<BookResponse> getBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategoryAndDeletedFalse(category, pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    public Page<BookResponse> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByIsAvailableTrueAndDeletedFalse(pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest bookRequest) {
        Book existingBook = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Calculate the difference in total copies to adjust available copies
        int copiesDifference = bookRequest.getTotalCopies() - existingBook.getTotalCopies();
        
        // Update book properties
        existingBook.setTitle(bookRequest.getTitle());
        existingBook.setAuthor(bookRequest.getAuthor());
        existingBook.setCategory(bookRequest.getCategory());
        existingBook.setTotalCopies(bookRequest.getTotalCopies());
        existingBook.setAvailableCopies(existingBook.getAvailableCopies() + copiesDifference);
        
        // Ensure available copies don't go negative
        if (existingBook.getAvailableCopies() < 0) {
            existingBook.setAvailableCopies(0);
        }
        
        // Update availability status
        Book updatedBook = bookRepository.save(existingBook);
        return modelMapper.map(updatedBook, BookResponse.class);
    }

    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return modelMapper.map(book, BookResponse.class);
    }
    
    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setDeleted(true);
        bookRepository.save(book);
    }
}