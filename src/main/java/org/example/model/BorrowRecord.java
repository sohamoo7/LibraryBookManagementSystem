package org.example.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id",
    scope = BorrowRecord.class
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BorrowRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book is required")
    @JsonBackReference("book-borrowRecords")
    private Book book;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "borrower_id", nullable = false)
    @NotNull(message = "Borrower is required")
    @JsonBackReference("borrower-borrowRecords")
    private Borrower borrower;
    
    /**
     * Sets the book for this borrow record and updates the bidirectional relationship.
     * Note: This method does NOT update the book's available copies - that should be handled by the service layer.
     * @param book The book to associate with this record
     */
    public void setBook(Book book) {
        // Prevent unnecessary updates
        if (Objects.equals(this.book, book)) {
            return;
        }
        
        // Remove from old book if exists
        if (this.book != null) {
            this.book.getBorrowRecords().remove(this);
        }
        
        // Set new book and update relationship
        this.book = book;
        
        if (book != null) {
            // Initialize the borrowRecords collection if it's null (shouldn't happen with proper initialization)
            if (book.getBorrowRecords() == null) {
                book.setBorrowRecords(new ArrayList<>());
            }
            book.getBorrowRecords().add(this);
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
            this.borrower.getBorrowRecords().remove(this);
        }
        
        // Set new borrower and update relationship
        this.borrower = borrower;
        if (borrower != null) {
            // Initialize the borrowRecords collection if it's null (shouldn't happen with proper initialization)
            if (borrower.getBorrowRecords() == null) {
                borrower.setBorrowRecords(new ArrayList<>());
            }
            borrower.getBorrowRecords().add(this);
        }
    }
    
    // Removed createBorrowRecord method as this logic should be in the service layer
    
    /**
     * Marks the book as returned.
     * Note: Updating the book's available copies should be handled by the service layer.
     */
    public void markAsReturned() {
        this.returnDate = LocalDate.now();
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

    // Removed getBorrowRecords() as it's not needed and was causing issues


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
