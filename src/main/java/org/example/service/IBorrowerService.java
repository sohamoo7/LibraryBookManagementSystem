package org.example.service;

import org.example.dto.BorrowRecordResponse;
import org.example.dto.BorrowerRequest;
import org.example.dto.BorrowerResponse;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ResourceAlreadyExistsException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IBorrowerService {
    /**
     * Registers a new borrower
     * @param request Contains borrower details
     * @return BorrowerResponse with the created borrower's information
     * @throws ResourceAlreadyExistsException if a borrower with the same email already exists
     */
    BorrowerResponse registerBorrower(BorrowerRequest request);

    /**
     * Gets a list of borrowers with overdue books
     * @param currentDate The current date to check for overdue books
     * @return List of borrowers with overdue books
     */
    List<BorrowerResponse> getBorrowersWithOverdueBooks(LocalDate currentDate);

    /**
     * Gets the borrow history for a specific borrower
     * @param borrowerId The ID of the borrower
     * @return List of borrow records for the borrower
     * @throws ResourceNotFoundException if borrower is not found
     */
    List<BorrowRecordResponse> getBorrowHistory(UUID borrowerId);
}
