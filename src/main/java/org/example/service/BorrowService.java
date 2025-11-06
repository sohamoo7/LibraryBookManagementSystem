package org.example.service;

import org.example.dto.BorrowRequest;
import org.example.dto.BorrowResponse;
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
public class BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowerRepository borrowerRepository;
    private final ModelMapper modelMapper;

    @Value("${borrow.days.due:14}")
    private int defaultBorrowDays;

    @Value("${borrow.fine.per.day:10.0}")
    private double finePerDay;

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

    public BorrowResponse borrowBook(BorrowRequest request) {
        // Check if borrower exists
        Borrower borrower = borrowerRepository.findById(request.getBorrowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Borrower not found with id: " + request.getBorrowerId()));

        // Check if book exists and is available
        Book book = bookRepository.findByIdAndDeletedFalse(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + request.getBookId()));

        if (book.getAvailableCopies() < 1) {
            throw new BookNotAvailableException("No available copies of the book with id: " + request.getBookId());
        }

        // Check if borrower has already borrowed this book
        if (borrowRecordRepository.isBookBorrowedByBorrower(request.getBookId(), request.getBorrowerId())) {
            throw new BookAlreadyBorrowedException("Book is already borrowed by this borrower");
        }

        // Check if borrower has reached max borrow limit
        int activeBorrows = borrowRecordRepository.countActiveBorrowsByBorrowerId(request.getBorrowerId());
        if (activeBorrows >= borrower.getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException("Borrower has reached the maximum borrow limit");
        }

        // Create borrow record
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(defaultBorrowDays);

        BorrowRecord borrowRecord = new BorrowRecord();
        borrowRecord.setBook(book);
        borrowRecord.setBorrower(borrower);
        borrowRecord.setBorrowDate(borrowDate);
        borrowRecord.setDueDate(dueDate);

        // Save borrow record
        BorrowRecord savedRecord = borrowRecordRepository.save(borrowRecord);

        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        return mapToBorrowResponse(savedRecord);
    }

    public BorrowResponse returnBook(BorrowRequest request) {
        // Find active borrow record
        BorrowRecord borrowRecord = borrowRecordRepository
                .findActiveBorrow(request.getBorrowerId(), request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("No active borrow record found for the given book and borrower"));

        // Update return date
        LocalDate returnDate = LocalDate.now();
        borrowRecord.setReturnDate(returnDate);

        // Calculate fine if returned after due date
        if (returnDate.isAfter(borrowRecord.getDueDate())) {
            long daysOverdue = returnDate.toEpochDay() - borrowRecord.getDueDate().toEpochDay();
            double fine = daysOverdue * finePerDay;
            borrowRecord.setFineAmount(fine);
        }

        // Save updated record
        BorrowRecord updatedRecord = borrowRecordRepository.save(borrowRecord);

        // Update book available copies
        Book book = borrowRecord.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return mapToBorrowResponse(updatedRecord);
    }

    public List<BorrowResponse> getActiveBorrows() {
        return borrowRecordRepository.findAllActiveBorrows().stream()
                .map(this::mapToBorrowResponse)
                .collect(Collectors.toList());
    }

    private BorrowResponse mapToBorrowResponse(BorrowRecord borrowRecord) {
        BorrowResponse response = modelMapper.map(borrowRecord, BorrowResponse.class);
        response.setBookId(borrowRecord.getBook().getId());
        response.setBorrowerId(borrowRecord.getBorrower().getId());
        return response;
    }
}
