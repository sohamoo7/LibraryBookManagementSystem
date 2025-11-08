package org.example.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id",
    scope = Book.class
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Book extends BaseEntity {
    // ID and audit fields are inherited from BaseEntity

    @NotBlank(message = "Title is required")
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

    @OneToMany(mappedBy = "book",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH},
            orphanRemoval = false,
            fetch = FetchType.LAZY)
    @JsonManagedReference("book-borrowRecords")
    private List<BorrowRecord> borrowRecords = new ArrayList<>();

    @Version
    @Column(name = "version")
    private Long version;
    
    @Column(name = "is_available", nullable = false)
    private boolean available = true;

    @Min(value = 0, message = "Total copies cannot be negative")
    @Column(nullable = false)
    private int totalCopies = 0;

    @Min(value = 0, message = "Available copies cannot be negative")
    @Column(nullable = false)
    private int availableCopies = 0;

    // Business methods

    /**
     * Adds a borrow record and updates available copies
     *
     * @param borrowRecord The borrow record to add
     * @throws IllegalArgumentException if borrowRecord is null or no copies available
     */
    public void addBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecord == null) {
            throw new IllegalArgumentException("Borrow record cannot be null");
        }
        if (availableCopies <= 0) {
            throw new IllegalStateException("No available copies to borrow");
        }
        if (!borrowRecords.contains(borrowRecord)) {
            borrowRecords.add(borrowRecord);
            borrowRecord.setBook(this);
            availableCopies--;
            available = availableCopies > 0;
        }
    }

    /**
     * Removes a borrow record and updates available copies
     *
     * @param borrowRecord The borrow record to remove
     */
    public void removeBorrowRecord(BorrowRecord borrowRecord) {
        if (borrowRecord != null && borrowRecords.remove(borrowRecord)) {
            // The BorrowRecord.setBook(null) will handle the other side
            borrowRecord.setBook(null);
            availableCopies++;
            available = true;
        }
    }

    public Book() {
    }

    public Book(String title, String author, String category, int totalCopies) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
        this.available = totalCopies > 0;
    }

    // Getters and Setters
    // ID getter and setter are inherited from BaseEntity

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

    public List<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public void setBorrowRecords(List<BorrowRecord> borrowRecords) {
        if (borrowRecords == null) {
            throw new IllegalArgumentException("Borrow records list cannot be null");
        }
        // Clear existing relationships
        this.borrowRecords.forEach(br -> br.setBook(null));
        this.borrowRecords.clear();
        // Set new relationships
        borrowRecords.forEach(this::addBorrowRecord);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) {
        if (totalCopies < 0) {
            throw new IllegalArgumentException("Total copies cannot be negative");
        }
        int difference = totalCopies - this.totalCopies;
        this.totalCopies = totalCopies;
        this.availableCopies = Math.max(0, this.availableCopies + difference);
        this.available = this.availableCopies > 0;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative");
        }
        if (availableCopies > this.totalCopies) {
            throw new IllegalArgumentException("Available copies cannot exceed total copies");
        }
        this.availableCopies = availableCopies;
        this.available = availableCopies > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Book book = (Book) o;
        return available == book.available &&
                totalCopies == book.totalCopies &&
                availableCopies == book.availableCopies &&
                Objects.equals(title, book.title) &&
                Objects.equals(author, book.author) &&
                Objects.equals(category, book.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, author, category, available, totalCopies, availableCopies);
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", available=" + available +
                ", totalCopies=" + totalCopies +
                ", availableCopies=" + availableCopies +
                '}';
    }
}