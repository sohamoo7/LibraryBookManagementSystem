
package org.example.service;

import org.example.dto.BookRequest;
import org.example.dto.BookResponse;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Book;
import org.example.repository.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BookService implements IBookService {

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookService(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
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

    @Override
    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findByDeletedFalse(pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Override
    public Page<BookResponse> getBooksByCategoryAndAvailable(String category, Pageable pageable) {
        return bookRepository.findByCategoryAndAvailableCopiesGreaterThan(category, 0, pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Override
    public Page<BookResponse> getBooksByCategory(String category, Pageable pageable) {
        return bookRepository.findByCategoryAndDeletedFalse(category, pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Override
    public Page<BookResponse> getAvailableBooks(Pageable pageable) {
        return bookRepository.findByAvailableTrueAndDeletedFalse(pageable)
                .map(book -> modelMapper.map(book, BookResponse.class));
    }

    @Override
    @Transactional
    public BookResponse updateBook(UUID id, BookRequest bookRequest) {
        // Find the existing book
        Book existingBook = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        // Validate total copies is not negative
        if (bookRequest.getTotalCopies() < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }

        // Calculate the number of currently borrowed copies
        int borrowedCopies = existingBook.getTotalCopies() - existingBook.getAvailableCopies();
        
        // Ensure we're not reducing total copies below the number of borrowed books
        if (bookRequest.getTotalCopies() < borrowedCopies) {
            throw new IllegalStateException(
                String.format("Cannot reduce total copies below %d (currently borrowed)", borrowedCopies)
            );
        }
        
        // Calculate new available copies
        // If no books are borrowed, available copies should match total copies
        int newAvailableCopies = bookRequest.getTotalCopies() - borrowedCopies;
        
        // Update book properties
        existingBook.setTitle(bookRequest.getTitle());
        existingBook.setAuthor(bookRequest.getAuthor());
        existingBook.setCategory(bookRequest.getCategory());
        existingBook.setTotalCopies(bookRequest.getTotalCopies());
        
        // Update available copies and ensure it's not negative
        existingBook.setAvailableCopies(Math.max(0, newAvailableCopies));
        
        // Update availability status based on available copies
        existingBook.setAvailable(existingBook.getAvailableCopies() > 0);
        
        // Save and return the updated book
        Book updatedBook = bookRepository.save(existingBook);
        return modelMapper.map(updatedBook, BookResponse.class);
    }

    @Override
    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return modelMapper.map(book, BookResponse.class);
    }
    
    @Override
    @Transactional
    public void deleteBook(UUID id) {
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setDeleted(true);
        bookRepository.save(book);
    }
}