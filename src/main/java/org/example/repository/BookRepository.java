package org.example.repository;

import org.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {
    // Paginated queries
    Page<Book> findByCategoryAndDeletedFalse(String category, Pageable pageable);
    Page<Book> findByAvailableTrueAndDeletedFalse(Pageable pageable);
    Page<Book> findByCategoryAndAvailableCopiesGreaterThan(String category, int count, Pageable pageable);
    Page<Book> findByDeletedFalse(Pageable pageable);
    
    // Common queries
    Optional<Book> findByTitleAndAuthorAndDeletedFalse(String title, String author);
    boolean existsByTitleAndAuthorAndDeletedFalse(String title, String author);
    Optional<Book> findByIdAndDeletedFalse(UUID id);
    
    /**
     * Finds a book by ID that is not deleted, with a write lock for optimistic locking.
     * This method should be used when you need to update the book to prevent concurrent modifications.
     *
     * @param id the ID of the book to find
     * @return the book if found and not deleted, empty otherwise
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b WHERE b.id = :id AND b.deleted = false")
    Optional<Book> findByIdAndDeletedFalseWithWriteLock(UUID id);
}
