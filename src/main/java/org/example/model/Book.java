package org.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
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
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowRecord> borrowRecords = new ArrayList<>();
    

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

    /**
     * Adds a borrow record for this book and sets up the bidirectional relationship.
     * @param borrowRecord The borrow record to add
     */
    public void addBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecords.add(borrowRecord);
        borrowRecord.setBook(this);
    }

    /**
     * Removes a borrow record from this book and clears the bidirectional relationship.
     * @param borrowRecord The borrow record to remove
     */
    public void removeBorrowRecord(BorrowRecord borrowRecord) {
        borrowRecords.remove(borrowRecord);
        borrowRecord.setBook(null);
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
    }

    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Gets all borrow records for this book.
     * @return An unmodifiable list of borrow records
     */
    public List<BorrowRecord> getBorrowRecords() {
        return List.copyOf(borrowRecords);
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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
