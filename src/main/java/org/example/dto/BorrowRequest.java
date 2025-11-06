package org.example.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class BorrowRequest {
    @NotNull(message = "Book ID is required")
    private UUID bookId;
    
    @NotNull(message = "Borrower ID is required")
    private UUID borrowerId;

    // Getters and Setters
    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public UUID getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(UUID borrowerId) {
        this.borrowerId = borrowerId;
    }
}
