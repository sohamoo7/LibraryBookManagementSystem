package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import org.example.dto.BorrowRequest;
import org.example.dto.BorrowResponse;
import org.example.dto.ReturnBookResponse;
import org.example.exception.*;
import org.example.model.Book;
import org.example.model.BorrowRecord;
import org.example.model.Borrower;
import org.example.repository.BookRepository;
import org.example.repository.BorrowRecordRepository;
import org.example.repository.BorrowerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowService implements IBorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final ModelMapper modelMapper;

    @Value("${borrow.days.due:14}")
    private int defaultBorrowDays;

    @Value("${borrow.fine.per.day:10.0}")
    private double finePerDay;
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowService.class);
    
    @Autowired
    public BorrowService(BorrowRecordRepository borrowRecordRepository,
                        BookRepository bookRepository,
                        BorrowerRepository borrowerRepository,
                        ModelMapper modelMapper) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.bookRepository = bookRepository;
        this.borrowerRepository = borrowerRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public BorrowResponse borrowBook(BorrowRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Borrow request cannot be null");
        }

        // Check if borrower exists
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + request.getBorrowerId()));

        // Check if book exists and is available
        Book book = bookRepository.findByIdAndDeletedFalseWithWriteLock(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        // Verify book is available
        if (book.getAvailableCopies() < 1) {
            throw new BookNotAvailableException("No available copies of the book with id: " + request.getBookId());
        }

        // Check if borrower has already borrowed this book
        boolean alreadyBorrowed = borrowRecordRepository.isBookBorrowedByBorrower(request.getBookId(), request.getBorrowerId());
        if (alreadyBorrowed) {
            throw new BookAlreadyBorrowedException("Book is already borrowed by this borrower");
        }

        // Check if borrower has reached max borrow limit
        int activeBorrows = borrowRecordRepository.countActiveBorrowsByBorrowerId(request.getBorrowerId());
        if (activeBorrows >= borrower.getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException("Borrower has reached the maximum borrow limit of " + borrower.getMaxBorrowLimit());
        }

        // Create borrow record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(defaultBorrowDays);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setDueDate(dueDate);
        borrowRecord.setReturnDate(null);
        borrowRecord.setFineAmount(0.0);
        
        // Update the book's available copies
        int newAvailableCopies = book.getAvailableCopies() - 1;
        book.setAvailableCopies(newAvailableCopies);
        book.setAvailable(newAvailableCopies > 0);
        
        // Set the book and borrower
        borrowRecord.setBook(book);
        borrowRecord.setBorrower(borrower);
        
        // Save the borrow record
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);
        
        return mapToBorrowResponse(savedRecord);
    }

    @Override
    @Transactional
    public ReturnBookResponse returnBook(BorrowRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Return request cannot be null");
        }

        try {
            // Find active borrow record with book and borrower loaded in the same transaction
            BorrowRecord borrowRecord = borrowRecordRepository
                    .findActiveBorrowWithBookAndBorrower(request.getBorrowerId(), request.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            String.format("No active borrow record found for bookId: %s and borrowerId: %s", 
                                    request.getBookId(), request.getBorrowerId())));

            // Get the book and borrower from the loaded borrow record
            Book book = borrowRecord.getBook();
            Borrower borrower = borrowRecord.getBorrower();
            
            if (book == null || borrower == null) {
                throw new IllegalStateException("Invalid borrow record: missing book or borrower");
            }

            // Mark as returned and update the return date
            LocalDate returnDate = LocalDate.now();
            if (borrowRecord.getReturnDate() != null) {
                throw new IllegalStateException("Book has already been returned");
            }
            
            borrowRecord.setReturnDate(returnDate);

            // Calculate fine if returned after due date
            if (returnDate.isAfter(borrowRecord.getDueDate())) {
                long daysOverdue = returnDate.toEpochDay() - borrowRecord.getDueDate().toEpochDay();
                double fine = daysOverdue * finePerDay;
                borrowRecord.setFineAmount(fine);
            } else {
                borrowRecord.setFineAmount(0.0);
            }

            // Update book available copies
            int newAvailableCopies = book.getAvailableCopies() + 1;
            book.setAvailableCopies(newAvailableCopies);
            book.setAvailable(newAvailableCopies > 0);

            // Create response
            ReturnBookResponse response = new ReturnBookResponse();
            response.setId(borrowRecord.getId());
            response.setBookId(book.getId());
            response.setBorrowerId(borrower.getId());
            response.setBorrowDate(borrowRecord.getBorrowDate());
            response.setDueDate(borrowRecord.getDueDate());
            response.setReturnDate(borrowRecord.getReturnDate());
            response.setFineAmount(borrowRecord.getFineAmount());
            response.setMessage("Book returned successfully");
            
            return response;
            
        } catch (Exception e) {
            logger.error("Error returning book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process book return", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowResponse> getActiveBorrows() {
        return borrowRecordRepository.findAllActiveBorrows().stream()
                .map(this::mapToBorrowResponse)
                .collect(Collectors.toList());
    }

    private BorrowResponse mapToBorrowResponse(BorrowRecord borrowRecord) {
        if (borrowRecord == null) {
            return null;
        }
        
        BorrowResponse response = new BorrowResponse();
        response.setId(borrowRecord.getId());
        response.setBookId(borrowRecord.getBook() != null ? borrowRecord.getBook().getId() : null);
        response.setBorrowerId(borrowRecord.getBorrower() != null ? borrowRecord.getBorrower().getId() : null);
        response.setBorrowDate(borrowRecord.getBorrowDate());
        response.setDueDate(borrowRecord.getDueDate());
        response.setReturnDate(borrowRecord.getReturnDate());
        response.setFineAmount(borrowRecord.getFineAmount());
        
        return response;
    }
}
