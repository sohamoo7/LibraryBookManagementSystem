package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book is required")
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrower_id", nullable = false)
    @NotNull(message = "Borrower is required")
    private Borrower borrower;
    
    /**
     * Sets the book for this borrow record and updates the bidirectional relationship.
     * @param book The book to associate with this record
     */
    public void setBook(Book book) {
        // Prevent unnecessary updates
        if (Objects.equals(this.book, book)) {
            return;
        }
        
        // Remove from old book if exists
        if (this.book != null) {
            this.book.removeBorrowRecord(this);
        }
        
        // Set new book and update relationship
        this.book = book;
        if (book != null) {
            book.addBorrowRecord(this);
        }
    }
    
    /**
     * Sets the borrower for this borrow record and updates the bidirectional relationship.
     * @param borrower The borrower to associate with this record
     */
    public void setBorrower(Borrower borrower) {
        // Prevent unnecessary updates
        if (Objects.equals(this.borrower, borrower)) {
            return;
        }
        
        // Remove from old borrower if exists
        if (this.borrower != null) {
            this.borrower.removeBorrowRecord(this);
        }
        
        // Set new borrower and update relationship
        this.borrower = borrower;
        if (borrower != null) {
            borrower.addBorrowRecord(this);
        }
    }
    
    /**
     * Helper method to set both book and borrower in a single transaction.
     * @param book The book to borrow
     * @param borrower The borrower who is borrowing the book
     */
    public void createBorrowRecord(Book book, Borrower borrower) {
        setBook(book);
        setBorrower(borrower);
        this.borrowDate = LocalDate.now();
        this.dueDate = this.borrowDate.plusWeeks(2); // 2 weeks borrowing period
    }
    
    /**
     * Marks the book as returned and updates the book's availability.
     */
    public void markAsReturned() {
        this.returnDate = LocalDate.now();
        if (this.book != null) {
            this.book.setAvailableCopies(this.book.getAvailableCopies() + 1);
            this.book.setAvailable(true);
        }
    }
    
    @Column(nullable = false)
    @NotNull(message = "Borrow date is required")
    @PastOrPresent(message = "Borrow date must be in the past or present")
    private LocalDate borrowDate;
    
    @Column(nullable = false)
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be in the future or present")
    private LocalDate dueDate;
    
    @PastOrPresent(message = "Return date must be in the past or present")
    private LocalDate returnDate;
    
    @Min(value = 0, message = "Fine amount cannot be negative")
    private Double fineAmount = 0.0;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

//




    public Borrower getBorrower() {
        return borrower;
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

    public Double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BorrowRecord that = (BorrowRecord) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(book, that.book) &&
               Objects.equals(borrower, that.borrower) &&
               Objects.equals(borrowDate, that.borrowDate) &&
               Objects.equals(dueDate, that.dueDate) &&
               Objects.equals(returnDate, that.returnDate) &&
               Objects.equals(fineAmount, that.fineAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, book, borrower, borrowDate, dueDate, returnDate, fineAmount);
    }

    @Override
    public String toString() {
        return "BorrowRecord{" +
               "id=" + id +
               ", book=" + book.getId() +
               ", borrower=" + borrower.getId() +
               ", borrowDate=" + borrowDate +
               ", dueDate=" + dueDate +
               ", returnDate=" + returnDate +
               ", fineAmount=" + fineAmount +
               '}';
    }
}
