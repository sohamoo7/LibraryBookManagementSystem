package org.example.service;

import org.example.dto.BorrowRequest;
import org.example.dto.BorrowResponse;
import org.example.dto.ReturnBookResponse;

import java.util.List;
import java.util.UUID;

public interface IBorrowService {
    /**
     * Borrows a book for a borrower
     * @param request Contains bookId and borrowerId
     * @return BorrowResponse with borrow details
     * @throws ResourceNotFoundException if book or borrower not found
     * @throws BookNotAvailableException if no copies available
     * @throws BookAlreadyBorrowedException if book already borrowed by same borrower
     * @throws BorrowLimitExceededException if borrower reached max borrow limit
     */
    BorrowResponse borrowBook(BorrowRequest request);

    /**
     * Returns a borrowed book
     * @param request Contains bookId and borrowerId
     * @return ReturnBookResponse with return details including any fines
     * @throws ResourceNotFoundException if no active borrow record found
     */
    ReturnBookResponse returnBook(BorrowRequest request);

    /**
     * Gets all active borrows
     * @return List of active borrows
     */
    List<BorrowResponse> getActiveBorrows();
}
