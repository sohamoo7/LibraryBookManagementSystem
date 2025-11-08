package org.example.dto;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class BorrowRecordResponse {
    private UUID id;
    private UUID bookId;
    private String bookTitle;
    private UUID borrowerId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private String status;

    public BorrowRecordResponse() {
    }

    public BorrowRecordResponse(UUID id, UUID bookId, String bookTitle, UUID borrowerId, 
                              LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate,
                              double fineAmount, String status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowerId = borrowerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public void setBookId(UUID bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public UUID getBorrowerId() {
        return borrowerId;
    }
    
    public void setBorrowerId(UUID borrowerId) {
        this.borrowerId = borrowerId;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecordResponse that = (BorrowRecordResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(bookId, that.bookId) &&
               Objects.equals(bookTitle, that.bookTitle) &&
               Objects.equals(borrowDate, that.borrowDate) &&
               Objects.equals(dueDate, that.dueDate) &&
               Objects.equals(returnDate, that.returnDate) &&
               Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookId, bookTitle, borrowDate, dueDate, returnDate, status);
    }

    @Override
    public String toString() {
        return "BorrowRecordResponse{" +
               "id=" + id +
               ", bookId=" + bookId +
               ", bookTitle='" + bookTitle + '\'' +
               ", borrowDate=" + borrowDate +
               ", dueDate=" + dueDate +
               ", returnDate=" + returnDate +
               ", status='" + status + '\'' +
               '}';
    }
}
