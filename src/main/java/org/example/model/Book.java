package org.example.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.example.model.BorrowRecord;

@Entity
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Book.class
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Size(max = 200, message = "Title must be less than 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Author is required")
    @Size(max = 100, message = "Author name must be less than 100 characters")
    @Column(nullable = false, length = 100)
    private String author;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category must be less than 50 characters")
    @Column(nullable = false, length = 50)
    private String category;

    @OneToMany(mappedBy = "book", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference("book-borrowRecords")
    private List<BorrowRecord> borrowRecords;  // Will be initialized in constructor
    
    /**
     * Sets the borrow records for this book.
     * @param borrowRecords the list of borrow records to set
     */
    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        this.borrowRecords = borrowRecords;
    }
    
    @Column(nullable = false)
    private boolean isAvailable = true;

    @Min(value = 0, message = "Total copies cannot be negative")
    @Column(nullable = false)
    private int totalCopies;

    @Min(value = 0, message = "Available copies cannot be negative")
    @Column(nullable = false)
    private int availableCopies;

    @Column(nullable = false)
    private boolean deleted = false;

    // Constructors
    public Book() {
        this.borrowRecords = new ArrayList<>();
    }

    public Book(String title, String author, String category, int totalCopies) {
        this();  // Call the default constructor to initialize collections
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.isAvailable = totalCopies > 0;
    }

    // Business methods
    /**
     * Adds a borrow record to this book's records.
     * Note: This method does NOT update the available copies count - that should be handled by the service layer.
     * @param borrowRecord The borrow record to add
     */
    /**
     * Adds a borrow record to this book's records.
     * Note: This method only manages the bidirectional relationship.
     * The service layer should handle business logic like available copies.
     * @param borrowRecord The borrow record to add (must not be null)
     * @throws IllegalArgumentException if borrowRecord is null
     */
    /**
     * Adds a borrow record to this book's records.
     * Note: This method only manages the bidirectional relationship.
     * The service layer should handle business logic like available copies.
     * @param borrowRecord The borrow record to add (must not be null)
     * @throws IllegalArgumentException if borrowRecord is null
     */
    public void addBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecord == null) {
            throw new IllegalArgumentException("Borrow record cannot be null");
        }
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<>();
        }
        if (!this.borrowRecords.contains(borrowRecord)) {
            this.borrowRecords.add(borrowRecord);
            // The BorrowRecord.setBook() method will handle the other side of the relationship
            borrowRecord.setBook(this);
        }
    }

    /**
     * Removes a borrow record from this book's records.
     * Note: This method does NOT update the available copies count - that should be handled by the service layer.
     * @param borrowRecord The borrow record to remove
     */
    /**
     * Removes a borrow record from this book's records.
     * @param borrowRecord The borrow record to remove
     */
    public void removeBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecord != null && this.borrowRecords != null && this.borrowRecords.remove(borrowRecord)) {
            // The BorrowRecord.setBook(null) will handle the other side
            borrowRecord.setBook(null);
        }
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
        this.isAvailable = availableCopies > 0;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<BorrowRecord> getBorrowRecords() {
        if (this.borrowRecords == null) {
            this.borrowRecords = new ArrayList<>();
        }
        return new ArrayList<>(borrowRecords);  // Return a copy to prevent direct modifications
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return isAvailable == book.isAvailable &&
                totalCopies == book.totalCopies &&
                availableCopies == book.availableCopies &&
                deleted == book.deleted &&
                Objects.equals(id, book.id) &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(category, book.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, category, isAvailable, totalCopies, availableCopies, deleted);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", isAvailable=" + isAvailable +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                ", deleted=" + deleted +
                '}';
    }
}